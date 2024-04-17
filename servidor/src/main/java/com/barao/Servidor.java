package com.barao;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Servidor implements  Runnable{
    public static String secretKey;
    public Socket socketCliente;
    public Random random = new Random();
    public AES aes;
    private boolean conexao = true;
    private Banco banco = new Banco();
    byte[] salt = HashPassword.getSalt();

    private ObjectOutputStream saidaStream;

    private ObjectInputStream entradaStream;

    public Servidor(Socket cliente) throws NoSuchAlgorithmException {

        this.socketCliente = cliente;
        Cadastro cadastro = new Cadastro("1", "senha1", "00000000000", "nome", "endereco", "telefone");
        banco.addCadastro(cadastro);
        banco.addConta(new Conta("1", 0.0));
        cadastro = new Cadastro("2", "senha2", "11111111111", "nome", "endereco", "telefone");
        banco.addCadastro(cadastro);
        banco.addConta(new Conta("2", 0.0));
        cadastro = new Cadastro("3", "senha3", "22222222222", "nome", "endereco", "telefone");
        banco.addCadastro(cadastro);
        banco.addConta(new Conta("3", 0.0));
    }

    @Override
    public void run() {
        System.out.println("Cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

        try {
//            Scanner s = new Scanner(socketCliente.getInputStream());
            saidaStream = new ObjectOutputStream(socketCliente.getOutputStream());
            entradaStream = new ObjectInputStream(socketCliente.getInputStream());
            socketCliente.getPort();

            String[] mensagemArray;
            String[] info;
            String mensagemRecebida;
            String tag;
            String senha;
            Mensagem mensagem;

            mensagem = (Mensagem) entradaStream.readObject();
//            System.out.println(mensagem.getAcao() + " " + mensagem.getMensagem());
//            String[] mensagemChave = mensagem.getMensagem().split(",");
//            long p = Long.parseLong(mensagemChave[0]);
//            long g = Long.parseLong(mensagemChave[1]);
//            long b = new Random().nextInt(100);
//            long B = expMod(g, b, p);
//            secretKey = expMod(Long.parseLong(mensagemChave[2]), b, p) + "";
            secretKey = mensagem.getMensagem();
            System.out.println("Chave secreta: " + secretKey);
            aes = new AES(secretKey);
//            mensagem = new Mensagem("0", B + "");
//            saidaStream.writeObject(mensagem);
            while (conexao){
                mensagem = (Mensagem) entradaStream.readObject();
                System.out.println(socketCliente.getInetAddress().getHostAddress() + " : " + mensagem);
                if (mensagem.getAcao().equalsIgnoreCase("0")){
                    conexao = false;
                }
                System.out.println(mensagem.getAcao() + " " + mensagem.getMensagem());
                switch(mensagem.getAcao()){
                    case "1":
                        System.out.println("Sacar");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        info = mensagemArray[0].split(";");
                        String contaSaque = info[1];
                        Double valor = Double.parseDouble(info[0]);
                        boolean sacou = banco.sacar(contaSaque, valor);
                        if(sacou){
                            mensagem = new Mensagem("1", "Saque realizado");
                            saidaStream.writeObject(mensagem);
                        }else{
                            mensagem = new Mensagem("1", "Saldo insuficiente");
                            saidaStream.writeObject(mensagem);
                        }
                        break;
                    case "2":
                        System.out.println("Depositar");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        info = mensagemArray[0].split(";");
                        String contaDeposito = info[1];
                        valor = Double.parseDouble(info[0]);
                        boolean depositou = banco.depositar(contaDeposito, valor);
                        if(depositou){
                            mensagem = new Mensagem("2", "Deposito realizado");
                            saidaStream.writeObject(mensagem);
                        }else{
                            mensagem = new Mensagem("2", "Erro ao depositar");
                            saidaStream.writeObject(mensagem);
                        }
                        break;
                    case "3":
                        System.out.println("Transferir");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        info = mensagemArray[0].split(";");
                        String contaDestino = info[1];
                        String contaOrigem = info[2];
                        valor = Double.parseDouble(info[0]);
                        boolean transferiu = banco.transferir(contaOrigem, contaDestino, valor);
                        if(transferiu){
                            mensagem = new Mensagem("3", "Transferencia realizada");
                            saidaStream.writeObject(mensagem);
                        }else{
                            mensagem = new Mensagem("3", "Erro ao transferir");
                            saidaStream.writeObject(mensagem);
                        }
                        break;
                    case "4":
                        System.out.println("Saldo");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        String contaSaldo = mensagemArray[0];
                        Double saldo = banco.saldo(contaSaldo);
                        mensagem = new Mensagem("4", saldo + "");
                        saidaStream.writeObject(mensagem);
                        break;
                    case "5":
                        System.out.println("Investir");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        info = mensagemRecebida.split(";");
                        String contaInvestimento = info[1];
                        valor = Double.parseDouble(info[0]);
                        boolean investiu = banco.investir(contaInvestimento, valor);
                        if(investiu){
                            mensagem = new Mensagem("5", "Investimento realizado");
                            saidaStream.writeObject(mensagem);
                        }else{
                            mensagem = new Mensagem("5", "Erro ao investir");
                            saidaStream.writeObject(mensagem);
                        }
                        break;
                    case "8":
                        System.out.println("Cadastrar");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        info = mensagemArray[0].split(";");
                        String contaNova = random.nextInt(1000000) + "";
//                        senha = Hmac.hMac(secretKey, info[4]);
                        senha = HashPassword.getSenhaSegura(info[4], salt);
                        System.out.println("Senha Cadastro: " + senha);
                        Cadastro cadastro = new Cadastro(contaNova, senha, info[1], info[0], info[2], info[3]);
                        Conta conta = new Conta(contaNova, 0.0);
                        banco.addCadastro(cadastro);
                        banco.addConta(conta);
                        mensagem = new Mensagem("8", contaNova);
                        banco.listaConta();
                        banco.listaCadastro();
                        saidaStream.writeObject(mensagem);
                        break;
                    case "9":
                        System.out.println("Autenticar");
                        mensagemRecebida = aes.decifrar(mensagem.getMensagem());
                        mensagemArray = mensagemRecebida.split("/");
                        System.out.println(mensagemArray[0] + " " + mensagemArray[1]);
                        tag = Hmac.hMac(secretKey, mensagemArray[0]);
                        if(!tag.equals(mensagemArray[1])){
                            conexao = false;
                            mensagem = new Mensagem("9", "Mensagem corrompida");
                            saidaStream.writeObject(mensagem);
                            break;
                        }
                        info = mensagemArray[0].split(";");
//                        senha = Hmac.hMac(secretKey, info[1]);
                        senha = HashPassword.getSenhaSegura(info[1], salt);
                        System.out.println("Senha Autenticacao: " + senha);
                        String cadastroExiste = banco.checkCadastro(info[0], senha);
                        if(cadastroExiste.equals("autenticado")){
                            mensagem = new Mensagem("9", "Autenticado");
                            saidaStream.writeObject(mensagem);
                        }else if(cadastroExiste.equals("senha incorreta")){
                            mensagem = new Mensagem("9", "Senha incorreta");
                            saidaStream.writeObject(mensagem);
                        }else if(cadastroExiste.equals("conta não encontrada")){
                            mensagem = new Mensagem("9", "Usuario não encontrado");
                            saidaStream.writeObject(mensagem);
                        }

                        break;
                }

            }
            System.out.println("Cliente desconectado: " + socketCliente.getInetAddress().getHostAddress());
            saidaStream.close();
            entradaStream.close();
            socketCliente.close();
        }catch (IOException e){
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static long expMod(long a, long b, long p)
    {
        if (b == 1)
            return a;
        else
            return (((long)Math.pow(a, b)) % p);
    }


}

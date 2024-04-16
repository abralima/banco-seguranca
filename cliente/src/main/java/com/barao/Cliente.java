package com.barao;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

public class Cliente implements Runnable {
    private static String secretKey;
    private Socket cliente;
    private boolean logado = false;
    private boolean conexao = true;
    private final Double poupancaTaxa = 0.005;
    private final Double investimentoTaxa = 0.015;

    private ObjectOutputStream saida;

    private final RSA rsa = new RSA(5, 7);

    private ObjectInputStream entradaStream;

    public Cliente(Socket c){
        this.cliente = c;
        rsa.gerarChaves();
    }

    @Override
    public void run() {
        System.out.println("Conectado ao servidor");
        try {
            Scanner entrada = new Scanner(System.in);
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entradaStream = new ObjectInputStream(cliente.getInputStream());
            String mensagem;
            String mensagemFinalString;
            Mensagem mensagemFinal;
            Mensagem mensagemRecebida;
            StringBuilder sb = new StringBuilder();
            String conta = "";
            gerarChave();
            mensagemRecebida = (Mensagem) entradaStream.readObject();
            System.out.println("Mensagem recebida: " + mensagemRecebida.getMensagem());

            while (conexao){
                sb.setLength(0);
                if(logado){
                    System.out.println("Digite a ação: " + "\n" +
                            "1 - Sacar" + "\n" +
                            "2 - Depositar" + "\n" +
                            "3 - Transferir" + "\n" +
                            "4 - Saldo" + "\n" +
                            "5 - Investir" + "\n" +
                            "0 - Sair" + "\n");
                    mensagem = entrada.nextLine();
                    if(mensagem.equalsIgnoreCase("0")){
                        conexao = false;
                        mensagemFinal = new Mensagem("0", "");
                        saida.writeObject(mensagemFinal);
                        System.out.println("Desconectado do servidor");
                        break;
                    }
                    switch (mensagem){
                        case "1":
                            System.out.println("Digite o valor: ");
                            sb.append(entrada.nextLine()).append(";");
                            sb.append(conta);
                            mensagemFinalString = sb.toString();
                            mensagemFinal = construirMensagem("1", mensagemFinalString);
                            saida.writeObject(mensagemFinal);
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            System.out.println(mensagemRecebida.getMensagem());
                            break;
                        case "2":
                            System.out.println("Digite o valor: ");
                            sb.append(entrada.nextLine()).append(";");
                            sb.append(conta);
                            mensagemFinalString = sb.toString();
                            mensagemFinal = construirMensagem("2", mensagemFinalString);
                            saida.writeObject(mensagemFinal);
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            System.out.println(mensagemRecebida.getMensagem());
                            break;
                        case "3":
                            System.out.println("Digite o valor: ");
                            sb.append(entrada.nextLine()).append(";");
                            System.out.println("Digite a conta: ");
                            sb.append(entrada.nextLine()).append(";");
                            sb.append(conta);
                            mensagemFinalString = sb.toString();
                            mensagemFinal = construirMensagem("3", mensagemFinalString);
                            saida.writeObject(mensagemFinal);
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            System.out.println(mensagemRecebida.getMensagem());
                            break;
                        case "4":
                            mensagemFinal = construirMensagem("4", conta);
                            saida.writeObject(mensagemFinal);
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            System.out.println("Saldo: " + mensagemRecebida.getMensagem());
                            break;
                        case "5":
                            System.out.println("Digite o valor: ");
                            String valorInvestido = entrada.nextLine();
                            sb.append(valorInvestido).append(";");
                            sb.append(conta);
                            mensagemFinalString = sb.toString();
                            mensagemFinal = construirMensagem("5", mensagemFinalString);
                            saida.writeObject(mensagemFinal);
                            System.out.println("Simulacao de investimento: " + "\n" +
                                    "3 meses: " + (Double.parseDouble(valorInvestido) * Math.pow((1 + investimentoTaxa), 3)) + "\n" +
                                    "6 meses: " + (Double.parseDouble(valorInvestido) * Math.pow((1 + investimentoTaxa), 6)) + "\n" +
                                    "12 meses: " + (Double.parseDouble(valorInvestido) * Math.pow((1 + investimentoTaxa), 12)));
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            System.out.println("Investimento: " + mensagemRecebida.getMensagem());
                            break;
                    }
                }else{
                    System.out.println("Digite a ação: " + "\n" +
                            "8 - Cadastrar" + "\n" +
                            "9 - Entrar" + "\n" +
                            "0 - Sair" + "\n");
                    mensagem = entrada.nextLine();
                    if(mensagem.equalsIgnoreCase("0")){
                        conexao = false;
                        mensagemFinal = new Mensagem("0", "");
                        saida.writeObject(mensagemFinal);
                        System.out.println("Desconectado do servidor");
                        break;
                    }
                    switch (mensagem){
                        case "8":
                            System.out.println("Digite o nome: ");
                            sb.append(entrada.nextLine()).append(";");
                            System.out.println("Digite o cpf: ");
                            sb.append(entrada.nextLine()).append(";");
                            System.out.println("Digite o endereço: ");
                            sb.append(entrada.nextLine()).append(";");
                            System.out.println("Digite o telefone: ");
                            sb.append(entrada.nextLine()).append(";");
                            System.out.println("Digite a senha: ");
                            sb.append(entrada.nextLine());
                            mensagemFinalString = sb.toString();
                            mensagemFinal = construirMensagem("8", mensagemFinalString);

                            saida.writeObject(mensagemFinal);
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            System.out.println("Sua conta nova: " + mensagemRecebida.getMensagem());
                            break;
                        case "9":
                            System.out.println("Digite a conta: ");
                            conta = entrada.nextLine();
                            sb.append(conta).append(";");
                            System.out.println("Digite a senha: ");
                            sb.append(entrada.nextLine());
                            mensagemFinalString = sb.toString();
                            mensagemFinal = construirMensagem("9", mensagemFinalString);
                            saida.writeObject(mensagemFinal);
                            mensagemRecebida = (Mensagem) entradaStream.readObject();
                            if(mensagemRecebida.getMensagem().equalsIgnoreCase("Autenticado")){
                                logado = true;
                                System.out.println("Logado");
                            }else{
                                System.out.println(mensagemRecebida.getMensagem());
                            }
                            break;
                    }
                }


                System.out.println("Mensagem enviada");
                System.out.println("Aguardando resposta");
                Thread.sleep(1000);
            }
            entrada.close();
            saida.close();
            cliente.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public Mensagem construirMensagem(String acao, String mensagem) throws Exception {
        String tag = Hmac.hMac(secretKey, mensagem);
        AES aes = new AES(secretKey);

        String mensagemCifrada = aes.cifrar(mensagem + "/" + tag);
        System.out.println("Mensagem cifrada: " + mensagemCifrada);
        return new Mensagem(acao, mensagemCifrada);
    }

    private static long expMod(long a, long b, long p)
    {
        if (b == 1)
            return a;
        else
            return (((long)Math.pow(a, b)) % p);
    }

    // Driver code
    public void gerarChave() throws IOException, ClassNotFoundException {
        long p, g, A, a, B, b;
        Random random = new Random();
        p = random.nextInt(100) + 1;
        System.out.println("Valor de p: " + p);
        g = random.nextInt(10) + 1;
        a = random.nextInt(10) + 1;
        A = expMod(g, a, p);
        Mensagem mensagem = new Mensagem("10", p + "," + g + "," + A);
        saida.writeObject(mensagem);
        Mensagem mensagemRecebida = (Mensagem) entradaStream.readObject();
        B = Long.parseLong(mensagemRecebida.getMensagem());
        secretKey = String.valueOf(expMod(B, a, p));
        System.out.println("Chave secreta: " + secretKey);
    }
}

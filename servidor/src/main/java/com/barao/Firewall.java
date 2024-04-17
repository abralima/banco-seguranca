package com.barao;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Firewall implements Runnable {

    Socket cliente;
    Socket clienteServidor;
    ObjectInputStream entrada;
    ObjectInputStream entradaServidor;
    ObjectOutputStream saida;
    ObjectOutputStream saidaServidor;
    int errorCount = 0;
    int errorLimit = 3;
    long timeStart;
    long timeEnd;
    double timeWaiting = 10000;
    private boolean conexao = true;
    private String keyPublica;
    private String keyPrivada;

    public Firewall(Socket cliente) throws IOException {
        this.cliente = cliente;
        clienteServidor = new Socket("127.0.0.1", 9090);
    }


    @Override
    public void run() {
        System.out.println("Firewall rodando");
        Mensagem mensagem;
        try {
            entrada = new ObjectInputStream(cliente.getInputStream());
            saida = new ObjectOutputStream(cliente.getOutputStream());
            entradaServidor = new ObjectInputStream(clienteServidor.getInputStream());
            saidaServidor = new ObjectOutputStream(clienteServidor.getOutputStream());
            gerarChave();
            saidaServidor.writeObject(new Mensagem("Chave privada", keyPrivada));
            saida.writeObject(new Mensagem("Chave publica", keyPublica));
            while (conexao){
                mensagem = (Mensagem) entrada.readObject();
                System.out.println("Mensagem recebida: " + mensagem.getMensagem());

                if(errorCount == errorLimit){
                    timeEnd = System.currentTimeMillis();
                    if(timeEnd - timeStart < timeWaiting){
                        saida.writeObject(new Mensagem("Erro", "Aguarde um momento"));
                        continue;
                    } else {
                        errorCount = 0;
                    }
                }

                if(mensagem.getAcao().equals("0")){
                    saidaServidor.writeObject(mensagem);
                    conexao = false;
                }

                if(mensagem.getAcao().equals("10")){
                    saida.writeObject(new Mensagem("Erro", "Tentativa de acesso a banco de dados bloqueado pelo firewall"));
                    conexao = false;
                    saidaServidor.writeObject(new Mensagem("0", ""));
                    continue;
                }

                if(checkSqlInjection(mensagem.getMensagem())) {
                    saida.writeObject(new Mensagem("Erro", "SQL Injection detectado"));
                }

                saidaServidor.writeObject(mensagem);
                mensagem = (Mensagem) entradaServidor.readObject();

                if(checkIfReturnedError(mensagem)){
                    errorCount++;
                    if(errorCount == errorLimit){
                        timeStart = System.currentTimeMillis();
                        saida.writeObject(new Mensagem("Erro", "Limite de tentativas excedido"));
                    } else {
                        saida.writeObject(mensagem);
                    }
                } else {
                    saida.writeObject(mensagem);
                }


            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkIfReturnedError(Mensagem mensagem) {
        return mensagem.getMensagem().contains("incorreta");
    }

    private boolean checkSqlInjection(String mensagem){
        return mensagem.contains("select") || mensagem.contains("delete")
                || mensagem.contains("update") || mensagem.contains("insert");
    }

    private boolean checkIfActionIfValid(String acao){
        int acaoInt = Integer.parseInt(acao);
        return acaoInt >= 0 && acaoInt <= 9;
    }

    public void gerarChave() throws IOException, ClassNotFoundException {
        long p, g, A, a, B, b;
        Random random = new Random();
        p = random.nextInt(100) + 1;
        System.out.println("Valor de p: " + p);
        g = random.nextInt(10) + 1;
        a = random.nextInt(10) + 1;
        b = random.nextInt(10) + 1;
        A = expMod(g, a, p);
        B = expMod(g, b, p);
        keyPrivada = String.valueOf(expMod(B, a, p));
        keyPublica = String.valueOf(expMod(A, b, p));
        System.out.println("Chave secreta privada: " + keyPrivada);
        System.out.println("Chave secreta publica: " + keyPublica);
    }

    private static long expMod(long a, long b, long p)
    {
        if (b == 1)
            return a;
        else
            return (((long)Math.pow(a, b)) % p);
    }

}

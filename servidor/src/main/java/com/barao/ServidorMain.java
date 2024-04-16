package com.barao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorMain {
    public static void main(String[] args) throws Exception {
        ServerSocket socketServidor = null;
        try {
            socketServidor = new ServerSocket(9090);
            while (true){
                Socket cliente = socketServidor.accept();
                Servidor servidor = new Servidor(cliente);
                Thread t = new Thread(servidor);
                t.start();

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }

//        RSA rsa = new RSA(11, 13);
//        rsa.gerarChaves();
//
//        String mensagem = "ola";
//        long[] mensagemCriptografada = rsa.criptografar(mensagem);
//        System.out.println("Mensagem criptografada: ");
//        for (int i = 0; i < mensagemCriptografada.length; i++) {
//            System.out.print(mensagemCriptografada[i] + " ");
//        }
//        System.out.println();
//
//        String mensagemDescriptografada = rsa.descriptografar(mensagemCriptografada);
//        System.out.println("Mensagem descriptografada: " + mensagemDescriptografada);

    }
}
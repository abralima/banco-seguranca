package com.barao;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        ServerSocket socketServidor = null;
        try {
            socketServidor = new ServerSocket(9190);
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
    }
}
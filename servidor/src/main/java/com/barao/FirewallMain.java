package com.barao;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FirewallMain {
    public static void main(String[] args) throws IOException {
        ServerSocket socketServidor = null;
        try {
            socketServidor = new ServerSocket(9190);
            while (true){
                Socket cliente = socketServidor.accept();
                Firewall servidor = new Firewall(cliente);
                Thread t = new Thread(servidor);
                t.start();

            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
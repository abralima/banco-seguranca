package com.barao;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteMain {
    public static void main(String[] args) throws UnknownHostException, IOException {

        Socket socket = new Socket("127.0.0.1", 9190);
        InetAddress ip = InetAddress.getLocalHost();
        Cliente cliente = new Cliente(socket);
        Thread t = new Thread(cliente);
        t.start();
    }
}
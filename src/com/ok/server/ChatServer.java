package com.ok.server;


public class ChatServer {

    public static ClientList clientList;

    public static void RunServer(int port){


            System.out.println("Server Running");
            clientList = new ClientList();
            serverListener.StartListening(port);

    }
}

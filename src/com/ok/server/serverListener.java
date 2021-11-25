package com.ok.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class serverListener {

    public static ExecutorService threadPool;
    static void StartListening(int port)  {
        threadPool = Executors.newCachedThreadPool();

        try {
            ServerSocket socket = new ServerSocket(port);

            while (!socket.isClosed()) {
                try {
                    Socket s = socket.accept();
                    HandleSocket(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    static void HandleSocket(Socket s){
        ClientHandler ch = new ClientHandler(s);
        threadPool.execute(ch);
    }
}

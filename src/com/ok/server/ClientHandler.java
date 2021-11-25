package com.ok.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private Socket Sock;
    public ClientHandler(Socket sock ){
        Sock = sock;
    }
    @Override
    public void run() {
        try {
            System.out.println(" got here  ");

            InputStream     is = Sock.getInputStream();
            OutputStream    os = Sock.getOutputStream();
            BufferedReader  br = new BufferedReader(new InputStreamReader(is));
            BufferedWriter  bw = new BufferedWriter(new OutputStreamWriter(os));

            bw.write("OK working 100\n");
            bw.flush();;
            String in = br.readLine();
            System.out.println(" got " + in);

            Sock.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

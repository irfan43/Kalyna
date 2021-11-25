package com.ok.chatbox;

import java.io.*;
import java.net.Socket;

public class ClientConn {

    private Socket Sock;
    private String IP;
    private int Port;

    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedWriter Writer;
    private BufferedReader Reader;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;


    public ClientConn(String ip,int port){
        try {
            IP = ip;
            Port = port;
            Connect();






        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Login() throws IOException{
        Writer.write("LOGIN\n");
        Writer.write(ChatClient.username + "\n");
        Writer.flush();
    }

    private void Connect() throws IOException {
        Sock = new Socket(IP,Port);
        SetupStreams();
        Login();
    }

    private void SetupStreams() throws IOException {
        inputStream = Sock.getInputStream();;
        outputStream = Sock.getOutputStream();

        bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedOutputStream = new BufferedOutputStream(outputStream);

        Writer = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream));
        Reader = new BufferedReader(new InputStreamReader(bufferedInputStream));


    }
}

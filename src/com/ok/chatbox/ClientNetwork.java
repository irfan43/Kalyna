package com.ok.chatbox;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ClientNetwork implements Runnable {

    private Socket Sock;
    private String IP;
    private int Port;

    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedWriter Writer;
    private BufferedReader Reader;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;


    private final Object lock = new Object();
    public ClientNetwork(String ip, int port){
        IP = ip;
        Port = port;
    }
    public void Close() throws IOException {
        Sock.close();
    }
    public boolean SendPacket(byte[] pack,String Base64PublicKey) throws IOException{

        Connect();

        Writer.write("SEND\n");
        Writer.write(Base64PublicKey + "\n");
        Writer.write( Base64.getEncoder().encodeToString(pack) + "\n");
        Writer.flush();
        String resp = Reader.readLine();
        Sock.close();
        return resp.equals("ok");
    }
    public String GetPublicKey(String username) throws IOException {


        Connect();

        Writer.write("PBK\n");
        Writer.write( username + "\n");
        Writer.flush();
        boolean success = Reader.readLine().equals("GOT");
        String Base64PublicKey = null;
        if(success){
            Base64PublicKey = Reader.readLine();
        }

        Sock.close();

        return Base64PublicKey;
    }
    private void Login() throws IOException{
        Writer.write("LOGIN\n");
        Writer.write(ChatClient.username + "\n");
        Writer.write(
                Base64.getEncoder().encodeToString(ChatClient.chatCipher.getPublicKey().getEncoded()) + "\n"
        );

        Writer.flush();

    }

    private void Connect() throws IOException {
        Sock = new Socket(IP,Port);
        SetupStreams();
    }

    private void SetupStreams() throws IOException {
        inputStream = Sock.getInputStream();;
        outputStream = Sock.getOutputStream();

        bufferedInputStream = new BufferedInputStream(inputStream);
        bufferedOutputStream = new BufferedOutputStream(outputStream);

        Writer = new BufferedWriter(new OutputStreamWriter(bufferedOutputStream));
        Reader = new BufferedReader(new InputStreamReader(bufferedInputStream));


    }


    @Override
    public void run() {
        try {
            Connect();
            Login();
            while (!Sock.isClosed()) {
                String cmd = Reader.readLine();
                if (cmd.equals("new")) {
                    byte[] packet = Base64.getDecoder().decode(Reader.readLine());
                    ChatClient.packetHandler.QueueNewPacket(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

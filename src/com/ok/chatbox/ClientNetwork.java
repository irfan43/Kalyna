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
        Writer.write(pack.length + "\n");
        System.out.println(" len " + pack.length);
        Writer.flush();
        outputStream.write(pack);
        outputStream.flush();
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
                    int len = Integer.parseInt(Reader.readLine());
                    byte[] packet = ReadNBytes(len);
                    ChatClient.packetHandler.newPacketReceived(packet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private byte[] ReadNBytes( int n) throws IOException {
        byte[] out  = new byte[n];
        int pos     = 0;

        while (pos < n){
            byte[] tmp  = new byte[Math.min((n - pos),bufferedInputStream.available())];
            int len     = bufferedInputStream.read(tmp);

            System.arraycopy(tmp,0,out,pos,len);
            pos += len;
        }
        return out;
    }
}

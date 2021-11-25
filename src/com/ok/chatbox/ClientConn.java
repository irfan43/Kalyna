package com.ok.chatbox;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClientConn implements Runnable {

    private Socket Sock;
    private String IP;
    private int Port;

    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedWriter Writer;
    private BufferedReader Reader;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;

    private Queue<byte[]> PacketQue;

    private final Object lock = new Object();
    public ClientConn(String ip,int port){
        IP = ip;
        Port = port;
    }
    public void Close() throws IOException {
        Sock.close();
    }
    public boolean SendPacket(byte[] pack,String Base64PublicKey) throws IOException{
        if(Sock.isConnected())
            throw new IllegalArgumentException("Socket Already Connected");
        Connect();

        Writer.write("SEND\n");
        Writer.write(Base64PublicKey + "\n");
        Writer.write(pack.length + "\n");
        Writer.flush();
        bufferedOutputStream.write(pack);
        bufferedOutputStream.flush();
        String resp = Reader.readLine();
        Sock.close();
        return resp.equals("ok");
    }
    public String GetPublicKey(String username) throws IOException {
        if(Sock.isConnected())
            throw new IllegalArgumentException("Socket Already Connected");

        Connect();

        Writer.write("PBK\n");
        Writer.write( username + "\n");
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

    public boolean PacketAvailable(){
        if(PacketQue == null)
            throw new IllegalArgumentException(" Calling Packet Available when Packet Listener not initialized ");
        synchronized (lock){
            return !PacketQue.isEmpty();
        }
    }

    public byte[] GetNextPacket(){
        if(PacketQue == null)
            throw new IllegalArgumentException(" Calling Get Packet when Packet Listener not initialized ");
        byte[] data = null;
        if(PacketAvailable()){
            synchronized (lock){
                data = PacketQue.poll();
            }
        }
        return data;
    }

    @Override
    public void run() {
        try {
            Connect();
            Login();
            PacketQue = new LinkedList<>();
            while (!Sock.isClosed()) {
                String cmd = Reader.readLine();
                if (cmd.equals("new")) {
                    int len = Integer.parseInt(Reader.readLine());
                    byte[] packet = ReadNBytes(len);
                    synchronized (lock) {
                        PacketQue.add(packet);
                    }
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

package com.ok.server;


import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ConnectedClient {

    private BufferedWriter Writer;
    private BufferedReader Reader;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    private Socket Sock;

    public ConnectedClient(Socket sock) throws IOException {
        Sock = sock;
        Writer = new BufferedWriter(new OutputStreamWriter( Sock.getOutputStream()));
        Reader = new BufferedReader(new InputStreamReader( Sock.getInputStream() ));

        bufferedInputStream = new BufferedInputStream(Sock.getInputStream());
        bufferedOutputStream = new BufferedOutputStream(Sock.getOutputStream());

    }

    public boolean SendPacket(byte[] Packet) throws IOException {
        if(Sock.isClosed())
            return false;
        Writer.write("new\n");
        Writer.write(Base64.getEncoder().encodeToString(Packet) + "\n");
        Writer.flush();
        return true;
    }
    public void Close() throws IOException {
        Sock.close();
    }
}

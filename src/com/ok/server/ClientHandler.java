package com.ok.server;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ClientHandler implements Runnable{

    private final Socket Sock;
    private BufferedReader br;
    private BufferedWriter bw;

    public ClientHandler(Socket sock ){
        Sock = sock;
    }

    @Override
    public void run() {
        try {

            InputStream is = Sock.getInputStream();
            OutputStream os = Sock.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
            bw = new BufferedWriter(new OutputStreamWriter(os));

            String command = br.readLine();
            switch (command){
                case "LOGIN":
                    login();
                    break;
                case "SEND":
                    sendPacket();
                    break;
                case "PBK":
                    getPBK();
                    break;
                case "USR":
                    getUsername();
                    break;
                default:
                    invalidCommand();
                    break;
            }

        }catch (IOException e){
            e.printStackTrace();
            if(!Sock.isClosed()) {
                try {
                    Sock.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets the Username of a Connected Client given the Public Key
     * @throws IOException if a IOException Occurs
     */
    private void getUsername() throws IOException{
        String Base64PublicKey  = br.readLine();
        String username         = ChatServer.clientList.getUsername(Base64PublicKey);

        if(username == null){
            bw.write("INVALID\n");
        }else {
            bw.write("GOT\n");
            bw.write(username + "\n");
        }
        bw.flush();
        Sock.close();
    }

    /**
     * Gets the public Key of a Connected Client given the username
     * @throws IOException if a IOException Occurs
     */
    private void getPBK() throws IOException{
        String username     = br.readLine();
        String pub          = ChatServer.clientList.getPBK(username);

        if(pub == null){
            bw.write("DNE\n");
        }else {
            bw.write("GOT\n");
            bw.write(pub + "\n");
        }
        bw.flush();
        Sock.close();
    }

    /**
     * Informs the connected Client that the command received was not recognized
     * @throws IOException if an IOException occurs
     */
    private void invalidCommand() throws IOException {
        bw.write("INVALID\n");
        bw.flush();
        Sock.close();
    }

    /**
     * Sends a Packet in to a connected user
     * @throws IOException if an IOException occurs
     */
    private void sendPacket() throws IOException {
        try {
            String Base64PublicKey = br.readLine();
            Base64.getDecoder().decode(Base64PublicKey);

            byte[] packetData = Base64.getDecoder().decode(br.readLine());
            boolean successes = ChatServer.clientList.SendPacket(packetData,Base64PublicKey);

            if(successes) {
                bw.write("ok\n");
            }   else {
                bw.write("FAIL\n");
            }
            bw.flush();
        }catch (IllegalArgumentException e){
            invalidCommand();
        }
    }

    /**
     * Grabs the Username and Public Key from the Socket
     * This is stored in the <code>clientList</code> along with the socket
     * @throws IOException if an IOException occurs
     */
    private void login() throws IOException{
        String username = br.readLine();
        String Base64PublicKey = br.readLine();

        System.out.println(" new User login " + username);
        ChatServer.clientList.LoginClient(Sock,Base64PublicKey,username);

    }


}

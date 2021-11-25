package com.ok.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ClientList {

    //          Base64 PBK to Objects
    private HashMap<String,ConnectedClient> ConnectedList;
    //          Usernames to Base 64 PBK
    private HashMap<String,String> usernameMap;
    public ClientList(){
        ConnectedList = new HashMap<>();
        usernameMap = new HashMap<>();
    }
    public void LoginClient(Socket s,String pbk, String username) throws IOException {
        ConnectedClient cc = new ConnectedClient(s);
        usernameMap.put(username,pbk);
        ConnectedList.put(pbk,cc);
    }

    public String getPBK(String username){
        if( usernameMap.containsKey(username) )
            return usernameMap.get(username);
        return null;
    }

    public boolean SendPacket(byte[] packet, String to) throws IOException {
        if ( !ConnectedList.containsKey(to) )
            return false;

        return ConnectedList.get(to).SendPacket(packet);
    }
}

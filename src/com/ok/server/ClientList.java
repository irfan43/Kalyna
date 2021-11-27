package com.ok.server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ClientList {

    //          Base64 PBK to Objects
    private HashMap<String,ConnectedClient> ConnectedList;
    //          Usernames to Base 64 PBK
    private HashMap<String,String> usernameMap;
    //          Base64 PBK to Username
    private HashMap<String,String> PBKMap;

    public ClientList(){
        ConnectedList = new HashMap<>();
        usernameMap = new HashMap<>();
        PBKMap = new HashMap<>();
    }
    public void LoginClient(Socket s,String pbk, String username) throws IOException {
        ConnectedClient cc = new ConnectedClient(s);
        usernameMap.put(username,pbk);
        PBKMap.put(pbk,username);
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

    public String getUsername(String base64PublicKey) {
        if( PBKMap.containsKey(base64PublicKey))
            return PBKMap.get(base64PublicKey);
        return null;
    }
}

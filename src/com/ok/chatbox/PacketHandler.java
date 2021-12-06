package com.ok.chatbox;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PacketHandler {




    private final List<ChatPacket> PacketList;
    private final List<ChatPacket> INITPacketList;
    public final Object lockPacketList = new Object();
    public final Object lockINITPacketList = new Object();

    public PacketHandler(){
        PacketList = new ArrayList<>();
        INITPacketList = new ArrayList<>();
    }


    public ChatPacket GetINITPacketFrom(String base64PublicKey){
        ChatPacket cp = null;
        synchronized (lockINITPacketList) {
            for (ChatPacket i : INITPacketList) {
                if (i.getFrom().equals(base64PublicKey)) {
                    cp = i;
                    INITPacketList.remove(cp);
                    break;
                }
            }
        }
        return cp;
    }
    public ChatPacket GetPacketFrom(String base64PublicKey){
        ChatPacket cp = null;

        synchronized (lockPacketList) {
            for (ChatPacket i : PacketList) {
                if (i.getFrom().equals(base64PublicKey)) {
                    cp = i;
                    PacketList.remove(cp);
                    break;
                }
            }
        }
        return cp;
    }

    public void SendMessagePacket(String base64PublicKey,byte[] data) throws IOException{
        ChatPacket cp = new ChatPacket(
                ChatPacket.TYPE_MESSAGE,
                ChatClient.chatCipher.getPublicKeyBase64(),
                data
        );
        byte[] pack = cp.getBytes();
        ChatClient.rest.SendPacket(pack,base64PublicKey);
    }

    public void SendPacketINIT(String base64PublicKey,byte[] pubByte, byte[] sign) throws IOException{
        String pubB64 = Base64.getEncoder().encodeToString(pubByte);
        String signB64 = Base64.getEncoder().encodeToString(sign);
        String mst = pubB64 + "\n" + signB64 + "\n";

        ChatPacket cp = new ChatPacket(
                ChatPacket.TYPE_INITIATOR,
                ChatClient.chatCipher.getPublicKeyBase64(),
                mst.getBytes(StandardCharsets.UTF_8)
        );

        ChatClient.rest.SendPacket( cp.getBytes() , base64PublicKey );
    }

    /**
     * This Queue new packets that have been sent to the Client from the server
     * @param packet The received packet
     * @throws IOException if an IOException occurs
     */
    public void QueueNewPacket(byte[] packet) throws IOException {
        ChatPacket cp = new ChatPacket(packet);
        if(cp.getType().equals(ChatPacket.TYPE_INITIATOR) ){
            synchronized (lockINITPacketList) {
                INITPacketList.add(cp);
            }
        }else{
            synchronized (lockPacketList) {
                PacketList.add(cp);
            }
        }

        //we notify to Connector thread who is waiting for new Packets
        //This will make it check the current packet queue and see if it's this added packet is needed
        if(UserList.connector != null) {
            synchronized (UserList.connector.lock) {
                UserList.connector.lock.notify();
            }
        }
    }
}

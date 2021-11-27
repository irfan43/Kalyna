package com.ok.chatbox;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PacketHandler {




    private final List<ChatPacket> PacketList;
    private final List<ChatPacket> INITPacketList;

    public PacketHandler(){
        PacketList = new ArrayList<>();
        INITPacketList = new ArrayList<>();
    }


    public ChatPacket GetINITPacketFrom(String base64PublicKey){
        ChatPacket cp = null;

        for (ChatPacket i : INITPacketList) {
            if(i.getFrom().equals(base64PublicKey)){
                cp = i;
                INITPacketList.remove(cp);
                break;
            }
        }
        return cp;
    }
    public ChatPacket GetPacketFrom(String base64PublicKey){
        ChatPacket cp = null;

        for (ChatPacket i : PacketList) {
            if(i.getFrom().equals(base64PublicKey)){
                cp = i;
                PacketList.remove(cp);
                break;
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
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(pubByte.length);
        os.write(pubByte);
        os.write(sign.length);
        os.write(sign);

        ChatPacket cp = new ChatPacket(
                ChatPacket.TYPE_INITIATOR,
                ChatClient.chatCipher.getPublicKeyBase64(),
                os.toByteArray()
        );
        byte[] pack = cp.getBytes();
        ChatClient.rest.SendPacket(pack,base64PublicKey);
    }
    public void newPacketReceived(byte[] packet) throws IOException {
        ChatPacket cp = new ChatPacket(packet);
        if(cp.getType().equals(ChatPacket.TYPE_INITIATOR) ){
            INITPacketList.add(cp);
        }else{
            PacketList.add(cp);
        }

    }
}

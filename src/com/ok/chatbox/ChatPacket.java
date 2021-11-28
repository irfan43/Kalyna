package com.ok.chatbox;

import java.io.*;
import java.util.Base64;

public class ChatPacket {

    public static final String TYPE_INITIATOR = "INIT";
    public static final String TYPE_MESSAGE = "MSG";
    public static final String TYPE_TEST = "ALL OK";

    /*
     * any packet
     *   packet type
     *   packet from PBK
     *   packet Raw Data
     *
     *
     * packet init
     *   "INIT"
     *   "senders PBK base 64"
     *   [DH PBK]
     *   [RSA sign of the DH PBK]
     *
     * package MSG
     *
     *
     *
     * */

    private String Type;
    private String From;
    private byte[] data;

    public ChatPacket(String type,String sender,byte[] data) {
        switch (type){
            case TYPE_INITIATOR:
            case TYPE_MESSAGE:
            case TYPE_TEST:
                break;
            default:
                throw new IllegalArgumentException("Invalid type " + type);
        }
        Type = type;
        From = sender;
        this.data = data;

    }
    public ChatPacket(byte[] packet) throws IOException{
        data = DecodePacket(packet);
    }

    private byte[] DecodePacket(byte[] packet) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(packet);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        Type = br.readLine();
        From = br.readLine();

        return  Base64.getDecoder().decode(br.readLine());
    }
    public byte[] getBytes() throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

        bw.write(Type);
        bw.newLine();
        bw.write(From);
        bw.newLine();
        bw.write(Base64.getEncoder().encodeToString(data));
        bw.newLine();
        bw.flush();

        return os.toByteArray();
    }
    public String getFrom(){
        return From;
    }
    public String getType(){
        return Type;
    }
    public byte[] getData(){
        return data;
    }


}

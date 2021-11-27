package com.ok.chatbox;

import java.io.*;

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
        return readBytes(is,is.available());
    }
    public byte[] getBytes() throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));

        bw.write(Type);
        bw.newLine();
        bw.write(From);
        bw.newLine();
        bw.flush();
        os.write(data);

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
    private static byte[] readBytes(InputStream is, int n) throws IOException {
        byte[] out  = new byte[n];
        int pos     = 0;

        while (pos < n){
            byte[] tmp  = new byte[Math.min((n - pos),is.available())];
            int len     = is.read(tmp);

            System.arraycopy(tmp,0,out,pos,len);
            pos += len;
        }
        return out;
    }

}
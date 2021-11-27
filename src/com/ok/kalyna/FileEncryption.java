package com.ok.kalyna;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class FileEncryption {


//    public static void main(String[] args) {
//        int mode = Kalyna.KALYNA_256KEY_256BLOCK;
//        if(args.length != 4 ) {
//            printMan();
//        }else if(!(args[0].equals("-e") || args[0].equals("-d"))){
//            printMan();
//        }else {
//           FileEncrypt(
//                   getKey(args[3],Kalyna.getKeySize(mode)),
//                   Path.of(args[1]),
//                   Path.of(args[2]),
//                   32000,args[0].equals("-e"),
//                   mode
//           );
//        }
//    }

    public static byte[] getKey(char[] password,int size) {
        byte[] bin = new byte[password.length];
        for (int i = 0; i < bin.length; i++)
            bin[i] = (byte) password[i];

        return KalynaHash.Hash(bin,size);
    }


    public static void FileEncrypt(byte[] key,Path input, Path output,int bufferSize,boolean encryption,int mode){
        try(
                BufferedInputStream     bis = new BufferedInputStream(Files.newInputStream(input));
                BufferedOutputStream    bos = new BufferedOutputStream(Files.newOutputStream(output))
        ){

            KalynaCFB kalynaCFB;
            if(encryption){
                kalynaCFB = new KalynaCFB(key,mode);
                bos.write(kalynaCFB.getIV());
                bos.write(kalynaCFB.getSALT());
            }else {
                //TODO maybe store mode here too
                byte[] iv       = readBytes( bis,Kalyna.getBlockSize(mode));
                byte[] salt     = readBytes( bis,Kalyna.getKeySize(mode));
                kalynaCFB       = new KalynaCFB(key,mode,iv,salt);
            }
            long lastReport = System.currentTimeMillis();
            int lastPos = bis.available();
            while (bis.available() != 0) {

                if(System.currentTimeMillis() - lastReport > 2000) {

                    int pos = bis.available();
                    int speed = (lastPos - pos)/2;
                    int time = pos/speed;
                    System.out.println(" remaining " + HumanReadableSize(bis.available()));
                    System.out.println(" speed     " + HumanReadableSize( speed ) + "/s");
                    System.out.println(" time      " +  time );


                    lastPos = bis.available();
                    lastReport = System.currentTimeMillis();
                }
                byte[] buf  = new byte[Math.min(bufferSize, bis.available())];
                int len     = bis.read(buf);

                bos.write(kalynaCFB.Update(Arrays.copyOf(buf, len)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private static void printMan() {
        System.out.println("Encryption");
        System.out.println("-e input_file output_file password");
        System.out.println("Decryption");
        System.out.println("-d input_file output_file password");
        //TODO add mode option later
    }
    public static String HumanReadableSize(int bytes){
        String[] units = {"bytes","KB","MB","GB","TB"};
        float size = bytes;
        int index;
        for (index = 0; index < units.length && size > 1024; index++) {
            size = size/1024F;
        }
        return String.format("%.2f",size) + " " + units[index];
    }
}

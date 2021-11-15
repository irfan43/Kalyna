package com.ok.kalyna;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileEncryption {


    public static void main(String[] args) {
        if(args.length != 4 ) {
            printMan();
        }else if(!(args[0].equals("-e") || args[0].equals("-d"))){
            printMan();
        }else {
           FileEncrypt(getKey(args[3]),Path.of(args[1]),Path.of(args[2]),4096,args[0].equals("-e"),Kalyna.KALYNA_512KEY_512BLOCK);
        }
    }

    private static byte[] getKey(String arg) {
        byte[] tmp = new byte[64];
        for (int i = 0; i < 64; i++)
            tmp[i] = (byte) i;

        return tmp;
    }

    public static void FileEncrypt(byte[] key,Path input, Path output,int bufferSize,boolean encryption,int mode){

        try(
                BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(input));
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(output))
        ){

            KalynaCFB kalynaCFB;
            if(encryption){
                kalynaCFB = new KalynaCFB(key,mode);
                bos.write(kalynaCFB.getIV());
                bos.write(kalynaCFB.getSALT());
            }else {
                //TODO maybe store mode here too
                byte[] iv = readBytes( bis,Kalyna.getBlockSize(mode));
                byte[] salt = readBytes( bis,Kalyna.getKeySize(mode));
                kalynaCFB = new KalynaCFB(key,mode,iv,salt);
            }

            while (bis.available() != 0) {
                int len = bufferSize;
                if (len < bis.available())
                    len = bis.available();
                byte[] buf = new byte[len];
                len = bis.read(buf);
                buf = kalynaCFB.Update(Arrays.copyOf(buf, len));
                bos.write(buf);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readBytes(InputStream is, int n) throws IOException {
        byte[] out = new byte[n];
        int pos = 0;
        while (pos < n){
            int len = is.available();
            if(len > (n - pos))
                len = (n - pos);

            byte[] tmp = new byte[len];
            len = is.read(tmp);

            System.arraycopy(tmp,0,out,pos,len);
            pos += len;
        }
        return out;
    }

    private static void printMan() {
        System.out.println("MANUAL");
        //TODO make a manual
    }
}

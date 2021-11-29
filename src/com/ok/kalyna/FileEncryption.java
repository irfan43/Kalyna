package com.ok.kalyna;

import com.ok.chatbox.ConsoleUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;


public class FileEncryption {

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
            long size = -1;
            KalynaCFB kalynaCFB;
            if(encryption){
                size = getFileSize(input);
                if(key == null)
                    key = getKey( getPassword() , Kalyna.getKeySize(mode));
                kalynaCFB = new KalynaCFB(key,mode);
                bos.write(  getEncoded(mode)            );
                bos.write(  getEncoded(size)            );
                bos.write(  kalynaCFB.getIV()           );
                bos.write(  kalynaCFB.getSALT()         );
            }else {
                byte[] modeEncoded  = readBytes( bis, 4 );
                byte[] sizeEncoded  = readBytes( bis,8 );

                size                = ByteBuffer.wrap(sizeEncoded).getLong();
                int m               = ByteBuffer.wrap(modeEncoded).getInt();
                if(mode != -1 && m != mode)
                    throw new IllegalArgumentException("Given mode mismatched");
                mode                = m;

                byte[] iv           = readBytes( bis,Kalyna.getBlockSize(mode)  );
                byte[] salt         = readBytes( bis,Kalyna.getKeySize(mode)    );
                if(key == null)
                    key = getKey( getPassword() , Kalyna.getKeySize(mode));
                kalynaCFB           = new KalynaCFB(key,mode,iv,salt);
            }
            long lastReport         = 0;
            long lastPos            = size;
            long pos                = size;
            byte[] mac              = new byte[Kalyna.getKeySize(mode)];
            int refreshTime         = 500;
            ConsoleUtil.CLS();
            while (pos > 0) {

                if(System.currentTimeMillis() - lastReport > refreshTime) {
                    System.out.print(ConsoleUtil.ANSI_CURS_HOME);
                    int time;
                    int speed   = (int)( (lastPos - pos)/(refreshTime/1000F) );
                    if (speed != 0)
                        time = (int) (pos/speed) ;
                    else
                        time = -1;
                    long rem = (size - pos);
                    System.out.print( " ".repeat(80)  + "\r");
                    System.out.println(" Remaining " + HumanReadableSize(pos));
                    System.out.print( " ".repeat(80)  + "\r");
                    System.out.println(" Speed     " + HumanReadableSize( speed ) + "/s");
                    System.out.print( " ".repeat(80)  + "\r");
                    System.out.println(" Time      " + HumanReadableTime( time  ) );

                    ConsoleUtil.ProgressBar(((float)(rem)/(float) size), "Progress" , 40  );
                    lastReport = System.currentTimeMillis();
                    lastPos = pos;
                }
                if(pos < bufferSize)
                    bufferSize = (int) pos;

                byte[] buf  = new byte[ Math.min(bufferSize, bis.available()) ];


                int len     = bis.read(buf);

                byte[] data = Arrays.copyOf(buf, len);

                pos -= len;

                bos.write(kalynaCFB.Update( data ));
            }
            ConsoleUtil.CLS();
            if(encryption){
                bos.write(kalynaCFB.getMAC());
                System.out.println(ConsoleUtil.ANSI_GREEN + " KALYNA ENCRYPTION SUCCESS " + ConsoleUtil.ANSI_RESET );
            }else {
                mac = bis.readNBytes(mac.length);
                if( Arrays.equals(mac,kalynaCFB.getMAC())){
                    System.out.println(ConsoleUtil.ANSI_GREEN + " KALYNA MAC VERIFIED OK " + ConsoleUtil.ANSI_RESET );
                }else {
                    System.out.println(ConsoleUtil.ANSI_RED + " !!!KALYNA MAC VERIFIED BAD!!! " + ConsoleUtil.ANSI_RESET );
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] getEncoded(int a) {
        return ByteBuffer.allocate(4).putInt(a).array();
    }

    private static long getFileSize(Path input) throws IOException {
        return FileChannel.open(input).size();
    }

    private static byte[] getEncoded(long a) throws IOException {
        return ByteBuffer.allocate(8).putLong(a).array();
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

    private static String HumanReadableTime(int time) {
        // 2 hours 35 mins 34 seconds
        // 2:35:34

        int secs    = time;
        int min     = secs/60;
        int hour    = min/60;
        int day     = hour/24;

        secs    = secs % 60;
        min     = min % 60;
        hour    = hour % 24;

        StringBuilder sb = new StringBuilder();
        if(day > 0)
            sb.append(day).append(" days ");
        if(hour > 0 )
            sb.append(secs).append(" hours ");
        if(min > 0)
            sb.append(secs).append(" minutes ");
        sb.append(secs).append(" secs ");
        return sb.toString();
    }


    public static String HumanReadableSize(long bytes){
        String[] units = {"bytes","KB","MB","GB","TB"};
        float size = bytes;
        int index;
        for (index = 0; index < units.length && size > 1024; index++) {
            size = size/1024F;
        }
        return String.format("%.2f",size) + " " + units[index];
    }

    public static char[] getPassword() {
        System.out.print("Enter Password :- ");
        Console con = System.console();
        if(con == null){
            Scanner s = new Scanner(System.in);
            String rtr = s.nextLine();
            return rtr.toCharArray();
        }
        return con.readPassword();
    }
}

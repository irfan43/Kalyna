package com.ok.kalyna;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ToDelete {

    public static void main(String[] args) {
        byte[] box = new byte[256];
        Scanner scn = new Scanner(System.in);
        List<String> elems = new ArrayList<>();

        StringBuilder built = new StringBuilder();
        while(true) {
            String tmp = scn.nextLine();
            if(tmp.equals(""))
                break;
            tmp = tmp.toUpperCase();
            char[] ar = tmp.toCharArray();
            for(char a: ar){
                if( (a >= '0' && a <= '9') || (a >= 'A' && a <= 'F') ){
                    built.append(a);
                }else{
                    elems.add(built.toString());
                    built = new StringBuilder();
                }
            }
            elems.add(built.toString());
            built = new StringBuilder();
        }

        System.out.println("{");
        for (int i = 0;i < 256;i++) {
            String e = elems.get(i);
            System.out.print(" (byte)0x" + e + ", ");
            if(i%16 == 15)
                System.out.println();
        }
    }
}

package com.ok.kalyna;

import java.util.Random;

public class ToDelete {

    public static void main(String[] args) {
        String[] parts = {" 6" ," 7", " 8", " 9", "10", "11", "12", "13"};
        for (int i = 0; i < 8; i++) {
            System.out.print("{ (byte) ");

            for (int j = 0; j < 8 ; j++) {
                System.out.print( parts[(i + j)%8] );
                if(j != 7)System.out.print(", (byte) ");
            }
            System.out.println(" },");
        }

    }
}

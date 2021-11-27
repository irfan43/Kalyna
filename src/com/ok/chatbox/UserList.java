package com.ok.chatbox;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class UserList {

    private static final Scanner scn = new Scanner(System.in);
    public static void showUserList(){
        String err_msg = "";
        while (true) {

            System.out.println(" -- chat with -- ");
            System.out.print(err_msg);
            System.out.println(" Enter Username :-  (\"exit\" to exit)");
            String username = scn.nextLine();
            if(username.equals("exit"))
                System.exit(0);
            try {
                String PBK = ChatClient.rest.GetPublicKey(username);
                System.out.println("got PBK");
                if(PBK != null){
                    ChatConnector.ChatWith(username,PBK);
                }else{
                    err_msg = " invalid Username \n";
                }
            } catch (IOException e) {
                e.printStackTrace();
                err_msg = "IO Exception while getting Public Key of " + username + "\n";
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                err_msg = "NO Such Algo Exception while " + e.getLocalizedMessage() +  "  \n";
            }
        }
    }
}

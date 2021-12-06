package com.ok.chatbox;

import com.ok.kalyna.KalynaHash;

import java.io.IOException;
import java.util.Base64;
import java.util.Scanner;

public class UserList {

    private static final Scanner scn = new Scanner(System.in);

    public static ChatConnector connector;

    public static void showUserList(){
        String err_msg = "";

        byte[] pbkHash = KalynaHash.Hash(
                ChatClient.chatCipher.getPublicKey().getEncoded(), 16
            );

        String hashB64 = Base64.getEncoder().encodeToString(pbkHash);


        while (true) {

            ConsoleUtil.CLS();

            System.out.println("\nPublic Key Hash " +  hashB64);
            System.out.println(" -- chat with -- ");
            System.out.print(err_msg);
            System.out.println(" Enter Username :-  (\"exit\" to exit)");

            String username = scn.nextLine();

            if(username.equals("exit"))
                System.exit(0);

            try {
                err_msg = OpenChatConsole(username);
            } catch (IOException e) {
                e.printStackTrace();
                err_msg = "IO Exception while getting Public Key of " + username + "\n";
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static String OpenChatConsole(String theirUsername) throws IOException, InterruptedException {
        String err_msg = "";
            String PBK = ChatClient.rest.GetPublicKey(theirUsername);
            if(PBK != null){
                System.out.println("got PBK");
                byte[] hash = KalynaHash.Hash( Base64.getDecoder().decode(PBK),16 );
                System.out.println("Hash(PBK) = " + Base64.getEncoder().encodeToString(hash));
                System.out.println("continue? (y/n)");
                if(scn.nextLine().toLowerCase().charAt(0) == 'y'){
                    connector = new ChatConnector(theirUsername,PBK);
                    connector.Open();
                }
            }else{
                err_msg = " invalid Username \n";
            }
        return err_msg;
    }
}

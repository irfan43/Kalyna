package com.ok.chatbox;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class ChatConsole implements Runnable{

    /*
    - Position the Cursor:
  \033[<L>;<C>H
     Or
  \033[<L>;<C>f
  puts the cursor at line L and column C.
- Move the cursor up N lines:
  \033[<N>A
- Move the cursor down N lines:
  \033[<N>B
- Move the cursor forward N columns:
  \033[<N>C
- Move the cursor backward N columns:
  \033[<N>D

- Clear the screen, move to (0,0):
  \033[2J
- Erase to end of line:
  \033[K

- Save cursor position:
  \033[s
- Restore cursor position:
  \033[u
  */
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_CURS_LEFT = "\u001B[1D";
    public static final String ANSI_CURS_UP = "\u001B[1F";
    public static final String ANSI_CURS_DOWN = "\u001B[1E";
    public static final String ANSI_CURS_ERASE = "\u001B[0J";

    public static final boolean IsWindows = System.getProperty("os.name").contains("Windows");
    private List<String> messages;
    private int messagesDrawn;
    private String Screen = "";
//    private String message;
    public StringBuilder msg;
    public boolean reDraw;
    private final Object lock = new Object();

    public ChatConsole(){

    }

    public void AddMessage(String message){
        synchronized (lock) {
            messages.add(message);
            reDraw = true;
        }
    }

    @Override
    public void run() {
        msg = new StringBuilder(ChatClient.username + ":- ");
        messages = new ArrayList<>();
        messagesDrawn = 0;
//        message = "";
        reDraw = true;
        CLS();
        while (true){
            if(reDraw) {
                Draw();
            }
            int keycode = 0;
            try {
                keycode = RawConsoleInput.read(false);
                if(keycode >= 0){
                    if(keycode == 3){
                        System.out.println("Ctrl + C Hard Exiting");
                        System.exit(-1);
                    }else if(keycode == 1){
                        break;
                    }else if(keycode >= 32 && keycode <= 126){
                        char t = (char) keycode;
                        msg.append(t);
                        System.out.print(t);
                    }else if((keycode == 127 && !IsWindows) ||
                            (keycode == 8 && IsWindows)){
                        if(msg.length() > (ChatClient.username + ":- ").length()) {
//                            reDraw = true;
                            System.out.print(ANSI_CURS_LEFT);
                            System.out.print(" ");
                            System.out.print(ANSI_CURS_LEFT);

                            msg.delete(msg.length() - 1,msg.length());
                        }
                    }else if( (keycode == 10 && !IsWindows) || (keycode == 13 && IsWindows) ) {
                        reDraw = true;
                        AddMessage(msg.toString());
                        msg = new StringBuilder(ChatClient.username + ":- ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void Draw(){
        synchronized (lock) {
//            CLS();
            System.out.print(ANSI_CURS_UP);
            System.out.print(ANSI_CURS_ERASE);
            for (int i = messagesDrawn; i < messages.size(); i++) {
                String m = messages.get(i);
                System.out.println(m);
            }
            System.out.print(ANSI_CURS_ERASE);
            System.out.print(ANSI_CURS_DOWN);
            messagesDrawn = messages.size();
            reDraw = false;
            System.out.print( msg.toString());
        }
    }

    public void CLS(){
        if(IsWindows){
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                System.out.println("Error While Trying to Clear Console");
            }
        }else {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }
}

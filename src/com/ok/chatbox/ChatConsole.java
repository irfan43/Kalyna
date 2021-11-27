package com.ok.chatbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatConsole implements Runnable{

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
        ConsoleUtil.CLS();
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
                    }else if((keycode == 127 && !ConsoleUtil.IsWindows) ||
                            (keycode == 8 && ConsoleUtil.IsWindows)){
                        if(msg.length() > (ChatClient.username + ":- ").length()) {
//                            reDraw = true;
                            System.out.print(ConsoleUtil.ANSI_CURS_LEFT);
                            System.out.print(" ");
                            System.out.print(ConsoleUtil.ANSI_CURS_LEFT);

                            msg.delete(msg.length() - 1,msg.length());
                        }
                    }else if( (keycode == 10 && !ConsoleUtil.IsWindows) || (keycode == 13 && ConsoleUtil.IsWindows) ) {
                        reDraw = true;
                        ChatConnector.sendMsg(msg.toString());
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
            System.out.print(ConsoleUtil.ANSI_CURS_UP);
            System.out.print(ConsoleUtil.ANSI_CURS_ERASE);
            for (int i = messagesDrawn; i < messages.size(); i++) {
                String m = messages.get(i);
                System.out.println(m);
            }
            System.out.print(ConsoleUtil.ANSI_CURS_ERASE);
            System.out.print(ConsoleUtil.ANSI_CURS_DOWN);
            messagesDrawn = messages.size();
            reDraw = false;
            System.out.print( msg.toString());
        }
    }

}

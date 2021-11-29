package com.ok.chatbox;

import java.io.IOException;

public class ConsoleUtil {
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
    public static final String ANSI_CURS_BEGIN = "\u001B[0G";
    public static final boolean IsWindows = System.getProperty("os.name").contains("Windows");

    public static void CLS(){
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

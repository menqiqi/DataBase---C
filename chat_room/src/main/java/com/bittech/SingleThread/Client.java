package com.bittech.SingleThread;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("127.0.0.1",6690);
        Scanner read = null;
        PrintStream write = null;
        try {
            read = new Scanner(client.getInputStream());
            write = new PrintStream(client.getOutputStream(),true,"UTF-8");
            write.println("Hi i am client");
            if (read.hasNext())
                System.out.println("服务器说："+read.nextLine());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            client.close();
            read.close();
            write.close();
        }
    }
}


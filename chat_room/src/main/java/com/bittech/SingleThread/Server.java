package com.bittech.SingleThread;


import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) throws IOException {
        //建立基站
        ServerSocket server = new ServerSocket(6690);
        Scanner read = null;
        PrintStream write = null;
        try {
            //取得客户的socket
            Socket clientSocket = server.accept();
            //取得输入输出流
            read = new Scanner(clientSocket.getInputStream());
            write = new PrintStream(clientSocket.getOutputStream(),true,"UTF-8");
            //数据的读取
            if (read.hasNext())
                System.out.println("客户端说："+read.nextLine());
            write.println("Hi i am server");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            server.close();
            read.close();
            write.close();
        }
    }
}


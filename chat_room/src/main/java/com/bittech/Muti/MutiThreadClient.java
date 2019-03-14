package com.bittech.Muti;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * 读线程
 */
class ReadThread implements Runnable{
    private Socket client;

    public ReadThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            //获取客户端输入流
            Scanner in = new Scanner(client.getInputStream());
            //设定自己的分隔标志
            in.useDelimiter("\n");
            while (true){
                String str = "";
                if (in.hasNext()){
                    str = in.next();
                    System.out.println(str);
                }
                //用户退出
                if (str.equals("quit")){
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("客户端读线程异常，错误为"+e);
        }
    }
}

/**
 * 写线程
 */
class WriteThread implements Runnable{
    private Socket client;

    public WriteThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //读取从键盘的输入
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter("\n");
        //获取客户端的输出流
        try {
            PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
            while (true){
                String str = "";
                if (scanner.hasNext()){
                    str = scanner.nextLine().trim();
                    out.println(str);
                }
                if (str.equals("quit")){
                    System.out.println("该用户下线！");
                    client.close();
                    out.close();
                    scanner.close();
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("客户端写线程异常，错误为"+e);
        }
    }
}

public class MutiThreadClient {
    public static void main(String[] args) {

        try {
            Socket client = new Socket("127.0.0.1",6669);
            Thread readFormServer = new Thread(new ReadThread(client));
            Thread writeToServer = new Thread(new WriteThread(client));
            readFormServer.start();
            writeToServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

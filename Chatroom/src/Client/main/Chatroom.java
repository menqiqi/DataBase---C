package Client.main;


import Client.util.ChatUtil;
import Server.function.ChatBean;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 用户列表中每个用户显示状态
 */

class CellRenderer extends JLabel implements ListCellRenderer {
    CellRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        if (value != null) {
            setText(value.toString());
            setIcon(new ImageIcon("images//1.jpg"));
        }
        //设置用户被选中和未被选中两种前景与背景颜色状态显示
        if (isSelected) {
            //被选中
            setBackground(new Color(255, 255, 153));//背景色
            setForeground(Color.black);//字体色
        } else {
            // 未被选中
            setBackground(Color.white);
            setForeground(Color.black);
        }
        setEnabled(list.isEnabled());
        setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
        setOpaque(true);
        return this;
    }
}


class UUListModel extends AbstractListModel{

    private Vector vs;

    public UUListModel(Vector vs){
        this.vs = vs;
    }

    @Override
    public Object getElementAt(int index) {
        // TODO Auto-generated method stub
        return vs.get(index);
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return vs.size();
    }

}


public class Chatroom extends JFrame {

    private static final long serialVersionUID = 6129126482250125466L;

    private static JPanel contentPane;
    private static Socket clientSocket;
    private static ObjectOutputStream oos;  //对象的输出流
    private static ObjectInputStream ois;  //对象的输入流
    private static String name;
    private static JTextArea textArea;
    private static AbstractListModel listmodel;  //抽象类
    private static JList list;  //列表框
    private static String filePath;
    private static JLabel lblNewLabel;
    private static JProgressBar progressBar;  //进度条
    private static Vector onlines;  //对象数组
    private static boolean isSendFile = false;
    private static boolean isReceiveFile = false;


    //声音
    private static File file, file2;
    private static URL cb, cb2;
    private static AudioClip aau, aau2;  //播放音频
    private File contentFile;



    public Chatroom(String u_name, Socket client) {

        name = u_name;
        clientSocket = client;
        onlines = new Vector();

        SwingUtilities.updateComponentTreeUI(this);  //简单的外观更改

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  //改变窗口显示风格
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (UnsupportedLookAndFeelException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        setTitle(name);
        setResizable(false);  //设置次窗体是否可由用户调整大小
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(200, 100, 688, 550);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("images\\聊天室1.jpg").getImage(), 0, 0,
                        getWidth(), getHeight(), null);
            }

        };


            contentFile = new File(name);


        setContentPane(contentPane);
        contentPane.setLayout(null);


        //聊天信息显示区域
        JScrollPane scrollPane = new JScrollPane();  //滚动面板
        scrollPane.setBounds(10, 10, 410, 300);
        getContentPane().add(scrollPane);

        //聊天信息显示框
        textArea = new JTextArea();  //文本域
        textArea.setEditable(false);
        textArea.setLineWrap(true);//自动换行
        textArea.setWrapStyleWord(true);//自动换行方式，行的长度大于所分配的宽度，在单词边界处换行
        textArea.setFont(new Font("sdf", Font.BOLD, 13));
        //将以往聊天记录显示在聊天信息框
        try{
            BufferedReader br = new BufferedReader(new FileReader(contentFile));
            String tempString = null;
            textArea.setFont(new Font("sdf",Font.BOLD,13));
            while ((tempString = br.readLine()) != null){
                textArea.append(tempString+"\n");
            }
            textArea.setFont(new Font("sdf",Font.BOLD,17));
            br.close();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        scrollPane.setViewportView(textArea);//设置视图

        //打字区域
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 347, 411, 97);
        getContentPane().add(scrollPane_1);

        //输入信息显示框
        final JTextArea textArea_1 = new JTextArea();
        textArea_1.setLineWrap(true);
        textArea_1.setWrapStyleWord(true);
        textArea_1.setFont(new Font("sdf",Font.PLAIN,18));
        scrollPane_1.setViewportView(textArea_1);

        // 关闭按钮
        final JButton btnNewButton = new JButton("关闭");
        btnNewButton.setMargin(new Insets(0,0,0,0));  //解决汉字不显示问题
        btnNewButton.setBounds(214, 448, 60, 30);
        getContentPane().add(btnNewButton);

        //发送按钮
        JButton btnNewButton_1 = new JButton("发送");
        btnNewButton_1.setMargin(new Insets(0,0,0,0));  //解决汉字不显示问题
        btnNewButton_1.setBounds(313, 448, 60, 30);
        getRootPane().setDefaultButton(btnNewButton_1);
        getContentPane().add(btnNewButton_1);

        // 在线用户列表
        listmodel = new UUListModel(onlines) ;
        list = new JList(listmodel);
        list.setCellRenderer(new CellRenderer());
        list.setOpaque(false);
        Border etch = BorderFactory.createEtchedBorder();
        list.setBorder(BorderFactory.createTitledBorder(etch, "<"+u_name+">"
                + "在线用户:", TitledBorder.LEADING, TitledBorder.TOP, new Font(
                "sdf", Font.BOLD, 20), Color.green));  //标题栏设置

        //在线用户滚动区域
        JScrollPane scrollPane_2 = new JScrollPane(list);
        scrollPane_2.setBounds(430, 10, 245, 375);
        scrollPane_2.setOpaque(false);//组件不会显示其中某些像素，允许控件下面的像素显示出来
        scrollPane_2.getViewport().setOpaque(false);//滚动条设置可见
        getContentPane().add(scrollPane_2);

        // 文件传输
        progressBar = new JProgressBar();  //可以输出进度的变化情况
        progressBar.setBounds(430, 390, 245, 15);
        progressBar.setMinimum(1);
        progressBar.setMaximum(100);
        getContentPane().add(progressBar);

        // 文件传输提示
        lblNewLabel = new JLabel(
                "文件传输信息栏:");
        lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 12));
        lblNewLabel.setBackground(Color.WHITE);
        lblNewLabel.setBounds(430, 410, 245, 15);
        getContentPane().add(lblNewLabel);

        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            // 记录上线的客户信息在ChatBean中，并发送给服务器
            ChatBean bean = new ChatBean();
            bean.setType(0);
            bean.setName(name);
            bean.setTimer(ChatUtil.getTimer());
            oos.writeObject(bean);
            oos.flush();

            // 消息提示声音
            file = new File("sounds\\叮.wav");
            cb = file.toURL();
            aau = Applet.newAudioClip(cb);
            // 上线提示声音
            file2 = new File("sounds\\呃欧.wav");
            cb2 = file2.toURL();
            aau2 = Applet.newAudioClip(cb2);

            // 启动用户接收线程
            new ClientInputThread().start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 发送按钮事件监听
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String info = textArea_1.getText();
                List to = list.getSelectedValuesList();  //获取在线用户列表
                //若未选择对象
                if (to.size() < 1) {
                    JOptionPane.showMessageDialog(getContentPane(), "请选择聊天对象");
                    return;
                }
                //若选择对象为自己
                if (to.toString().contains(name+"(我)")) {
                    JOptionPane
                            .showMessageDialog(getContentPane(), "不能向自己发送信息");
                    return;
                }
                //若发送消息为空
                if (info.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "发送消息不能为空");
                    return;
                }

                //正常情况
                ChatBean clientBean = new ChatBean();
                clientBean.setType(1);
                clientBean.setName(name);
                String time = ChatUtil.getTimer();
                clientBean.setTimer(time);
                clientBean.setInfo(info);
                HashSet set = new HashSet();
                set.addAll(to);
                clientBean.setClients(set);
                sendMessage(clientBean);

                // 自己发送的内容也显示在自己的对话框中
                textArea.append(time + " 我对" + to + "说:\r\n" + info + "\r\n");

                //将发送消息保存在本地文件中，作为聊天记录
                try{
                    FileWriter fw = new FileWriter(contentFile,true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(time+"我对<"+to+">说:\r\n"+info+"\r\n");

                    bw.close();
                    fw.close();
                }catch (IOException e1){
                    e1.printStackTrace();
                }
                //清空发送消息栏并重新获取焦点
                textArea_1.setText(null);
                textArea_1.requestFocus();
            }
        });

        // 关闭按钮事件监听
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //文件传输过程中不能关闭窗口
                if(isSendFile || isReceiveFile){
                    JOptionPane.showMessageDialog(contentPane,
                            "正在传输文件中，请勿离开",
                            "Error Message", JOptionPane.ERROR_MESSAGE);
                }else{
                    //发送下线消息
                    btnNewButton.setEnabled(false);
                    ChatBean clientBean = new ChatBean();
                    clientBean.setType(-1);
                    clientBean.setName(name);
                    clientBean.setTimer(ChatUtil.getTimer());
                    sendMessage(clientBean);
                }
            }
        });

        // 窗口事件监听
        this.addWindowListener(new WindowAdapter() {
            @Override
            //离开
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                if(isSendFile || isReceiveFile){
                    JOptionPane.showMessageDialog(contentPane,
                            "正在传输文件中，请勿离开",
                            "Error Message", JOptionPane.ERROR_MESSAGE);
                }else{
                    int result = JOptionPane.showConfirmDialog(getContentPane(),
                            "您确定要离开聊天室");
                    //若确认离开，发送下线消息
                    if (result == 0) {
                        ChatBean clientBean = new ChatBean();
                        clientBean.setType(-1);
                        clientBean.setName(name);
                        clientBean.setTimer(ChatUtil.getTimer());
                        sendMessage(clientBean);
                    }
                }
            }
        });

        // 在线用户列表监听
        list.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                List to = list.getSelectedValuesList();
                //双击事件代表发送文件
                if (e.getClickCount() == 2) {

                    if (to.toString().contains(name+"(我)")) {
                        JOptionPane
                                .showMessageDialog(getContentPane(), "不能向自己发送文件");
                        return;
                    }

                    // 双击打开文件选择框
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("选择文件"); // 弹出对话框
                    chooser.showDialog(getContentPane(), "选择"); // 按钮的名字

                    // 判定是否选择了文件
                    if (chooser.getSelectedFile() != null) {
                        // 获取文件路径
                        filePath = chooser.getSelectedFile().getPath();
                        File file = new File(filePath);
                        // 文件为空
                        if (file.length() == 0) {
                            JOptionPane.showMessageDialog(getContentPane(),
                                    filePath + "文件为空，不允许发送");
                            return;
                        }

                        //正常状态，建立连接，发送请求
                        ChatBean clientBean = new ChatBean();
                        clientBean.setType(2);// 请求发送文件
                        clientBean.setSize(new Long(file.length()).intValue());
                        clientBean.setName(name);
                        clientBean.setTimer(ChatUtil.getTimer());
                        clientBean.setFileName(file.getName()); // 记录文件名
                        clientBean.setInfo("请求发送文件");

                        // 判定发文件给谁
                        HashSet<String> set = new HashSet<String>();
                        set.addAll(list.getSelectedValuesList());
                        clientBean.setClients(set);
                        sendMessage(clientBean);
                    }
                }
            }
        });

    }

    class ClientInputThread extends Thread {

        @Override
        public void run() {
            try {
                // 从服务器接收消息
                while (true) {
                    ois = new ObjectInputStream(clientSocket.getInputStream());
                    final ChatBean  bean = (ChatBean) ois.readObject();
                    switch (bean.getType()) {
                        case 0: {
                            // 更新列表
                            onlines.clear();
                            HashSet<String> clients = bean.getClients();
                            Iterator<String> it = clients.iterator();
                            //重新加载
                            while (it.hasNext()) {
                                String ele = it.next();
                                if (name.equals(ele)) {
                                    onlines.add(ele + "(我)");
                                } else {
                                    onlines.add(ele);
                                }
                            }

                            listmodel = new UUListModel(onlines);
                            list.setModel(listmodel);
                            aau2.play();  //上线声音
                            textArea.append(bean.getInfo() + "\r\n");
                            textArea.selectAll();
                            break;
                        }
                        case -1: {
                            //下线
                            return;
                        }
                        case 1: {
                            //获取发送消息
                            String info = bean.getTimer() + "  " + bean.getName()
                                    + " 对 " + bean.getClients() + "说:\r\n";
                            //将对方发送的消息中自己的名字替换成我
                            if (info.contains(name) ) {
                                info = info.replace(name, "我");
                            }
                            aau.play();
                            textArea.append(info+bean.getInfo() + "\r\n");
                            //将对方发送的消息写入聊天记录中
                            try{
                                FileWriter fw = new FileWriter(contentFile,true);
                                BufferedWriter bw = new BufferedWriter(fw);
                                bw.write(info+bean.getInfo()+"\r\n");

                                bw.close();
                                fw.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                            textArea.selectAll();
                            break;
                        }
                        case 2: {
                            //等待目标用户确认接收文件是阻塞状态，这里使用线程处理
                            new Thread(){
                                public void run() {
                                    //显示是否接收文件对话框
                                    int result = JOptionPane.showConfirmDialog(
                                            getContentPane(), bean.getInfo());
                                    switch(result){
                                        case 0:{  //接收文件
                                            JFileChooser chooser = new JFileChooser();
                                            chooser.setDialogTitle("保存文件框"); // 标题
                                            //默认将文件放在当前目录下
                                            chooser.setSelectedFile(new File(bean
                                                    .getFileName()));
                                            chooser.showDialog(getContentPane(), "保存"); // ���ǰ�ť������..
                                            //保存文件的路径
                                            String saveFilePath =chooser.getSelectedFile().toString();

                                            //创建用户ChatBean
                                            ChatBean clientBean = new ChatBean();
                                            clientBean.setType(3);
                                            clientBean.setName(name);  //接收方的用户名
                                            clientBean.setTimer(ChatUtil.getTimer());
                                            clientBean.setFileName(saveFilePath);
                                            clientBean.setInfo("确定接收文件");

                                            // 判断发送给谁
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set);  //文件来源
                                            clientBean.setTo(bean.getClients());//给这些用户发送文件



                                            //建立服务器端接收数据
                                            try {
                                                ServerSocket ss = new ServerSocket(0); //0可以获取空闲端口号

                                                clientBean.setIp(clientSocket.getInetAddress().getHostAddress());
                                                clientBean.setPort(ss.getLocalPort());
                                                sendMessage(clientBean); //通过服务器告诉发送方，你可以发送文件到我这里了
                                                isReceiveFile=true;
                                                //等待目标来源的客户
                                                Socket sk = ss.accept();
                                                textArea.append(ChatUtil.getTimer() + "  " + bean.getFileName()
                                                        + "文件保存中.\r\n");
                                                DataInputStream dis = new DataInputStream(  //从网络上读取文件
                                                        new BufferedInputStream(sk.getInputStream()));
                                                DataOutputStream dos = new DataOutputStream(  //写在本地
                                                        new BufferedOutputStream(new FileOutputStream(
                                                                saveFilePath)));

                                                int count = 0;
                                                int num = bean.getSize() / 100;
                                                int index = 0;
                                                while (count < bean.getSize()) {
                                                    int t = dis.read();
                                                    dos.write(t);
                                                    count++;

                                                    if(num>0){
                                                        if (count % num == 0 && index < 100) {
                                                            progressBar.setValue(++index);
                                                        }
                                                        lblNewLabel.setText("下载进度："+ count
                                                                + "/" + bean.getSize() + "  整体" + index
                                                                + "%");
                                                    }else{
                                                        lblNewLabel.setText("下载进度：" + count
                                                                + "/" + bean.getSize() +"  整体:"+new Double(new Double(count).doubleValue()/new Double(bean.getSize()).doubleValue()*100).intValue()+"%");
                                                        if(count==bean.getSize()){
                                                            progressBar.setValue(100);
                                                        }
                                                    }

                                                }

                                                //给发送方发提示消息，文件保存成功
                                                PrintWriter out = new PrintWriter(sk.getOutputStream(),true);
                                                out.println(ChatUtil.getTimer() + " 发送给"+name+"的文件[" + bean.getFileName()+"]"
                                                        + "文件保存完毕.\r\n");
                                                out.flush();
                                                dos.flush();
                                                dos.close();
                                                out.close();
                                                dis.close();
                                                sk.close();
                                                ss.close();
                                                textArea.append(ChatUtil.getTimer() + "  " + bean.getFileName()
                                                        + "文件保存完毕.存放位置为:"+saveFilePath+"\r\n");
                                                isReceiveFile = false;
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                            break;
                                        }
                                        default: {
                                            //用户选择取消接收
                                            ChatBean clientBean = new ChatBean();
                                            clientBean.setType(4);
                                            clientBean.setName(name);  //接收方
                                            clientBean.setTimer(ChatUtil.getTimer());
                                            clientBean.setFileName(bean.getFileName());
                                            clientBean.setInfo(ChatUtil.getTimer() + "  "
                                                    + name + "取消接收文件["
                                                    + bean.getFileName() + "]");


                                            //判断发送给谁
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set);
                                            clientBean.setTo(bean.getClients());

                                            sendMessage(clientBean);

                                            break;

                                        }
                                    }
                                };
                            }.start();
                            break;
                        }
                        case 3: {  //接收方愿意接收文件，发送方读取本地文件到网络上
                            textArea.append(bean.getTimer() + "  "+ bean.getName() + "确定接收文件" + ",文件传送中..\r\n");
                            new Thread(){
                                public void run() {

                                    try {
                                        isSendFile = true;
                                        //创界接收文件的客户套接字
                                        Socket s = new Socket(bean.getIp(),bean.getPort());
                                        DataInputStream dis = new DataInputStream(
                                                new FileInputStream(filePath));  //本地读取该用户刚才选中的文件
                                        DataOutputStream dos = new DataOutputStream(
                                                new BufferedOutputStream(s
                                                        .getOutputStream()));  //网络写出文件


                                        int size = dis.available();

                                        int count = 0;  //读取次数
                                        int num = size / 100;
                                        int index = 0;
                                        while (count < size) {

                                            int t = dis.read();
                                            dos.write(t);
                                            count++;  //每次读取一个字节

                                            //显示传输进度
                                            if(num>0){
                                                if (count % num == 0 && index < 100) {
                                                    progressBar.setValue(++index);

                                                }
                                                lblNewLabel.setText("上传进度:" + count + "/"
                                                        + size + "  整体" + index
                                                        + "%");
                                            }else{
                                                lblNewLabel.setText("上传进度:" + count + "/"
                                                        + size +"  整体:"+new Double(new Double(count).doubleValue()/new Double(size).doubleValue()*100).intValue()+"%"
                                                );
                                                if(count==size){
                                                    progressBar.setValue(100);
                                                }
                                            }
                                        }
                                        dos.flush();
                                        dis.close();
                                        //读取目标客户的提示保存完毕的信息
                                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                        textArea.append( br.readLine() + "\r\n");
                                        isSendFile = false;
                                        br.close();
                                        s.close();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                };
                            }.start();
                            break;
                        }
                        case 4: {
                            textArea.append(bean.getInfo() + "\r\n");
                            break;
                        }
                        default: {
                            break;
                        }
                    }

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }
    }

    //传输信息
    private void sendMessage(ChatBean clientBean) {
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(clientBean);
            oos.flush();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}

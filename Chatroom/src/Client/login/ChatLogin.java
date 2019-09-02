package Client.login;

import Client.function.ClientBean;

import Client.main.Chatroom;
import Client.util.ChatUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;

public class ChatLogin extends JFrame {

    private JPanel contentPane;  //面板容器
    private JTextField textField;  //文本输入，账号
    private JPasswordField passwordField;  //密码框
    public static HashMap<String, ClientBean> onlines;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    //启动登录界面
                    ChatLogin frame = new ChatLogin();
                    frame.setVisible(true);  //窗口显示出来
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public ChatLogin() {
        setTitle("欢迎来到聊天室\n");  //设置标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //用户单击关闭按钮时直接关闭应用程序
        setBounds(350, 250, 450, 350);  //设置组件的大小
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;  //默认 序列化

            @Override
            protected void paintComponent(Graphics g) {  //绘制背景
                super.paintComponent(g);  //调用父类的绘制事件
                g.drawImage(new ImageIcon(
                                "images\\登陆界面.jpg").getImage(), 0,
                        0, getWidth(), getHeight(), null);
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));  //设置边框，预留5个像素的空白
        setContentPane(contentPane);  //给窗体加内容面板
        contentPane.setLayout(null);  //将容器的布局设为绝对布局

        //账号输入框
        textField = new JTextField();  //账号的输入框
        textField.setBounds(128, 180, 104, 21);
        textField.setOpaque(false);  //组件是透明的，可显示下方的组件
        contentPane.add(textField);
        textField.setColumns(10);  //最多只能有10个字

        //密码输入框
        passwordField = new JPasswordField();
        passwordField.setForeground(Color.BLACK);  //设置密码字体为黑色
        passwordField.setEchoChar('*');  //设置输入时的文字全为*
        passwordField.setOpaque(false);
        passwordField.setBounds(128, 219, 104, 21);
        contentPane.add(passwordField);

        //登录按钮
        final JButton btnNewButton = new JButton();  //实现按钮
        btnNewButton.setIcon(new ImageIcon("images\\登陆.jpg"));
        btnNewButton.setBounds(246, 237, 50, 25);
        getRootPane().setDefaultButton(btnNewButton);  //设置默认按钮，回车就触发，输完密码后回车就直接登录
        contentPane.add(btnNewButton);

        //注册按钮
        final JButton btnNewButton_1 = new JButton();
        btnNewButton_1.setIcon(new ImageIcon("images\\注册.jpg"));
        btnNewButton_1.setBounds(317, 237, 50, 25);
        contentPane.add(btnNewButton_1);

        //提示信息
        final JLabel lblNewLabel = new JLabel();  //显示文本或图像
        lblNewLabel.setBounds(60, 220, 151, 21);
        lblNewLabel.setForeground(Color.red);  //设置文字颜色
        getContentPane().add(lblNewLabel);


        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Properties userPro = new Properties();  //读取Java的配置文件
                File file = new File("Users.properties");  //以键值对的形式存储：账号=密码
                ChatUtil.loadPro(userPro, file);
                String u_name = textField.getText();  //获取账号

                if (file.length() != 0) {

                    if (userPro.containsKey(u_name)) {
                        String u_pwd = new String(passwordField.getPassword());  //获取密码
                        if (u_pwd.equals(userPro.getProperty(u_name))) {  //如果输入的密码和配置文件中的密码相同，即密码输入正确

                            try {
                                Socket client = new Socket("127.0.0.1", 6669);

                                btnNewButton.setEnabled(false);  //按钮无法触发响应
                                Chatroom frame = new Chatroom(u_name,
                                        client);
                                frame.setVisible(true);//让JFrame对象显示出来
                                setVisible(false);//将窗口隐藏，但相关资源依然存在

                            } catch (UnknownHostException e1) {
                                // TODO Auto-generated catch block
                                errorTip("The connection with the server is interrupted, please login again");
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                errorTip("The connection with the server is interrupted, please login again");
                            }

                        } else {
                            lblNewLabel.setText("密码输入错误，请重新输入");
                            textField.setText("");
                            passwordField.setText("");
                            textField.requestFocus();  //获取焦点
                        }
                    } else {
                        lblNewLabel.setText("该账户不存在，请重新输入");
                        textField.setText("");
                        passwordField.setText("");
                        textField.requestFocus();
                    }
                } else {
                    lblNewLabel.setText("请先注册");
                    textField.setText("");
                    passwordField.setText("");
                    textField.requestFocus();
                }
            }
        });

        //注册
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNewButton_1.setEnabled(false);  //无法响应触发事件
                ChatResign frame = new ChatResign();
                frame.setVisible(true);//显示注册界面
                setVisible(false);//隐藏掉登录界面
            }
        });
    }

    protected void errorTip(String str) {
        // TODO Auto-generated method stub
        JOptionPane.showMessageDialog(contentPane, str, "Error Message",
                JOptionPane.ERROR_MESSAGE);
        textField.setText("");
        passwordField.setText("");
        textField.requestFocus();
    }
}
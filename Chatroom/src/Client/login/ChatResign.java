package Client.login;

import Client.util.ChatUtil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


/**
 * 注册
 */
public class ChatResign extends JFrame {

    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    private JPasswordField passwordField_1;
    private JLabel lblNewLabel;

    public ChatResign() {
        setTitle("Registered chat room\n");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(350, 250, 450, 300);
        contentPane = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(new ImageIcon("images\\注册界面.jpg").getImage(), 0,0, getWidth(), getHeight(), null);
            }
        };
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        textField = new JTextField();  //账号
        textField.setBounds(150, 42, 104, 21);
        textField.setOpaque(false);
        contentPane.add(textField);
        textField.setColumns(10);

        passwordField = new JPasswordField();  //密码
        passwordField.setEchoChar('*');
        passwordField.setOpaque(false);
        passwordField.setBounds(190, 98, 104, 21);
        contentPane.add(passwordField);

        passwordField_1 = new JPasswordField();  //确认密码
        passwordField_1.setBounds(192, 152, 104, 21);
        passwordField_1.setOpaque(false);
        contentPane.add(passwordField_1);

        //注册按钮
        final JButton btnNewButton_1 = new JButton();
        btnNewButton_1.setIcon(new ImageIcon("images\\登陆.jpg"));
        btnNewButton_1.setBounds(320, 198, 80, 40);
        getRootPane().setDefaultButton(btnNewButton_1);
        contentPane.add(btnNewButton_1);

        //返回按钮
        final JButton btnNewButton = new JButton("");
        btnNewButton.setIcon(new ImageIcon("images\\返回.jpg"));
        btnNewButton.setBounds(230, 198, 80, 40);
        contentPane.add(btnNewButton);


        lblNewLabel = new JLabel();
        lblNewLabel.setBounds(55, 218, 185, 20);
        lblNewLabel.setForeground(Color.red);
        contentPane.add(lblNewLabel);

        //注册按钮响应事件
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNewButton.setEnabled(false);
                //进入登陆状态
                ChatLogin frame = new ChatLogin();
                frame.setVisible(true);
                setVisible(false);
            }
        });

        //返回按钮响应事件
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Properties userPro = new Properties();
                File file = new File("Users.properties");
                ChatUtil.loadPro(userPro, file);

                String u_name = textField.getText();
                String u_pwd = new String(passwordField.getPassword());
                String u_pwd_ag = new String(passwordField_1.getPassword());


                if (u_name.length() != 0) {

                    if (userPro.containsKey(u_name)) {
                        lblNewLabel.setText("用户名已存在");
                    } else {
                        isPassword(userPro, file, u_name, u_pwd, u_pwd_ag);
                    }
                } else {
                    lblNewLabel.setText("用户名不能为空");
                }
            }

            private void isPassword(Properties userPro,
                                    File file, String u_name, String u_pwd, String u_pwd_ag) {
                if (u_pwd.equals(u_pwd_ag)) {
                    if (u_pwd.length() != 0) {
                        userPro.setProperty(u_name, u_pwd_ag);
                        try {
                            userPro.store(new FileOutputStream(file),
                                    "Copyright (c) Boxcode Studio");  //给配置文件添加注释
                        } catch (FileNotFoundException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        btnNewButton_1.setEnabled(false);
                        //进入登陆界面
                        ChatLogin frame = new ChatLogin();
                        frame.setVisible(true);
                        setVisible(false);
                    } else {
                        lblNewLabel.setText("密码不能为空");
                    }
                } else {
                    lblNewLabel.setText("输入错误");
                }
            }
        });
    }
}

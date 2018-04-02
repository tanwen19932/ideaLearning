package nio_tw.chatroom.can_work;
  
import nio_tw.chatroom.can_work.ClientService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;  
import java.awt.event.KeyEvent;  
import java.awt.event.KeyListener;  
import java.awt.event.WindowAdapter;  
import java.awt.event.WindowEvent;  
  
import javax.swing.DefaultListModel;  
import javax.swing.JButton;  
import javax.swing.JFrame;  
import javax.swing.JList;  
import javax.swing.JScrollPane;  
import javax.swing.JTextArea;  
import javax.swing.event.ListSelectionEvent;  
import javax.swing.event.ListSelectionListener;  
  
/** 
 * 聊天室窗体 
 * @author zing 
 * 
 */  
public class ChatFrame {  
  
    private JTextArea readContext = new JTextArea(18, 30);// 显示消息文本框  
    private JTextArea writeContext = new JTextArea(6, 30);// 发送消息文本框  
  
    private DefaultListModel modle = new DefaultListModel();// 用户列表模型  
    private JList list = new JList(modle);// 用户列表  
  
    private JButton btnSend = new JButton("发送");// 发送消息按钮  
    private JButton btnClose = new JButton("关闭");// 关闭聊天窗口按钮  
  
    private JFrame frame = new JFrame("ChatFrame");// 窗体界面  
  
    private String uname;// 用户姓名  
  
    private ClientService service;// 用于与服务器交互
  
    private boolean isRun = false;// 是否运行  
  
    public ChatFrame(ClientService service, String uname) {  
        this.isRun = true;  
        this.uname = uname;  
        this.service = service;  
    }  
  
    // 初始化界面控件及事件  
    private void init() {  
        frame.setLayout(null);  
        frame.setTitle(uname + " 聊天窗口");  
        frame.setSize(500, 500);  
        frame.setLocation(400, 200);  
        //设置可关闭  
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        //不能改变窗体大小  
        frame.setResizable(false);  
        //聊天消息显示区带滚动条  
        JScrollPane readScroll = new JScrollPane(readContext);  
        readScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);  
        frame.add(readScroll);  
        //消息编辑区带滚动条  
        JScrollPane writeScroll = new JScrollPane(writeContext);  
        writeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);  
        frame.add(writeScroll);  
        frame.add(list);  
        frame.add(btnSend);  
        frame.add(btnClose);  
        readScroll.setBounds(10, 10, 320, 300);  
        readContext.setBounds(0, 0, 320, 300);  
        readContext.setEditable(false);//设置为不可编辑  
        readContext.setLineWrap(true);// 自动换行  
        writeScroll.setBounds(10, 315, 320, 100);  
        writeContext.setBounds(0, 0, 320, 100);  
        writeContext.setLineWrap(true);// 自动换行  
        list.setBounds(340, 10, 140, 445);  
        btnSend.setBounds(150, 420, 80, 30);  
        btnClose.setBounds(250, 420, 80, 30);  
        //窗体关闭事件  
        frame.addWindowListener(new WindowAdapter() {  
            @Override  
            public void windowClosing(WindowEvent e) {  
                isRun = false;  
                service.sendMsg("exit_" + uname);  
                System.exit(0);  
            }  
        });  
  
        //发送按钮事件  
        btnSend.addActionListener(new ActionListener() {  
            @Override  
            public void actionPerformed(ActionEvent e) {  
                String msg = writeContext.getText().trim();  
                if(msg.length() > 0){  
                    service.sendMsg(uname + "^" + writeContext.getText());  
                }  
                //发送消息后，去掉编辑区文本，并获得光标焦点  
                writeContext.setText(null);  
                writeContext.requestFocus();  
            }  
        });  
  
        //关闭按钮事件  
        btnClose.addActionListener(new ActionListener() {  
            @Override  
            public void actionPerformed(ActionEvent e) {  
                isRun = false;  
                service.sendMsg("exit_" + uname);  
                System.exit(0);  
            }  
        });  
          
        //右边名称列表选择事件  
        list.addListSelectionListener(new ListSelectionListener() {  
            @Override  
            public void valueChanged(ListSelectionEvent e) {  
                // JOptionPane.showMessageDialog(null,  
                // list.getSelectedValue().toString());  
            }  
        });  
          
        //消息编辑区键盘按键事件  
        writeContext.addKeyListener(new KeyListener() {  
              
            @Override  
            public void keyTyped(KeyEvent e) {  
                // TODO Auto-generated method stub  
                  
            }  
              
            //按下键盘按键后释放  
            @Override  
            public void keyReleased(KeyEvent e) {  
                //按下enter键发送消息  
                if(e.getKeyCode() == KeyEvent.VK_ENTER){  
                    String msg = writeContext.getText().trim();  
                    if(msg.length() > 0){  
                        service.sendMsg(uname + "^" + writeContext.getText());  
                    }  
                    writeContext.setText(null);  
                    writeContext.requestFocus();  
                }  
            }  
              
            @Override  
            public void keyPressed(KeyEvent e) {  
                // TODO Auto-generated method stub  
                  
            }  
        });  
    }  
  
    // 此线程类用于轮询读取服务器发送的消息  
    private class MsgThread extends Thread {  
        @Override  
        public void run() {  
            while (isRun) {  
                String msg = service.receiveMsg();  
                if (msg != null) {  
                    //若是名称列表数据，则更新聊天窗体右边的列表  
                    if (msg.indexOf("[") != -1 && msg.lastIndexOf("]") != -1) {  
                        msg = msg.substring(1, msg.length() - 1);  
                        String[] userNames = msg.split(",");  
                        modle.removeAllElements();  
                        for (int i = 0; i < userNames.length; i++) {  
                            modle.addElement(userNames[i].trim());  
                        }  
                    } else {  
                        //将聊天数据设置到聊天消息显示区  
                        String str = readContext.getText() + msg;  
                        readContext.setText(str);  
                        readContext.selectAll();//保持滚动条在最下面  
                    }  
                }  
            }  
        }  
    }  
  
    // 显示界面  
    public void show() {  
        this.init();  
        service.sendMsg("open_" + uname);  
        MsgThread msgThread = new MsgThread();  
        msgThread.start();  
        this.frame.setVisible(true);  
    }  
}
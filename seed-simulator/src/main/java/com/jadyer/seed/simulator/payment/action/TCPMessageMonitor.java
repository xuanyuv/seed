package com.jadyer.seed.simulator.payment.action;

import com.jadyer.seed.comm.util.JadyerUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP报文监控器
 * Created by 玄玉<https://jadyer.cn/> on 2013/05/20 17:23.
 */
public class TCPMessageMonitor extends JPanel implements ActionListener {
    private static final long serialVersionUID = -5516580977254338556L;
    private JTextField tfListenPort,tfHost,tfPort;
    private JTextPane tpReqMsg,tpRespMsg;
    private JButton bCloseTab,bStartMonitor;

    public TCPMessageMonitor(){
        initComponents();
    }

    private void initComponents(){
        JPanel pHostInfo = new JPanel();    //装载请求目标主机地址的的Panel
        JPanel pMessageInfo = new JPanel(); //装载所有的请求交互报文的Panel
        JSplitPane splitPane = new JSplitPane();   //请求报文和响应报文的分割条
        JScrollPane spReqMsg = new JScrollPane();  //请求报文的滚动条效果
        JScrollPane spRespMsg = new JScrollPane(); //响应报文的滚动条效果
        tpReqMsg = new JTextPane();  //用于显示具体的请求报文
        tpRespMsg = new JTextPane(); //用于显示具体的响应报文
        tfListenPort = new JTextField("8089");
        tfHost = new JTextField("127.0.0.1");
        tfPort = new JTextField("8088");
        bCloseTab = new JButton("Close Tab");
        bCloseTab.addActionListener(this);
        bStartMonitor = new JButton("Start Monitor");
        bStartMonitor.addActionListener(this);

        pHostInfo.setLayout(new GridBagLayout());
        pHostInfo.setMinimumSize(new Dimension(500, 50));
        pHostInfo.setPreferredSize(new Dimension(500, 50));
        GridBagConstraints localGridBagConst = new GridBagConstraints();
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 0;
        localGridBagConst.insets = new Insets(10, 0, 20, 30);
        pHostInfo.add(bStartMonitor, localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 0;
        localGridBagConst.insets = new Insets(10, 0, 20, 0);
        pHostInfo.add(new JLabel("ListenPort："), localGridBagConst);
        localGridBagConst.gridx = 2;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 40;
        localGridBagConst.insets = new Insets(10, 0, 20, 10);
        pHostInfo.add(tfListenPort, localGridBagConst);
        localGridBagConst.gridx = 3;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 0;
        localGridBagConst.insets = new Insets(10, 0, 20, 0);
        pHostInfo.add(new JLabel("Host："), localGridBagConst);
        localGridBagConst.gridx = 4;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 100;
        localGridBagConst.insets = new Insets(10, 0, 20, 10);
        pHostInfo.add(tfHost, localGridBagConst);
        localGridBagConst.gridx = 5;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 0;
        localGridBagConst.insets = new Insets(10, 0, 20, 0);
        pHostInfo.add(new JLabel("Port："), localGridBagConst);
        localGridBagConst.gridx = 6;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 40;
        localGridBagConst.insets = new Insets(10, 0, 20, 30);
        pHostInfo.add(tfPort, localGridBagConst);
        localGridBagConst.gridx = 7;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 0;
        localGridBagConst.insets = new Insets(10, 0, 20, 0);
        pHostInfo.add(bCloseTab, localGridBagConst);

        tpRespMsg.setText("该区域用于显示TCP/HTTP/WebService应答报文");
        tpRespMsg.setFont(new Font("Courier New", Font.PLAIN, 12));
        tpRespMsg.setBackground(Color.BLACK);
        tpRespMsg.setForeground(Color.WHITE);
        tpRespMsg.setEditable(false);
        tpReqMsg.setEditable(false);
        tpReqMsg.setText("该区域用于显示TCP/HTTP/WebService请求报文");
        tpReqMsg.setFont(new Font("Courier New", Font.PLAIN, 12));
        tpReqMsg.setBackground(new Color(199, 237, 204));
        tpReqMsg.setForeground(Color.BLACK);
        spReqMsg.setViewportView(tpReqMsg);
        spRespMsg.setViewportView(tpRespMsg);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT); //设置组件为上下方向
        splitPane.setResizeWeight(0.3D);         //让上面的分隔窗格占据空间小一点,若调成0.5D则两个分隔窗格会各占一半空间
        splitPane.setOneTouchExpandable(true);   //在分隔条上提供一个UI小部件来快速展开/折叠
        splitPane.setTopComponent(spReqMsg);     //将组件设置到分割条的上面
        splitPane.setBottomComponent(spRespMsg); //将组件设置到分割条的下面
        pMessageInfo.setLayout(new BorderLayout());
        pMessageInfo.add(splitPane, BorderLayout.CENTER);

        this.setLayout(new BorderLayout());
        this.add(pHostInfo, BorderLayout.NORTH);
        this.add(pMessageInfo, BorderLayout.CENTER);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.bCloseTab == e.getSource()){
            this.getParent().remove(this);
        }else if(this.bStartMonitor == e.getSource()){
            bStartMonitor.setText("Monitoring....");
            bStartMonitor.setEnabled(false);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    TCPMessageMonitor.this.requestProxy(tfListenPort.getText(), tfHost.getText(), tfPort.getText());
                }
            });
            thread.start();
        }
    }


    private void requestProxy(String listenPort, String host, String port) {
        String charset = System.getProperty("file.encoding");
        String respData;
        ServerSocket serverSocket;
        Socket sendSocket = null;
        Socket receiveSocket = null;
        String localSocketAddress = null;
        String remoteSocketAddress = null;
        //Be used to tpReqMsg.setText(reqMsg.toString())
        ByteArrayOutputStream reqMsgStream = new ByteArrayOutputStream();
        ByteArrayOutputStream respMsgStream = new ByteArrayOutputStream();
        try {
            serverSocket = new ServerSocket(Integer.parseInt(listenPort));
            receiveSocket = serverSocket.accept();
            localSocketAddress = receiveSocket.getLocalSocketAddress().toString();
            remoteSocketAddress = receiveSocket.getRemoteSocketAddress().toString();
            /*
             * 接收客户端请求,并转发报文流
             */
            sendSocket = new Socket(host, Integer.parseInt(port));
            Thread thread = new Thread(new StreamThread(receiveSocket.getInputStream(), sendSocket.getOutputStream(), reqMsgStream));
            thread.start();
            Thread thread22 = new Thread(new StreamThread(sendSocket.getInputStream(), receiveSocket.getOutputStream(), respMsgStream));
            thread22.start();
            thread22.run();
            thread.join();
            StringBuilder sb = new StringBuilder();
            sb.append("【通信双方】：java socket, server, ").append(remoteSocketAddress).append(" => ").append(localSocketAddress).append("\r\n");
            sb.append("【收发标识】：Receive\r\n");
            sb.append("【报文内容】：").append(reqMsgStream.toString(charset));
            this.tpReqMsg.setText(sb.toString());
            respData = respMsgStream.toString(charset);
        } catch (Exception e) {
            respData = JadyerUtil.extractStackTrace(e);
        } finally {
            if(null != sendSocket){
                try {
                    sendSocket.close();
                } catch (IOException e) {
                    //ignore the exception
                }
            }
            if(null != receiveSocket){
                try {
                    receiveSocket.close();
                } catch (IOException e) {
                    //ignore the exception
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("【通信双方】：java socket, server, ").append(localSocketAddress).append(" => ").append(remoteSocketAddress).append("\r\n");
        sb.append("【收发标识】：Response\r\n");
        sb.append("【报文内容】：").append(respData);
        this.tpRespMsg.setText(sb.toString());
    }


    private class StreamThread implements Runnable {
        private InputStream srcStream;
        private OutputStream destStream;
        private ByteArrayOutputStream msgStream;
        private StreamThread(InputStream srcStream, OutputStream destStream, ByteArrayOutputStream msgStream){
            this.srcStream = srcStream;
            this.destStream = destStream;
            this.msgStream = msgStream;
        }
        private void copyStream() throws IOException{
            byte[] buffer = new byte[3072];
            int len;
            int k = 0;
            do{
                //客户端建立连接后,若3s内仍未发送数据过来,则抛异常
                if(this.srcStream.available() == 0){
                    if (k >= 15){
                        throw new IOException("Data not available on the connection");
                    }
                    try {
                        Thread.sleep(200); //休眠0.2s
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    k++;
                }
                //然后把模拟器收到的InputStream里面的数据"拷贝"到两个OutputStream里面
                len = this.srcStream.read(buffer);
                if(len > 0){
                    k = 0;
                    this.destStream.write(buffer, 0, len);
                    this.msgStream.write(buffer, 0, len);
                }
            }while(len != -1);
        }
        @Override
        public void run() {
            try {
                copyStream();
            } catch (IOException e1) {
                System.out.println("TCPMessageMonitor.StreamThread.copyStream()时遇到异常,堆栈轨迹如下");
                e1.printStackTrace();
            } finally {
                if(null != this.srcStream){
                    try {
                        this.srcStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(null != this.destStream){
                    try {
                        this.destStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(null != this.msgStream){
                    try {
                        this.msgStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
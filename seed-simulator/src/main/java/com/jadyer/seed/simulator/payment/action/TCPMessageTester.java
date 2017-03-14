package com.jadyer.seed.simulator.payment.action;

import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.JadyerUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * TCP报文测试器
 * Created by 玄玉<https://jadyer.github.io/> on 2013/04/05 21:32.
 */
public class TCPMessageTester extends JPanel implements ActionListener {
	private static final long serialVersionUID = -2783534335348156619L;
	private JTextField tfHostIP;
	private JTextField tfHostPort;
	private JTextField tfMsgLen;
	private JTextPane tpReqMsg;
	private JTextPane tpRespMsg;
	private JButton bSendMsg;
	private JButton bCloseTab;
	
	public TCPMessageTester(){
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
		tfHostIP = new JTextField("127.0.0.1");
		tfHostPort = new JTextField("9901");
		tfMsgLen = new JTextField("372");
		tfMsgLen.setBackground(Color.WHITE);
		tfMsgLen.setEditable(false);
		bSendMsg = new JButton("Send Msg");
		bSendMsg.addActionListener(this);
		bCloseTab = new JButton("Close Tab");
		bCloseTab.addActionListener(this);
		
		pHostInfo.setLayout(new GridBagLayout());
		pHostInfo.setMinimumSize(new Dimension(500, 50));
		pHostInfo.setPreferredSize(new Dimension(500, 50));
		GridBagConstraints localGridBagConst = new GridBagConstraints();
		localGridBagConst.gridx = 0;
		localGridBagConst.gridy = 0;
		localGridBagConst.insets = new Insets(10, 0, 20, 30);
		pHostInfo.add(bSendMsg, localGridBagConst);
		localGridBagConst.gridx = 1;
		localGridBagConst.gridy = 0;
		localGridBagConst.insets = new Insets(10, 0, 20, 0);
		pHostInfo.add(new JLabel("Host："), localGridBagConst);
		localGridBagConst.gridx = 2;
		localGridBagConst.gridy = 0;
		localGridBagConst.ipadx = 85;
		localGridBagConst.insets = new Insets(10, 0, 20, 0);
		pHostInfo.add(tfHostIP, localGridBagConst);
		localGridBagConst.gridx = 3;
		localGridBagConst.gridy = 0;
		localGridBagConst.ipadx = 0;
		localGridBagConst.insets = new Insets(10, 10, 20, 0);
		pHostInfo.add(new JLabel("Port："), localGridBagConst);
		localGridBagConst.gridx = 4;
		localGridBagConst.gridy = 0;
		localGridBagConst.ipadx = 45;
		localGridBagConst.insets = new Insets(10, 0, 20, 0);
		pHostInfo.add(tfHostPort, localGridBagConst);
		localGridBagConst.gridx = 5;
		localGridBagConst.gridy = 0;
		localGridBagConst.ipadx = 0;
		localGridBagConst.insets = new Insets(10, 10, 20, 0);
		pHostInfo.add(new JLabel("Length："), localGridBagConst);
		localGridBagConst.gridx = 6;
		localGridBagConst.gridy = 0;
		localGridBagConst.ipadx = 30;
		localGridBagConst.insets = new Insets(10, 0, 20, 0);
		pHostInfo.add(tfMsgLen, localGridBagConst);
		localGridBagConst.gridx = 7;
		localGridBagConst.gridy = 0;
		localGridBagConst.ipadx = 0;
		localGridBagConst.insets = new Insets(10, 30, 20, 0);
		pHostInfo.add(bCloseTab, localGridBagConst);
		
		tpRespMsg.setText("该区域用于显示TCP应答报文");
		tpRespMsg.setFont(new Font("Courier New", Font.PLAIN, 12));
		tpRespMsg.setBackground(Color.BLACK);
		tpRespMsg.setForeground(Color.WHITE);
		tpRespMsg.setEditable(false);
		tpReqMsg.setText("0003721000510110199201209222240000020120922000069347814303000700000813``中国联通交费充值`为号码18655228826交费充值100.00元`UDP1209222238312219411`10000```20120922`chinaunicom-payFeeOnline`UTF-8`20120922223831`MD5`20120922020103806276`1`02`10000`20120922223954`20120922`BOCO_B2C```http://192.168.20.2:5545/ecpay/pay/elecChnlFrontPayRspBackAction.action`1`立即支付,交易成功`");
		tpReqMsg.setFont(new Font("Courier New", Font.PLAIN, 12));
		tpReqMsg.setBackground(new Color(199, 237, 204));
		tpReqMsg.setForeground(Color.BLACK);
		tpReqMsg.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					tfMsgLen.setText(String.valueOf(JadyerUtil.getBytes(TCPMessageTester.this.tpReqMsg.getText(), StandardCharsets.UTF_8.toString()).length));
				} catch (Exception e1) {
					tpRespMsg.setText(JadyerUtil.extractStackTrace(e1));
				}
			}
		});
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
		}else if(this.bSendMsg == e.getSource()){
			bSendMsg.setText("Waiting....");
			bSendMsg.setEnabled(false);
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Map<String, String> respMap = HttpUtil.tcp(tfHostIP.getText(), Integer.parseInt(tfHostPort.getText()), tpReqMsg.getText());
					StringBuilder sb = new StringBuilder();
					sb.append("【通信双方】：java socket, client, /127.0.0.1:").append(respMap.get("localBindPort")).append(" => /").append(tfHostIP.getText()).append(":").append(tfHostPort.getText()).append("\r\n");
					sb.append("【收发标识】：Receive\r\n");
					sb.append("【报文内容】：").append(respMap.get("respData"));
					sb.append(null==respMap.get("respDataHex") ? "" : respMap.get("respDataHex"));
					tpRespMsg.setText(sb.toString());
					bSendMsg.setText("Send Msg");
					bSendMsg.setEnabled(true);
				}
			});
			thread.start();
		}
	}
}
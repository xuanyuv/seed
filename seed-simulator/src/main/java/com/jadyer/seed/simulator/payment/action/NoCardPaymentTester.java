package com.jadyer.seed.simulator.payment.action;

import com.jadyer.seed.comm.util.CodecUtil;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.JadyerUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoCardPaymentTester extends JPanel implements ActionListener {
    private static final long serialVersionUID = 3781058633393514998L;
    /*
    evn.database.description=本地的开发环境:local,集成测试环境:develop,准生产环境:preProduce,生产环境:production
    env.key.mer.local=301900100000521:NotFound
    env.key.mer.develop=301900100000521:PPT342KCAOYA53S3335J3FPXP7H5J333,301102000000727:D26W18AH14AMMVJFC2733379RZIXL333,301102000000781:B79J5EL4CUZGZRCJ3333RWAB9JSIV333,305100700001481:DP3LGM1DQIKKL4M333CTUF3XSO78Z333
    env.key.mer.preProduce=301900100000521:PPT342KCAOYA53S3335J3FPXP7H5J333,301102000000727:D26W18AH14AMMVJFC2733379RZIXL333,301102000000781:B79J5EL4CUZGZRCJ3333RWAB9JSIV333,305100700001481:DP3LGM1DQIKKL4M333CTUF3XSO78Z333
    env.key.mer.production=301900100000521:PPT342KCAOYA53S3335J3FPXP7H5J333,301102000000727:D26W18AH14AMMVJFC2733379RZIXL333,301102000000781:B79J5EL4CUZGZRCJ3333RWAB9JSIV333,305100700001481:DP3LGM1DQIKKL4M333CTUF3XSO78Z333
    env.url.noCardPayment.local=http://127.0.0.1/tra/trade/noCardNoPassword.htm
    env.url.noCardPayment.develop=http://123.125.97.92:8080/tra/trade/noCardNoPassword.htm
    env.url.noCardPayment.preProduce=http://123.125.97.239/tra/trade/noCardNoPassword.htm
    env.url.noCardPayment.production=http://123.125.97.248/tra/trade/noCardNoPassword.htm
    bank.noCardPayment=光大银行信用卡:CEB_CREDIT,工商银行信用卡:ICBC_CREDIT,广发银行信用卡:GDB_CREDIT,交通银行信用卡:COMM_CREDIT,招商银行信用卡:CEB_CREDIT,农业银行信用卡:ABC_CREDIT,建设银行信用卡:CCB_CREDIT
    */
    private static final String ENVS = "本地的开发环境:local,集成测试环境:develop,准生产环境:preProduce,生产环境:production";
    private static final String BANKS = "光大银行信用卡:CEB_CREDIT,工商银行信用卡:ICBC_CREDIT,广发银行信用卡:GDB_CREDIT,交通银行信用卡:COMM_CREDIT,招商银行信用卡:CEB_CREDIT,农业银行信用卡:ABC_CREDIT,建设银行信用卡:CCB_CREDIT";
    private Map<String, String> bankMap = new HashMap<>();
    private Map<String, String> envMap = new HashMap<>();
    private JTextField tfAmount;
    private JTextField tfGoodsName;
    private JTextField tfMerNo;
    private JTextField tfOrderNo;
    private JTextField tfCustomerID;
    private JTextField tfCustomerName;
    private JTextField tfMobileNo;
    private JTextField tfCreditCardNo;
    private JTextField tfValidityYear;
    private JTextField tfValidityMonth;
    private JTextField tfCVVNo;
    private JComboBox cbCooBankNo;
    private JComboBox cbSelectEnv;
    private JTextPane tpReqMsg;
    private JTextPane tpRespMsg;
    private JButton bCloseTab;
    private JButton bCommitReq;

    public NoCardPaymentTester(){
        initEnvs();       //初始化业务环境
        initBanks();      //初始化无卡支付业务所支持的银行卡
        initComponents(); //初始化各组件
    }

    private void initEnvs(){
        for(String env : ENVS.split(",")){
            envMap.put(env.substring(0, env.indexOf(":")), env.substring(env.indexOf(":")+1));
        }
    }

    private void initBanks(){
        for(String bank : BANKS.split(",")){
            bankMap.put(bank.substring(0, bank.indexOf(":")), bank.substring(bank.indexOf(":")+1));
        }
    }

    private void initComponents(){
        JPanel pReqInfo = new JPanel();  //整体的请求信息,包括请求描述、参数、提交请求按钮等
        JPanel pReqParam = new JPanel(); //真实的请求参数
        JPanel pRespInfo = new JPanel(); //装载所有的请求交互报文的Panel
        JSplitPane splitPane = new JSplitPane();   //请求报文和响应报文的分割条
        JScrollPane spReqMsg = new JScrollPane();  //请求报文的滚动条效果
        JScrollPane spRespMsg = new JScrollPane(); //响应报文的滚动条效果
        tpReqMsg = new JTextPane();                //用于显示具体的请求报文
        tpRespMsg = new JTextPane();                //用于显示具体的响应报文
        bCloseTab = new JButton("Close Tab");
        bCommitReq = new JButton("Commit Req");
        bCloseTab.addActionListener(this);
        bCommitReq.addActionListener(this);
        tfAmount = new JTextField("1");
        tfAmount.addKeyListener(new KeyAdapter(){
            @Override
            public void keyReleased(KeyEvent e) {
                if(Integer.parseInt(tfAmount.getText()) > 10 ){
                    JOptionPane.showMessageDialog(null, "差不多就行了，这又不是你的钱！！", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        tfOrderNo = new JTextField("90020120914015860583");
        tfCustomerID = new JTextField("513721199010304811");
        tfCustomerName = new JTextField("李治学");
        tfMobileNo = new JTextField("13501248084");
        tfCreditCardNo = new JTextField("6225591370451548");
        tfValidityYear = new JTextField("17");
        tfValidityMonth = new JTextField("05");
        tfCVVNo = new JTextField("695");
        cbCooBankNo = new JComboBox();
        cbCooBankNo.setBackground(Color.WHITE);
        cbCooBankNo.setFont(new Font("宋体", Font.PLAIN, 12));
        for(Map.Entry<String, String> entry : bankMap.entrySet()){
            cbCooBankNo.addItem(entry.getKey());
        }
        //显示效果,这里才手写了..其实也应该像cbCooBankNo一样从配置文件中读取<select>的<option>值
        cbSelectEnv = new JComboBox(new String[]{"本地的开发环境", "集成测试环境", "准生产环境", "生产环境"});
        cbSelectEnv.setFont(new Font("宋体", Font.BOLD, 12));
        cbSelectEnv.setBackground(Color.WHITE);
        cbSelectEnv.setSelectedIndex(1);
        cbSelectEnv.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED && "生产环境".equals(e.getItem())){
                    JOptionPane.showMessageDialog(null, "想动生产环境？没门 ！！", "风险告警", JOptionPane.INFORMATION_MESSAGE);
                    cbSelectEnv.setSelectedIndex(1);
                }
            }
        });
        tfGoodsName = new JTextField("Tea");
        tfGoodsName.setEditable(false);
        tfMerNo = new JTextField("301900100000521");
        tfMerNo.setEditable(false);
        JTextField tfSignMsg = new JTextField("模拟器会自动签名");
        tfSignMsg.setEditable(false);

        pReqParam.setLayout(new GridBagLayout());
        pReqParam.setMinimumSize(new Dimension(260, 380));
        pReqParam.setPreferredSize(new Dimension(260, 380));
        GridBagConstraints localGridBagConst = new GridBagConstraints();
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 2;
        localGridBagConst.anchor = GridBagConstraints.EAST; //右对齐
        pReqParam.add(new JLabel("订单号："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 0;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfOrderNo, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 2;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("订单金额(分)："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 2;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfAmount, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 3;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("持卡人姓名："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 3;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfCustomerName, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 4;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("持卡人手机："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 4;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfMobileNo, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 5;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("身份证(18位)："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 5;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfCustomerID, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 6;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("合作银行："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 6;
        localGridBagConst.ipadx = 51;
        localGridBagConst.anchor = GridBagConstraints.WEST;
        pReqParam.add(cbCooBankNo, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 7;
        localGridBagConst.ipadx = 2;
        localGridBagConst.anchor = GridBagConstraints.EAST; //右对齐
        pReqParam.add(new JLabel("银行卡号："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 7;
        localGridBagConst.ipadx = 160;
        localGridBagConst.insets = new Insets(1, 0, 0, 0);
        pReqParam.add(tfCreditCardNo, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 8;
        localGridBagConst.ipadx = 2;
        localGridBagConst.insets = new Insets(0, 0, 0, 0);
        pReqParam.add(new JLabel("有效期年："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 8;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfValidityYear, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 9;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("有效期月："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 9;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfValidityMonth, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 10;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("CVV码："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 10;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfCVVNo, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 11;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("商品名："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 11;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfGoodsName, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 12;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("商户号："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 12;
        localGridBagConst.ipadx = 160;
        pReqParam.add(tfMerNo, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 13;
        localGridBagConst.ipadx = 2;
        pReqParam.add(new JLabel("商户签名："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 13;
        localGridBagConst.ipadx = 159;
        localGridBagConst.anchor = GridBagConstraints.WEST;
        pReqParam.add(tfSignMsg, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 14;
        localGridBagConst.ipadx = 2;
        localGridBagConst.anchor = GridBagConstraints.EAST;
        pReqParam.add(new JLabel("请选择环境："), localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 14;
        localGridBagConst.ipadx = 44;
        localGridBagConst.anchor = GridBagConstraints.WEST;
        localGridBagConst.insets = new Insets(1, 0, 0, 0);
        pReqParam.add(cbSelectEnv, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 15;
        localGridBagConst.ipadx = 2;
        localGridBagConst.anchor = GridBagConstraints.CENTER;
        localGridBagConst.insets = new Insets(10, 2, 0, 0);
        pReqParam.add(bCloseTab, localGridBagConst);
        localGridBagConst.gridx = 1;
        localGridBagConst.gridy = 15;
        localGridBagConst.anchor = GridBagConstraints.CENTER;
        pReqParam.add(bCommitReq, localGridBagConst);

        pReqInfo.setLayout(new GridBagLayout());
        pReqInfo.setMinimumSize(new Dimension(300, 380));
        pReqInfo.setPreferredSize(new Dimension(300, 380));
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 0;
        localGridBagConst.insets = new Insets(0, 0, 0, 0);
        localGridBagConst.anchor = GridBagConstraints.CENTER;
        //JButton和JLabel等Swing组件主要支持HTML中的用于指定静态文档或图片的格式的标记
        //如<B>、<I>、<P>、<BR>、<FONT>、<HR>和<IMG>等
        //但它们不支持HTML3.2中的<APPLET>、<PARAM>、<MAP>、<AREA>、<LINK>、<SCRIPT>和<STYLE>标记
        //并且不支持HTML4.0中的各种新标记,如<BDO>、<BUTTON>、<LEGEND>和<TFOOT>等
        //此外要注意的是,当使用<IMG>时,Swing组件不能解析属性值中的相对路径,所以要写成绝对路径的图片
        pReqInfo.add(new JLabel("<html><font color='blue'><i>This utility can be used to test NoCardPayment</i></font></html>"), localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 1;
        localGridBagConst.insets = new Insets(0, 0, 90, 0);
        pReqInfo.add(pReqParam, localGridBagConst);

        tpReqMsg.setText("该区域用于显示HTTP请求报文");
        tpReqMsg.setFont(new Font("Courier New", Font.PLAIN, 12));
        tpReqMsg.setBackground(new Color(199, 237, 204));
        tpReqMsg.setForeground(Color.BLACK);
        tpReqMsg.setEditable(false);
        tpRespMsg.setText("该区域用于显示HTTP应答报文");
        tpRespMsg.setFont(new Font("Courier New", Font.PLAIN, 12));
        tpRespMsg.setBackground(Color.BLACK);
        tpRespMsg.setForeground(Color.WHITE);
        tpRespMsg.setEditable(false);
        spReqMsg.setViewportView(tpReqMsg);
        spRespMsg.setViewportView(tpRespMsg);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT); //设置组件为上下方向
        splitPane.setResizeWeight(0.4D);         //让上面的分隔窗格占据空间小一点,若调成0.5D则两个分隔窗格会各占一半空间
        splitPane.setOneTouchExpandable(true);   //在分隔条上提供一个UI小部件来快速展开/折叠
        splitPane.setTopComponent(spReqMsg);     //将组件设置到分割条的上面
        splitPane.setBottomComponent(spRespMsg); //将组件设置到分割条的下面

        pRespInfo.setLayout(new BorderLayout());
        pRespInfo.add(splitPane, BorderLayout.CENTER);
        this.setLayout(new BorderLayout());
        this.add(pReqInfo, BorderLayout.WEST);
        this.add(pRespInfo, BorderLayout.CENTER);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(this.bCloseTab == e.getSource()){
            this.getParent().remove(this);
        }else if(this.bCommitReq == e.getSource()){
            this.bCommitReqActionPerformed(e);
        }
    }


    private void bCommitReqActionPerformed(ActionEvent e){
        bCommitReq.setText("Waiting....");
        bCommitReq.setEnabled(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> respMap = new HashMap<>();
                Map<String, String> reqParams = new HashMap<>();
                reqParams.put("goodId", "goodId");
                reqParams.put("goodsDesc", "goodsDesc");
                reqParams.put("merUserId", "merUserId");
                reqParams.put("merExtend", "merExtend");
                reqParams.put("orderDate", new SimpleDateFormat("yyyyMMdd").format(new Date()));
                reqParams.put("merReqTime", new SimpleDateFormat("HHmmss").format(new Date()));
                reqParams.put("serverCallUrl", "http://123.125.97.248/");
                reqParams.put("interfaceVersion", "1.0.0.1");
                reqParams.put("busChannel", "02");
                reqParams.put("signType", "MD5");
                reqParams.put("orderValidityUnits", "m"); //m表示分钟
                reqParams.put("orderValidityNum", "30");  //这里就是30分钟
                reqParams.put("customerType", "02");      //02--18位身份证,01--15位身份证
                reqParams.put("merNo", tfMerNo.getText());
                reqParams.put("amount", tfAmount.getText());
                reqParams.put("orderNo", tfOrderNo.getText());
                reqParams.put("goodsName", tfGoodsName.getText());
                reqParams.put("customerID", tfCustomerID.getText());
                reqParams.put("customerName", tfCustomerName.getText());
                reqParams.put("mobileNo", tfMobileNo.getText());
                reqParams.put("creditCardNo", tfCreditCardNo.getText());
                reqParams.put("validityYear", tfValidityYear.getText());
                reqParams.put("validityMonth", tfValidityMonth.getText());
                reqParams.put("CVVNo", tfCVVNo.getText());
                reqParams.put("cooBankNo", bankMap.get(cbCooBankNo.getSelectedItem()));
                try {
                    //获取商户签名key
                    String merSignKey = "MERKEY_NOT_FOUND";
                    //获取当前所选择的业务环境
                    String env = envMap.get(cbSelectEnv.getSelectedItem());
                    //获取不同环境下的商户key
                    String keys;
                    if("local".equals(env)){
                        keys = "301900100000521:NotFound";
                    }else{
                        keys = "301900100000521:PPT342KCAOYA53S3335J3FPXP7H5J333,301102000000727:D26W18AH14AMMVJFC2733379RZIXL333,301102000000781:B79J5EL4CUZGZRCJ3333RWAB9JSIV333,305100700001481:DP3LGM1DQIKKL4M333CTUF3XSO78Z333";
                    }
                    if(keys.contains(tfMerNo.getText())){
                        String str = keys.substring(keys.indexOf(tfMerNo.getText()) + tfMerNo.getText().length() + 1);
                        merSignKey = str.contains(",") ? str.substring(0, str.indexOf(",")) : str;
                    }
                    //签名
                    reqParams.put("signMsg", CodecUtil.buildHexSign(reqParams, "UTF-8", "MD5", merSignKey));
                    //很奇怪，交易前置的无磁无密接口在验签时，没有把merReqSerial字段也验进去
                    reqParams.put("merReqSerial", "merReqSerial");
                    //获取远程主机接口地址
                    String reqURL = null;
                    switch(env){
                        case "local":
                            reqURL = "http://127.0.0.1/tra/trade/noCardNoPassword.htm";
                            break;
                        case "develop":
                            reqURL = "http://123.125.97.92:8080/tra/trade/noCardNoPassword.htm";
                            break;
                        case "preProduce":
                            reqURL = "http://123.125.97.239/tra/trade/noCardNoPassword.htm";
                            break;
                        case "production":
                            reqURL = "http://123.125.97.248/tra/trade/noCardNoPassword.htm";
                            break;
                        default:
                            System.out.println("nothing to do");
                    }
                    //发送HTTP请求
                    respMap = HTTPUtil.postBySocket(reqURL, reqParams);
                    tpRespMsg.setText(null==respMap.get("respMsgHex") ? respMap.get("respFullData") : respMap.get("respFullData")+respMap.get("respMsgHex"));
                } catch (Exception e1) {
                    tpRespMsg.setText(JadyerUtil.extractStackTrace(e1));
                }
                tpReqMsg.setText(respMap.get("reqFullData"));
                bCommitReq.setText("Commit Req");
                bCommitReq.setEnabled(true);
            }
        });
        thread.start();
    }
}
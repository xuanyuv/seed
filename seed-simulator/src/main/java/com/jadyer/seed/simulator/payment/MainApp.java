package com.jadyer.seed.simulator.payment;

import com.jadyer.seed.comm.util.JCifsUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.simulator.payment.action.NoCardPaymentTester;
import com.jadyer.seed.simulator.payment.action.TCPMessageMonitor;
import com.jadyer.seed.simulator.payment.action.TCPMessageTester;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * 模拟器入口
 * ------------------------------------------------------------------------------------------------------------
 * 归档工具使用说明
 * 注意：最简单的就是Eclipse上面装个FatJar插件，因为下面的办法不是绝对可运行的（它引用了外部的lib/ojdbc.jar）
 * 0、下面说的是有依赖jar的情况，没有依赖jar的话，可以直接用jar命令生成
 * 1、桌面新建目录sim
 * 2、将bin下的所有文件，以及/lib/ojdbc6-11.2.0.4.jar（连同lib文件夹）都拷到桌面的sim目录下
 * 3、在cmd下执行C:\Users\Jadyer\Desktop\sim>jar -cvfe mysim.jar com.jadyer.seed.simulator.payment.MainApp *
 * 4、剪切生成的mysim.jar到桌面，并打开，修改里面的/META-INF/MANIFEST.MF文件
 * 5)MANIFEST.MF修改后的内容如下（注意这里最后空出来了两行）
 *   Manifest-Version: 1.0
 *   Created-By: 1.6.0_33 (Sun Microsystems Inc.)
 *   Main-Class: com.jadyer.seed.simulator.payment.MainApp
 *   Class-Path: /lib/ojdbc6-11.2.0.4.jar
 * 
 * 
 * 6)注意MANIFEST.MF：最后空出两个空行，Main-Class的冒号后要跟一个空格，并且该文件默认采用ANSI编码，不要用UTF-8
 * ------------------------------------------------------------------------------------------------------------
 * 另外，说一下在没有JRE的机器上如何运行
 * 1、写一个start.bat（其内容为:.\jre6\bin\java -jar .\mySimulator.jar）
 * 2、然后将start.bat，mySimulator.jar还有jre6文件夹放到一个文件夹内
 * 3、双击start.bat即可
 * ------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2013/04/05 17:54.
 */
public class MainApp extends JFrame implements ActionListener {
    private static final long serialVersionUID = 6763819475306383567L;
    /*
    simulator.name=支付模拟器v2.4
    simulator.about=\r\n作者：玄玉\r\n邮箱：jadyer@yeah.net\r\n博客：https://jadyer.github.io/\r\n更新：\r\nv1.0-->20130405：实现了无卡支付业务模拟功能，支持多家银行，支持生产在内的所有环境测试\r\nv2.0-->20130406：增加TCP报文测试器工具，可自动计算报文长度，支持服务端响应原始字节打印\r\nv2.1-->20130406：主窗口WelcomeTab增加数字时钟功能，目前订单通知和自动补单功能尚未实现\r\nv2.2-->20130423：移除自动补单测试的功能，并增加网络共享文件读取及调用本地程序查看的功能\r\nv2.3-->20130502：读取共享文件时增加进度条显示，增加多线程处理，取消无卡支付生产测试功能\r\nv2.4-->20130714：新增代码行数统计器，支持 *.java  *.xml  *.properties  *.jsp  *.htm  *.html六种文件
    #v2.4-->20130520：增加TCP报文监控器工具，可监控TCP或HTTP报文，类似于Apache-TCPMon工具
    */
    private static final String SIMULATOR_NAME = "支付模拟器v2.4";
    private static final String SIMULATOR_ABOUT = "\r\n作者：玄玉\r\n邮箱：jadyer@yeah.net\r\n博客：https://jadyer.github.io/\r\n更新：\r\nv1.0-->20130405：实现了无卡支付业务模拟功能，支持多家银行，支持生产在内的所有环境测试\r\nv2.0-->20130406：增加TCP报文测试器工具，可自动计算报文长度，支持服务端响应原始字节打印\r\nv2.1-->20130406：主窗口WelcomeTab增加数字时钟功能，目前订单通知和自动补单功能尚未实现\r\nv2.2-->20130423：移除自动补单测试的功能，并增加网络共享文件读取及调用本地程序查看的功能\r\nv2.3-->20130502：读取共享文件时增加进度条显示，增加多线程处理，取消无卡支付生产测试功能\r\nv2.4-->20130714：新增代码行数统计器，支持 *.java  *.xml  *.properties  *.jsp  *.htm  *.html六种文件";
    private JTabbedPane tabbedPane;
    private Timer timer;
    private JLabel lClock;
    private JMenuItem mi_quit,mi_about,mi_toBig,mi_toSmall,mi_pop_toBig,mi_pop_toSmall,mi_pop_exit;
    private JButton bCaptureScreenTester,bNoCardPaymentTester,bTCPMessageTester,bTCPMessageMonitor,bCodeLineCounter,bShareFileReader;

    /**
     * 启动应用
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainApp().setVisible(true);
            }
        });
    }

    private MainApp(){
        //设置界面外观为跨平台外观,默认即跨平台外观
        //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        //设置界面外观为系统外观
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        initComponents();
    }

    /**
     * 组件初始化
     */
    private void initComponents(){
        tabbedPane = new JTabbedPane();
        JMenuBar jmb = new JMenuBar();
        JMenu menu_file = new JMenu("File");
        JMenu menu_edit = new JMenu("Edit");
        JMenu menu_help = new JMenu("Help");
        JMenuItem mi_new = new JMenuItem("New");
        JMenuItem mi_open = new JMenuItem("Open");
        JMenuItem mi_save = new JMenuItem("Save");
        mi_quit = new JMenuItem("Exit");
        JMenuItem mi_font = new JMenuItem("字体");
        JMenu menu_window = new JMenu("窗体");
        mi_toBig = new JMenuItem("放大");
        mi_toSmall = new JMenuItem("缩小");
        JMenuItem mi_toCustom = new JMenuItem("自定义");
        mi_about = new JMenuItem("About");
        mi_new.setEnabled(false);
        mi_open.setEnabled(false);
        mi_save.setEnabled(false);
        mi_font.setEnabled(false);
        mi_toCustom.setEnabled(false);
        menu_file.add(mi_new);
        menu_file.add(mi_open);
        menu_file.add(mi_save);
        menu_file.addSeparator(); //添加分隔线
        menu_file.add(mi_quit);
        menu_edit.add(mi_font);
        menu_edit.add(menu_window);
        menu_window.add(mi_toBig);
        menu_window.add(mi_toSmall);
        menu_window.add(mi_toCustom);
        menu_help.add(mi_about);
        jmb.add(menu_file);
        jmb.add(menu_edit);
        jmb.add(menu_help);
        mi_quit.addActionListener(this);
        mi_toBig.addActionListener(this);
        mi_toSmall.addActionListener(this);
        mi_about.addActionListener(this);
        /*
         * 制作数字时钟
         */
        lClock = new JLabel("Clock");
        timer = new Timer(1000, this);
        timer.setInitialDelay(0); //设置首次延迟时间,单位为毫秒
        timer.start();            //启动定时器
        /*
         * 编辑欢迎页标签的右键弹出式菜单
         */
        final JPanel pWelcome = new JPanel();
        JPopupMenu jpm = new JPopupMenu();
        mi_pop_toBig = new JMenuItem("放大");
        mi_pop_toSmall = new JMenuItem("缩小");
        mi_pop_exit = new JMenuItem("退出");
        jpm.add(mi_pop_toBig);
        jpm.add(mi_pop_toSmall);
        jpm.add(mi_pop_exit);
        mi_pop_toBig.addActionListener(this);
        mi_pop_toSmall.addActionListener(this);
        mi_pop_exit.addActionListener(this);
        /*
         * 组装WelcomeTab
         */
        pWelcome.setComponentPopupMenu(jpm);     //指定右键弹出菜单
        pWelcome.setLayout(new GridBagLayout()); //使用卡片包式布局
        bCaptureScreenTester = new JButton("全屏截图");
        bNoCardPaymentTester = new JButton("无卡支付测试");
        bTCPMessageTester = new JButton("TCP报文测试器");
        bTCPMessageMonitor = new JButton("TCP报文监控器");
        bCodeLineCounter = new JButton("代码行数统计器");
        bShareFileReader = new JButton("共享文件读取器");
        GridBagConstraints localGridBagConst = new GridBagConstraints();
        localGridBagConst.gridx = 0; //指定包含组件的显示区域开始边的单元格,其中行的第一个单元格为gridx=0
        localGridBagConst.gridy = 0; //指定位于组件显示区域的顶部的单元格,其中最上边的单元格为gridy=0
        localGridBagConst.insets = new Insets(0, 0, 10, 0); //指定组件的外部填充(顶部,左边,底部,右边)
        pWelcome.add(lClock, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 1;
        pWelcome.add(bCaptureScreenTester, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 2;
        pWelcome.add(bNoCardPaymentTester, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 3;
        pWelcome.add(bTCPMessageTester, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 4;
        pWelcome.add(bTCPMessageMonitor, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 5;
        pWelcome.add(bCodeLineCounter, localGridBagConst);
        localGridBagConst.gridx = 0;
        localGridBagConst.gridy = 6;
        pWelcome.add(bShareFileReader, localGridBagConst);
        /*
         * 处理Welcome页的各个按钮事件
         */
        bCaptureScreenTester.addActionListener(this);
        bNoCardPaymentTester.addActionListener(this);
        bTCPMessageTester.addActionListener(this);
        bTCPMessageMonitor.addActionListener(this);
        bCodeLineCounter.addActionListener(this);
        bShareFileReader.addActionListener(this);
        /*
         * 布局模拟器主窗口
         */
        tabbedPane.addTab("Welcome", pWelcome);
        this.add(tabbedPane, BorderLayout.CENTER);
        this.setTitle(SIMULATOR_NAME);
        this.setJMenuBar(jmb);
        this.setSize(900, 600);
        this.setLocationRelativeTo(null); //居中显示
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }


    /**
     * 点击事件处理
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //时钟
        if(this.timer == e.getSource()){
            Calendar c = new GregorianCalendar();
            String time = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.DATE) + "  ";
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);
            int s = c.get(Calendar.SECOND);
            String ph = h<10 ? "0" : "";
            String pm = m<10 ? "0" : "";
            String ps = s<10 ? "0" : "";
            time += ph + h + ":" + pm + m + ":" + ps + s;
            lClock.setText(time);
            lClock.setForeground(Color.RED);
            lClock.repaint(); //重绘此组件
        //放大
        }else if(this.mi_pop_toBig==e.getSource() || this.mi_toBig==e.getSource()){
            this.setSize(this.getWidth()+100, this.getHeight()+50);
        //缩小
        }else if(this.mi_pop_toSmall==e.getSource() || this.mi_toSmall==e.getSource()){
            if(this.getWidth()>600 && this.getHeight()>400){
                this.setSize(this.getWidth()-100, this.getHeight()-50);
            }else{
                JOptionPane.showMessageDialog(null, "不能再小了，再小就没了！！", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
            }
        //Exit
        }else if(this.mi_quit==e.getSource() || this.mi_pop_exit==e.getSource()){
            System.exit(0);
        //About
        }else if(this.mi_about == e.getSource()){
            JOptionPane.showMessageDialog(null, SIMULATOR_NAME + SIMULATOR_ABOUT, "About", JOptionPane.PLAIN_MESSAGE);
        //全屏截图
        }else if(this.bCaptureScreenTester == e.getSource()){
            this.setExtendedState(Frame.ICONIFIED); //最小化...Frame.MAXIMIZED_BOTH表示水平垂直都最大化,即全屏最大化
            try {
                Thread.sleep(500); //最小化需要时间
            } catch (InterruptedException e1) {
                //..
            }
            String desktop = FileSystemView.getFileSystemView().getHomeDirectory().getPath();
            String separator = System.getProperty("file.separator");
            String imageName = "截屏_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";
            String fileName = desktop + separator + imageName;
            if(JadyerUtil.captureScreen(null, false)){
                JOptionPane.showMessageDialog(null, "截屏完毕，图片已生成为" + fileName, "温馨提示", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(null, "截屏失败，请升级为最新版模拟器", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
            }
            this.setExtendedState(Frame.NORMAL); //从最小化恢复到常态
        //无卡支付测试
        }else if(this.bNoCardPaymentTester == e.getSource()){
            bNoCardPaymentTester.setText("组件初始化...");
            bNoCardPaymentTester.setEnabled(false);
            final String command = e.getActionCommand();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    NoCardPaymentTester pNoCardPaymentTester = new NoCardPaymentTester();
                    tabbedPane.addTab(command, pNoCardPaymentTester);
                    tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(pNoCardPaymentTester));
                    bNoCardPaymentTester.setText("无卡支付测试");
                    bNoCardPaymentTester.setEnabled(true);
                }
            });
            thread.start();
        //TCP报文测试器
        }else if(this.bTCPMessageTester == e.getSource()){
            TCPMessageTester pTCPMessageTestor = new TCPMessageTester();
            tabbedPane.addTab(e.getActionCommand(), pTCPMessageTestor);
            tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(pTCPMessageTestor));
        //TCP报文监控器
        }else if(this.bTCPMessageMonitor == e.getSource()){
            JOptionPane.showMessageDialog(null, "尚需完善之处如下\r\n" +
                    "1）可用来监控TCP／HTTP／WebService报文\r\n" +
                    "2）监控HTTP请求时，报文内容会延迟片刻才显示出来\r\n" +
                    "3）只能对第一次请求进行有效的监控，而无法连续监控多次请求\r\n" +
                    "4）无法有效的解除对本地端口的监听，重启模拟器时才可重复监听该端口", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
            TCPMessageMonitor pTCPMessageMonitor = new TCPMessageMonitor();
            tabbedPane.addTab(e.getActionCommand(), pTCPMessageMonitor);
            tabbedPane.setSelectedIndex(tabbedPane.indexOfComponent(pTCPMessageMonitor));
        //代码行数统计器
        }else if(this.bCodeLineCounter == e.getSource()){
            //设置选择框的默认路径为用户桌面
            final JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory().getPath());
            //设置支持的文件类型
            jfc.addChoosableFileFilter(new FileNameExtensionFilter("支持的文件类型 (*.java;*.xml;*.properties;*.jsp;*.htm;*.html)", "java", "xml", "properties", "jsp", "htm", "html"));
            //允许选择文件或文件夹,其默认值为FILES_ONLY
            //并且默认不允许打开多个文件,可以使用jfc.setMultiSelectionEnabled(true)修改为允许打开多个文件
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            //打开文件对话框
            int returnVal = jfc.showOpenDialog(null);
            //选中文件后点击'打开'按钮时，开始统计代码行数
            if(returnVal == JFileChooser.APPROVE_OPTION){
                this.bCodeLineCounter.setText("正在统计行数....");
                this.bCodeLineCounter.setEnabled(false);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Integer> resultMap = new HashMap<>();
                        resultMap.put("total", 0);
                        resultMap.put("code", 0);
                        resultMap.put("comment", 0);
                        resultMap.put("blank", 0);
                        //boolean isSupportHTML = false;
                        //if(jfc.getFileFilter().getDescription().equals("支持的文件类型 (*.java;*.xml;*.properties;*.jsp;*.htm;*.html)")){
                        //    isSupportHTML = true;
                        //}
                        //JadyerUtil.getCodeLineCounts(jfc.getSelectedFile(), resultMap, isSupportHTML);
                        JadyerUtil.getCodeLineCounts(jfc.getSelectedFile(), resultMap);
                        bCodeLineCounter.setText("行数统计完毕....");
                        JOptionPane.showMessageDialog(null, "合计行数：" + resultMap.get("total") + "\r\n代码行数：" + resultMap.get("code") + "\r\n注释行数：" + resultMap.get("comment") + "\r\n空行行数：" + resultMap.get("blank"), "代码行数统计结果", JOptionPane.INFORMATION_MESSAGE);
                        bCodeLineCounter.setText("代码行数统计器");
                        bCodeLineCounter.setEnabled(true);
                    }
                });
                thread.start();
            }else if(returnVal == JFileChooser.CANCEL_OPTION){
                System.out.println("点击'取消'按钮时什么都不干");
            }
        //共享文件读取
        }else if(this.bShareFileReader == e.getSource()){
            this.getShareFile();
        }
    }


    /**
     * 读取共享文件
     */
    private void getShareFile(){
        final JProgressBar jpbFileLoading = new JProgressBar();
        jpbFileLoading.setPreferredSize(new Dimension(125, 28));
        jpbFileLoading.setIndeterminate(true); //设置进度条为不确定模式,默认为确定模式
        jpbFileLoading.setStringPainted(true);
        jpbFileLoading.setString("文件加载中....");
        bShareFileReader.setMargin(new Insets(0, 0, 0, 0));
        bShareFileReader.setEnabled(false);       //设置按钮不可用
        bShareFileReader.setBorderPainted(false); //设置按钮无边框
        bShareFileReader.add(jpbFileLoading);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String filepath = "192.168.8.57/南天-case/";
                String localDirectory = System.getenv("JAVA_HOME") + "\\" + filepath.substring(filepath.indexOf("/")+1, filepath.lastIndexOf("/")) + "\\";
                if(JCifsUtil.getRemoteFile("wzf", "unicomroot", filepath, localDirectory)){
                    jpbFileLoading.setString("文件加载完毕");
                    bShareFileReader.remove(jpbFileLoading);
                    bShareFileReader.setPreferredSize(new Dimension(125, 28));
                    bShareFileReader.setBorderPainted(true);
                    bShareFileReader.setEnabled(true);
                    JFileChooser jfc = new JFileChooser(localDirectory);
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = jfc.showOpenDialog(null);
                    //选中文件后点击'打开'按钮时,使用本地程序打开文件
                    if(returnVal == JFileChooser.APPROVE_OPTION){
                        try {
                            Desktop.getDesktop().open(new File(localDirectory + jfc.getSelectedFile().getName()));
                        } catch (IOException e) {
                            //nothing to do
                        }
                    }
                }else{
                    jpbFileLoading.setIndeterminate(false);
                    jpbFileLoading.setString("文件加载失败");
                    JOptionPane.showMessageDialog(null, "请开启VPN并确定网络共享已开启", "温馨提示", JOptionPane.INFORMATION_MESSAGE);
                    try {
                        //进度条描述文字显示0.5s后再消失
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bShareFileReader.remove(jpbFileLoading);
                    bShareFileReader.setPreferredSize(new Dimension(125, 28));
                    bShareFileReader.setBorderPainted(true);
                    bShareFileReader.setEnabled(true);
                }
            }
        });
        thread.start();
    }
}
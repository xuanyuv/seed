package com.jadyer.seed.simulator.cronbuilder;

import javax.swing.*;
import java.awt.*;

/**
 * Quartz表达式生成器
 * -----------------------------------------------------------------------------------------------------
 * 感谢wjw465150的开源分享，wjw.cron.ex模块代码整理自https://github.com/wjw465150/CronExpBuilder
 * -----------------------------------------------------------------------------------------------------
 * 用wjw465150的话说就是
 * Quartz的Cron任务调度表达式一般人很难理解，在Googole上查询也没有发现类似的代码
 * 所以开发了一个对Quartz Cron表达式的可视化双向解析和生成的一个java的GUI程序，供使用Quartz的程序员参考和使用
 * -----------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/09/01 21:18.
 */
public class Main {
    public static void main(String[] args) {
        initCustomLAF();
        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run() {
                new MainJFrame().setVisible(true);
            }
        });
    }

    /**
     * Create the custom look and feel
     */
    private static void initCustomLAF(){
        Font font = new Font("Dialog", Font.PLAIN, 12);
        //Color forecolor = new Color(5, 50, 156);
        //Color backcolor = new Color(204,204,255);
        //Color backcolor = new Color(245, 245, 245);
        //Border borderButton = BorderFactory.createEmptyBorder();
//        UIManager.put("MenuBar.background", backcolor);
//        UIManager.put("Menu.background", backcolor);
//        UIManager.put("MenuItem.background", backcolor);
//        UIManager.put("CheckBoxMenuItem.background", backcolor);
//        UIManager.put("ToolBar.background", backcolor);
//        UIManager.put("Panel.background", backcolor);
//        UIManager.put("TabbedPane.background", backcolor);
//        UIManager.put("ComboBox.background", backcolor);
//        UIManager.put("ComboBoxItem.background", backcolor);
//        UIManager.put("RadioButton.background", backcolor);
//        UIManager.put("OptionPane.background", backcolor);
//        UIManager.put("TableHeader.background", backcolor);
//        UIManager.put("Viewport.background", backcolor);
//        UIManager.put("ScrollPane.background", backcolor);
//        UIManager.put("Slider.background", backcolor);
//        UIManager.put("Label.background", Color.white);
//        UIManager.put("CheckBox.background", backcolor);
//        UIManager.put("Button.background", backcolor);
//        UIManager.put("ProgressBar.background", backcolor);
//        UIManager.put("TabbedPane.selected", backcolor);
//        UIManager.put("TabbedPane.selectHighlight", Color.DARK_GRAY);
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ColorChooser.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("ComboBoxItem.font", font);
        UIManager.put("InternalFrame.titleFont", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("MenuBar.font", font);
        UIManager.put("Menu.font", font);
        UIManager.put("MenuItem.font", font);
        UIManager.put("RadioButtonMenuItem.font", font);
        UIManager.put("CheckBoxMenuItem.font", font);
        UIManager.put("PopupMenu.font", font);
        UIManager.put("OptionPane.font", font);
        UIManager.put("Panel.font", font);
        UIManager.put("ProgressBar.font", font);
        UIManager.put("ScrollPane.font", font);
        UIManager.put("Viewport", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("TableHeader.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordFiled.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TitledBorder.font", font);
        UIManager.put("ToolBar.font", font);
        UIManager.put("ToolTip.font", font);
        UIManager.put("Tree.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("Viewport.font", font);
        UIManager.put("Spinner.font", font);
        UIManager.put("PasswordField.font", font);
    }
}
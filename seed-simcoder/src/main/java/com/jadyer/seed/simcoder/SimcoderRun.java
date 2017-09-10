package com.jadyer.seed.simcoder;

import com.jadyer.seed.simcoder.helper.CodeGenHelper;

import javax.swing.filechooser.FileSystemView;

public class SimcoderRun {
    public static void main(String[] args){
        String desktopPath = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + System.getProperty("file.separator");
        //CodeGenHelper.genAllTable(desktopPath, "t_mpp_fans_info", "意见22反馈表");
        CodeGenHelper.genAllDatabase(desktopPath, "mpp");
    }
}
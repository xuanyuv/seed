package com.jadyer.seed.comm.tmp.archive;

import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompressUtil {
    private static boolean unRAR(String rootPath, String sourceRarPath, String destDirPath, String password) {
        String rarDir = rootPath + sourceRarPath;
        String outDir = rootPath + destDirPath + File.separator;
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            // 第一个参数是需要解压的压缩包路径，第二个参数参考JdkAPI文档的RandomAccessFile
            randomAccessFile = new RandomAccessFile(rarDir, "r");
            if (StringUtils.isNotBlank(password)) {
                inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), password);
            }else {
                inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            }
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final int[] hash = new int[]{0};
                if (!item.isFolder()) {
                    ExtractOperationResult result;
                    final long[] sizeArray = new long[1];

                    File outFile = new File(outDir + item.getPath());
                    File parent = outFile.getParentFile();
                    if ((!parent.exists()) && (!parent.mkdirs())) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(password)) {
                        result = item.extractSlow(data -> {
                            try {
                                IOUtils.write(data, new FileOutputStream(outFile, true));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            sizeArray[0] += data.length;
                            return data.length; // Return amount of consumed
                        }, password);
                    } else {
                        result = item.extractSlow(data -> {
                            try {
                                IOUtils.write(data, new FileOutputStream(outFile, true));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            sizeArray[0] += data.length;
                            return data.length; // Return amount of consumed
                        });
                    }

                    if (result == ExtractOperationResult.OK) {
                        System.out.println("解压rar成功......" + String.format("%9X | %10s | %s", hash[0], sizeArray[0], item.getPath()));
                    } else if (StringUtils.isNotBlank(password)) {
                        System.out.println("密码错误或者其他错误......" + result);
                        return false;
                    } else {
                        System.out.println("rar error......");
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            // System.out.println("unRAR error!......");
            // e.printStackTrace();
            return false;
        } finally {
            try {
                inArchive.close();
                randomAccessFile.close();
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }


    /**
     * 注：这个方法不太可靠，有时候它得到的密码，实际解不开压缩包
     */
    public static boolean unRAR22(String rootPath, String sourceRarPath, String destDirPath, String password){
        String rarFilePath = rootPath + sourceRarPath;
        String destinationPath = rootPath + destDirPath + File.separator;

        File rarFile = new File(rarFilePath);
        File destinationFolder = new File(destinationPath);

        try {
            com.github.junrar.Archive archive = new com.github.junrar.Archive(rarFile, password);
            com.github.junrar.rarfile.FileHeader fileHeader = archive.nextFileHeader();

            while (fileHeader != null) {
                String fileName = fileHeader.getFileName().trim();
                File extractedFile = new File(destinationFolder, new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

                if (fileHeader.isDirectory()) {
                    extractedFile.mkdirs();
                } else {
                    extractedFile.getParentFile().mkdirs();
                    FileOutputStream outputStream = new FileOutputStream(extractedFile);
                    archive.extractFile(fileHeader, outputStream);
                    outputStream.close();
                }

                fileHeader = archive.nextFileHeader();
            }

            archive.close();
            System.out.println("RAR file extracted successfully!");
            return true;
            // } catch (IOException | com.github.junrar.exception.RarException e) {
        } catch (Throwable e) {
            // ignore...
        }
        return false;
    }


    public static void unpackMyFile(List<String> prefixList, String rarFilename, String passwordFilename) {
        File file = new File("C:\\Users\\xuanyu\\Desktop\\" + passwordFilename);
        try {
            FileUtils.writeLines(file, Collections.singletonList(passwordFilename), true);
            List<String> middleList = Arrays.asList("!", "@");
            int suffixMin = 0;
            int suffixMax = 999999;
            for (String prefix : prefixList) {
                for (String middle : middleList) {
                    for (int i=suffixMin,len=suffixMax+1; i<len; i++) {
                        String password = prefix + middle + i;
                        if(unRAR("C:\\Users\\xuanyu\\Desktop\\", rarFilename, "", password)){
                            System.out.println("password=[" + password + "]");
                            FileUtils.writeLines(file, Collections.singletonList(password), true);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        // System.out.println(unRAR("C:\\Users\\xuanyu\\Desktop\\", "22.rar", "", "22@123"));
        // System.out.println(unRAR22("C:\\Users\\xuanyu\\Desktop\\", "22.rar", "", "22@123"));

        // ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
        // for (int i=0; i<3; i++) {
        //     int j = i;
        //     fixedThreadPool.execute(() -> {
        //         if(j == 0){
        //             unpackMyFile(Arrays.asList("aa"/*, "aa"*/), "aa.rar", "my_aa.txt");
        //         }
        //         if(j == 1){
        //             unpackMyFile(Arrays.asList("bb"/*, "bb"*/), "bb.rar", "my_bb.txt");
        //         }
        //         if(j == 2){
        //             unpackMyFile(Arrays.asList("cc"/*, "cc"*/), "cc.rar", "my_cc.txt");
        //         }
        //     });
        // }
        // fixedThreadPool.shutdown();

        // ExecutorService fixedThreadPool = Executors.newFixedThreadPool(6);
        // for (int i=0; i<6; i++) {
        //     int j = i;
        //     fixedThreadPool.execute(() -> {
        //         if(j == 0){
        //             unpackMyFile(Collections.singletonList("aa"), "aa.rar", "my_aa.txt");
        //         }
        //         if(j == 1){
        //             unpackMyFile(Collections.singletonList("bb"), "bb.rar", "my_bb.txt");
        //         }
        //         if(j == 2){
        //             unpackMyFile(Collections.singletonList("cc"), "cc.rar", "my_cc.txt");
        //         }
        //         if(j == 3){
        //             unpackMyFile(Collections.singletonList("dd"), "dd.rar", "my_dd.txt");
        //         }
        //         if(j == 4){
        //             unpackMyFile(Collections.singletonList("ee"), "ee.rar", "my_ee.txt");
        //         }
        //         if(j == 5){
        //             unpackMyFile(Collections.singletonList("ff"), "ff.rar", "my_ff.txt");
        //         }
        //     });
        // }
        // fixedThreadPool.shutdown();
    }
}
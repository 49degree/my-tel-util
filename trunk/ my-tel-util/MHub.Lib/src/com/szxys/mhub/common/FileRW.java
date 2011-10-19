/*
 * 文 件 名:  FileRW.java
 * 版    权:  New Element Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  yangzhao
 * 修改时间:  2011-4-7
 * 修改内容:  <修改内容>
 */

package com.szxys.mhub.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import android.os.Environment;
import android.os.StatFs;

/**
 * 对文件进行读写操作 ；<功能详细描述>
 * 
 * @author yangzhao
 * @version [版本号V01, 2011-4-7]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class FileRW
{
    
    public final static int FILE_STATUS_NONE = 0x0004;// 文件为未操作状态
    
    public final static int FILE_STATUS_WRITE = 0x0005;// 文件为写状态
    
    public final static int FILE_STATUS_READ = 0x0006;// 文件状为读状态
    
    public final static int FILE_MODE_XML = 0x0007;// XML文件
    
    public final static int FILE_MODE_COMMON = 0X0008;// 普通文本文件
    
    public final static int STORAGE_SUCCESS = 0x0020;// 存储成功
    
    public final static int DISK_SPACE_FULL = 0x0021;// 磁盘已满
    
    public final static int DISK_SPACE_LESS = 0x0022;// 磁盘空间低
    
    public final static int DISK_NOT_MOUNTED = 0x0023;// 磁盘未挂载
    
    public final static int DISK_DISABLED = 0x0024;// 磁盘错误
    
    public final static int DISK_MOUNTED = 0x0025;// 磁盘已经挂载
    
    public final static int STORAGE_FAILED = 0x0026;// 存储失败
    
    private long nTotalBlocks = 0;// 获取SDCard卡的Block总数
    
    private long nBlocSize = 0;// 获取SDCard上每个block的SIZE
    
    private long nAvailaBlock = 0;// 获取可供程序使用的Block的数量
    
    private long nFreeBlock = 0;// 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
    
    private long nSDTotalSize = 0;// 计算SDCard 总容量大小MB
    
    private long nSDFreeSize = 0;// 计算 SDCard 剩余大小MB
    
    private String sDcString;// 取得SD卡的挂载状态
    
    private Properties props;// properties文件操作对象
    
    private File file, directory;// 文件目录操作对象
            
    private FileOutputStream fileOutputStream;// 写文件流
    
    private FileInputStream fileInputStream;// 读sdcard文件流
    
    private InputStream mInputStream;// 读手机内部存储文件流
    
    private String fCoding = "UTF-8";// 编码
    
    private String fName = "szxys";// 文件名
    
    private String fPath = "";// 相对sdcard根目录的目录名,默认为/sdcard/目录
    
    /**
     * <默认构造函数>
     * 
     * @param path :String 目录名,可为空,默认为"/sdcard/"目录
     * @param filename :String 文件名,可为空,默认为"szxys"
     * @param coding :String 编码，可为空,默认为UTF-8
     */
    public FileRW(String path, String filename, String coding)
    {
        if (path != null && !path.equals(""))
            fPath = path;
        if (filename != null && !filename.equals(""))
            fName = filename;
        if (coding != null && !coding.equals(""))
            fCoding = coding;
    }
    
    /**
     * 在"/sdcard/"目录下建立文件夹 ；文件目录可以为多级目录，如：/szxys/yang 创建构造函数path对应的目录
     * 
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    private boolean mkdir()
    {
        boolean ismkdir = false;
        // 判断SD卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 取得SDcard文件路径
            directory = android.os.Environment.getExternalStorageDirectory();
            String[] dir = fPath.split("/");
            int lenth = dir.length;
            for (int i = 0; i < lenth; i++)
            {
                if (!"".equals(dir[i]))
                {
                    directory = new File(directory, dir[i]);
                    if (!directory.exists())
                    {
                        ismkdir = directory.mkdir();
                        System.out.println("mkdirS: " + directory.getName());
                    }
                }
            }
        }else{
            Close();
        }
        return ismkdir;
        
    }
    
    /**
     * 创建目录 ； 在"/sdcard/"目录下建立文件夹 文件目录可以为多级目录，如：/szxys/yang
     * 
     * @param dirName ：String 任意目录名
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean mkdir(String dirName)
    {
        boolean ismkdir = false;
        // 判断SD卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            // 取得SDcard文件路径
            directory = android.os.Environment.getExternalStorageDirectory();
            if (dirName != null && dirName.equals(""))
            {
                String[] dir = dirName.split("/");
                int lenth = dir.length;
                for (int i = 0; i < lenth; i++)
                {
                    if (!"".equals(dir[i]))
                    {
                        directory = new File(directory, dir[i]);
                        if (!directory.exists())
                        {
                            ismkdir = directory.mkdir();
                            System.out.println("mkdirS: " + directory.getName());
                        }
                    }
                }
            }
        }else{
            Close();
        }
        return ismkdir;
        
    }
    
    /**
     * 删除Sdcard上的文件 ；<功能详细描述>
     * 
     * @param dirName ：String /sdcard/目录的相对路径目录名
     * @param fileName ：String 文件名
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean deleteFile()
    {
        boolean isdelte = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, fPath);
            file = new File(directory, fName);
            isdelte = file.delete();
        }else{
            Close();
        }
        return isdelte;
    }
    
    /**
     * 删除指定目录下指定文件 "<功能详细描述>"
     * 
     * @param path ：String 目录名
     * @param fileName ：String 文件名
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean deleteFile(String path, String fileName)
    {
        boolean isdelte = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, path);
            file = new File(directory, fileName);
            
            if (file != null)
            {
                isdelte = file.delete();
            }
        }else{
            Close();
        }
        return isdelte;
    }
    
    /**
     * 删除当前目录下所有文件 ； 删除构造函数path所对应的目录下的所有文件
     * 
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean deleteFiles()
    {
        int n = 0;// 删除文件计数器
        boolean isdelte = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, fPath);
            String[] files = fileList();
            int lenth = files.length;
            for (int i = 0; i < lenth; i++)
            {
                file = new File(directory, files[i]);
                isdelte = file.delete();
                if (isdelte)
                {
                    n++;
                }
            }
            if (n == files.length)
            {
                isdelte = true;
            }
        }else{
            Close();
        }
        return isdelte;
    }
    
    /**
     * 删除Sdcard任意目录下的所有文件 ； <功能详细描述>
     * 
     * @param path ：String 目录名
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean deleteFiles(String path)
    {
        int n = 0;// 删除文件计数器
        boolean isdelte = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, path);
            String[] files = fileList();
            int lenth = files.length;
            for (int i = 0; i < lenth; i++)
            {
                file = new File(directory, files[i]);
                isdelte = file.delete();
                if (isdelte)
                {
                    n++;
                }
            }
            if (n == files.length)
            {
                isdelte = true;
            }
        }else{
            Close();
        }
        return isdelte;
    }
    
    /**
     * 删除文件夹 ； 删除构造函数path所对应目录
     * 
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean deleteDir()
    {
        boolean isdelte = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, fPath);
            String[] files = fileList();
            int lenth = files.length;
            for (int i = 0; i < lenth; i++)
            {
                file = new File(directory, files[i]);
                isdelte = file.delete();
            }
            isdelte = directory.delete();
        }else{
            Close();
        }
        return isdelte;
    }
    
    /**
     * 删除指定目录 ； <功能详细描述>
     * 
     * @param path ：String 目录名
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean deleteDir(String path)
    {
        boolean isdelte = false;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, path);
            String[] files = fileList();
            int lenth = files.length;
            for (int i = 0; i < lenth; i++)
            {
                file = new File(directory, files[i]);
                isdelte = file.delete();
            }
            isdelte = directory.delete();
        }else{
            Close();
        }
        return isdelte;
    }
    
    /**
     * 列出Sdcard上构造函数path对应的目录下的文件 ； 列出/Sdcard/?/? 目录下的文件名
     * 
     * @return String[] sdcard没有挂载返回null
     * @see [类、类#方法、类#成员]
     */
    public String[] fileList()
    {
        String[] files = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, fPath);
            files = directory.list();
        }else{
            Close();
        }
        return files;
    }
    
    /**
     * 列出Sdcard指定目录下的文件 ； <功能详细描述>
     * 
     * @param path ：String 目录名
     * @return String[]
     * @see [类、类#方法、类#成员]
     */
    public String[] fileList(String path)
    {
        String[] files = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            directory = android.os.Environment.getExternalStorageDirectory();
            directory = new File(directory, path);
            
            files = directory.list();
        }else{
            Close();
        }
        return files;
    }
    
    /**
     * 在Sdcard写入Preperties文件 ；文件格式为（ #node #Thu Apr 21 09:24:20 GMT 2011 k3=3 k4=4 k1=1 k2=2）
     * 
     * @param prop ：Properties
     * @param note ：String 注释内容
     * @param append ：boolean 是否以追加形式写入
     * @param fileMode :int 文件存储类型，xml或普通文本文件
     * @return int
     * @see [FileRW#STORAGE_SUCCESS/STORAGE_FAILED]
     */
    public int WriteFile(Properties prop, String note, boolean append, int fileMode)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (fileOutputStream == null)
                {
                    mkdir();
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    
                    fileOutputStream = new FileOutputStream(file, append);
                    
                }
                if (fileMode == FILE_MODE_COMMON)
                {
                    prop.store(fileOutputStream, note);
                }
                else if (fileMode == FILE_MODE_XML)
                {
                    prop.storeToXML(fileOutputStream, note, fCoding);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return STORAGE_FAILED;
            }
        }
        else
        {
            Close();
       
            return STORAGE_SUCCESS;
        }
        return DISK_NOT_MOUNTED;
    }
    
    /**
     * 从Sdcard读取Properties文件 ；从Sdcard读取普通文本文件，或XML文件
     * 
     * @param fileMode ：int 文件类型
     * @return Properties
     * @see [类、类#方法、类#成员]
     */
    public Properties ReadFile(int fileMode)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (mInputStream == null)
                {
                    props = new Properties();
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    mInputStream = new FileInputStream(file);
                }
                if (fileMode == FILE_MODE_COMMON)
                {
                    props.load(mInputStream);
                }
                else if (fileMode == FILE_MODE_XML)
                {
                    props.loadFromXML(mInputStream);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else{
            Close();
        }
        return props;
        
    }
    
    /**
     * 在Sdcard写入普通文本文件 ； <功能详细描述>
     * 
     * @param str ：String 写入内容
     * @param append ：boolean 写入方式是否为追加
     * @return int
     * @see [FileRW#STORAGE_SUCCESS/STORAGE_FAILED]
     */
    public int WriteFile(String str, boolean append)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (fileOutputStream == null)
                {
                    mkdir();
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    fileOutputStream = new FileOutputStream(file, append);
                }
                byte[] bt = str.getBytes();
                fileOutputStream.write(bt);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return STORAGE_FAILED;
            }
        }
        else
        {
            Close();
        
            return STORAGE_SUCCESS;
        }
        return DISK_NOT_MOUNTED;
    }
    
    /**
     * 在Sdcard写入byte[]文件 ； <功能详细描述>
     * 
     * @param bt :byte[]
     * @param append ：boolean 是否以追加形式写文件
     * @return int
     * @see [FileRW#STORAGE_SUCCESS/STORAGE_FAILED]
     */
    public int WriteFile(byte[] bt, boolean append)
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (fileOutputStream == null)
                {
                    mkdir();
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    fileOutputStream = new FileOutputStream(file, append);
                }
                fileOutputStream.write(bt);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return STORAGE_FAILED;
            }
        }
        else
        {
            Close();
       
            return STORAGE_SUCCESS;
        }
        return DISK_NOT_MOUNTED;
    }
    
    /**
     * 从Sdcard打开整个文本文件 ；
     * 
     * @return String sdcard未挂载返回null；
     * @see [类、类#方法、类#成员]
     */
    
    public String ReadFile()
    {
        byte[] buffer = new byte[0];
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (mInputStream == null)
                {
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    mInputStream = new FileInputStream(file);
                }
                buffer = new byte[mInputStream.available()];
                
                mInputStream.read(buffer);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else{
            Close();
        }
        return EncodingUtils.getString(buffer, fCoding);
    }
    
    /**
     * 读取Sdcard中的普通文本文件 ；<功能详细描述>
     * 
     * @param offset ：int 读取位置
     * @param length ：int 读取长度
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public String ReadFile(int offset, int length)
    {
        byte[] buffer = new byte[0];
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (mInputStream == null)
                {
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    mInputStream = new FileInputStream(file);
                }
                if (offset + length > mInputStream.available())
                {
                    length = mInputStream.available() - offset;
                }
                buffer = new byte[length];
                mInputStream.read(buffer, offset, length);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }else{
            Close();
        }
        return EncodingUtils.getString(buffer, fCoding);
        
    }
    
    /**
     * 读取Sdcard中的byte[]文件 ； 读取全部
     * 
     * @return byte[]
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public byte[] ReadFile_bytes()
    {
        byte[] buffer = new byte[0];
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (mInputStream == null)
                {
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    mInputStream = new FileInputStream(file);
                }
                buffer = new byte[mInputStream.available()];
                mInputStream.read(buffer);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }else{
            Close();
        }
        return buffer;
    }
    
    /**
     * 读取Sdcard中的byte[]文件 ； 读取指定长度
     * 
     * @param offset :int 读取位置
     * @param length ：int 读取长度
     * @param radix :int 进制
     * @return byte[]
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public byte[] ReadFile_bytes(int offset, int length)
    {
        byte[] buffer = new byte[0];
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            try
            {
                if (mInputStream == null)
                {
                    directory = android.os.Environment.getExternalStorageDirectory();
                    file = new File(directory, fPath + File.separator + fName);
                    mInputStream = new FileInputStream(file);
                }
                if (offset + length > mInputStream.available())
                {
                    length = mInputStream.available() - offset;
                }
                buffer = new byte[length];
                mInputStream.read(buffer, offset, length);
            }
            catch (Exception e)
            {
                
                e.printStackTrace();
            }
        }else{
            Close();
        }
        return buffer;
        
    }
    
    /**
     * 关闭输入输出流
     * 
     * @see [类、类#方法、类#成员]
     */
    public void Close()
    {
        try
        {
            if (fileOutputStream != null)
            {
                fileOutputStream.close();
                fileOutputStream = null;
                directory = null;
                file = null;
            }
            
            if (fileInputStream != null)
            {
                fileInputStream.close();
                fileInputStream = null;
                directory = null;
                file = null;
            }
            
            if (mInputStream != null)
            {
                mInputStream.close();
                mInputStream = null;
                directory = null;
                file = null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * 取得SDCard信息 取得SDCard的状态信息，包括可用空间，总大小等 ；<功能详细描述>
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    private int getSDCardState()
    {
        sDcString = android.os.Environment.getExternalStorageState();
        
        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED))
        {
            
            // 取得SDcard文件路径
            File pathFile = Environment.getExternalStorageDirectory();
            
            StatFs statfs = new StatFs(pathFile.getPath());
            
            nTotalBlocks = statfs.getBlockCount();
            
            nBlocSize = statfs.getBlockSize();
            
            nAvailaBlock = statfs.getAvailableBlocks();
            
            nFreeBlock = statfs.getFreeBlocks();
            
            nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
            
            nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
            return DISK_MOUNTED;
        }
        else if (nSDTotalSize == 0)
        {
            return DISK_DISABLED;
        }
        else
        {
            return DISK_NOT_MOUNTED;
        }
        
    }
    
    /**
     * 取得Sdcard空间大小 ； 取得总大小和剩余空间大小，单位为M，磁盘没有挂载返回一个值，为DISK_NOT_MOUNTED
     * 
     * @return long[] ：包含两个返回值，第一个为总大小，第二个为剩余空间
     * @see [类、类#方法、类#成员]
     */
    public long[] getSDCardSize()
    {
        sDcString = android.os.Environment.getExternalStorageState();
        
        if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED))
        {
            
            // 取得SDcard文件路径
            File pathFile = Environment.getExternalStorageDirectory();
            
            StatFs statfs = new StatFs(pathFile.getPath());
            
            nTotalBlocks = statfs.getBlockCount();
            
            nBlocSize = statfs.getBlockSize();
            
            nAvailaBlock = statfs.getAvailableBlocks();
            
            nFreeBlock = statfs.getFreeBlocks();
            
            nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
            
            nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
            long[] sdcardsize = new long[] {nSDTotalSize, nSDFreeSize};
            return sdcardsize;
        }
        else
        {
            return new long[] {DISK_NOT_MOUNTED};
        }
        
    }
    
    /**
     * 获取SDCard上BLOCK总数 ；<功能详细描述>
     * 
     * @return long
     * @see [类、类#方法、类#成员]
     */
    public long getnTotalBlocks()
    {
        getSDCardState();
        return nTotalBlocks;
    }
    
    /**
     * 获取SDCard上每个block的SIZE ；<功能详细描述>
     * 
     * @return long
     * @see [类、类#方法、类#成员]
     */
    public long getnBlocSize()
    {
        getSDCardState();
        return nBlocSize;
    }
    
    /**
     * 获取可供程序使用的Block的数量 ；<功能详细描述>
     * 
     * @return long
     * @see [类、类#方法、类#成员]
     */
    public long getnAvailaBlock()
    {
        getSDCardState();
        return nAvailaBlock;
    }
    
    /**
     * 获取剩下的所有Block的数量 ；包括预留的一般程序无法使用的块
     * 
     * @return long
     * @see [类、类#方法、类#成员]
     */
    public long getnFreeBlock()
    {
        getSDCardState();
        return nFreeBlock;
    }
    
    /**
     * 获得SDCard 总容量大小MB ；<功能详细描述>
     * 
     * @return long
     * @see [类、类#方法、类#成员]
     */
    public long getnSDTotalSize()
    {
        getSDCardState();
        return nSDTotalSize;
    }
    
    /**
     * 获得 SDCard 剩余大小MB ；<功能详细描述>
     * 
     * @return long
     * @see [类、类#方法、类#成员]
     */
    public long getnSDFreeSize()
    {
        getSDCardState();
        return nSDFreeSize;
    }
    
    /**
     * 把值对转为Properties对象 ； <功能详细描述>
     * 
     * @param parameterName ：String key名
     * @param parameterValue ：string value值
     * @return Properties
     * @see [类、类#方法、类#成员]
     */
    public Properties getProperties(String[] parameterName, String[] parameterValue)
    {
        Properties prop = new Properties();
        int lenth = parameterName.length;
        for (int i = 0; i < lenth; i++)
        {
            prop.setProperty(parameterName[i], parameterValue[i]);
        }
        return prop;
    }
}

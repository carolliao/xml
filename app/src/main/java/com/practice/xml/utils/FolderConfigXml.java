package com.practice.xml.utils;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Long on 2019/6/26.
 */

public class FolderConfigXml {
    private static final String TAG = "FolderConfigXml";
    private  String xmlPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "sync_folder_config.xml";
    private final String enter = System.getProperty("line.separator");//换行
    private static volatile  FolderConfigXml mConfigXml = null;
    private FolderConfigXml(){

    }

    public static FolderConfigXml getInstance(){
        if(mConfigXml == null) {
            synchronized (FolderConfigXml.class) {
                if (mConfigXml == null) {
                    mConfigXml = new FolderConfigXml();
                }
            }
        }

        return mConfigXml;
    }

    public synchronized boolean exists(){
        return new File(xmlPath).exists();
    }

    /**
     * 增加。如果存在相同的文件夹，执行替换操作
     * @param folder
     */
    public synchronized void add(Folder folder){
        List<Folder> folders = read();
        if(folders == null){
            folders = new ArrayList<>();
        }else{
            for(int i = 0; i < folders.size(); i++){
                Folder f = folders.get(i);
                if(f.path.equals(folder.path)){
                    folders.remove(i);
                    break;
                }
            }
        }
        folders.add(folder);
        write(folders);
    }

    public synchronized void add(List<Folder> folders){
        List<Folder> folderList = read();
        if(folderList == null){
            folderList = new ArrayList<>();
        }else{
            for(int i = 0; i < folderList.size(); i++){
                Folder f = folderList.get(i);
                for(int j = 0; j < folders.size(); j++) {
                    Folder folder = folders.get(j);
                    if (f.path.equals(folder.path)) {
                        folderList.remove(i);
                        i--;
                        break;
                    }
                }
            }

        }
        folderList.addAll(folders);
        write(folderList);
    }
    /**
     * 查询文件夹。只有文件夹的绝对路径是唯一的
     * @param path
     * @return
     */
    public synchronized Folder query(final String path){
        List<Folder> folders = read();
        if(folders != null){
            for (Folder folder:
                    folders) {
                if(folder.path.equals(path)){
                    return folder;
                }
            }
        }

        return null;
    }

    public synchronized List<Folder> query(){
        return read();
    }

    /**
     * 更新。不存在不做添加操作
     * @param folder
     * @return 不存在返回false
     */
    public synchronized boolean update(Folder folder){
        boolean result = false;
        List<Folder> folders = read();
        if(folders != null){
            for (Folder f:
                    folders) {
                if(f.path.equals(folder.path)){
                    f.path = folder.path;
                    f.rescanIntervalS = folder.rescanIntervalS;
                    f.needFiles = folder.needFiles;
                    result = true;
                    break;
                }
            }
            if(result){
                write(folders);
            }
        }

        return result;
    }


    public synchronized boolean delete(final String path){
        boolean result = false;
        List<Folder> folders = read();
        if(folders != null){
            for(int i = 0; i < folders.size(); i++){
                Folder f = folders.get(i);
                if(f.path.equals(path)){
                    folders.remove(i);
                    result = true;
                    break;
                }
            }
            if(result){
                write(folders);
            }
        }

        return result;
    }

    private void write(List<Folder> folders){
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlPath,false);
            //通过 Xml 类创建一个 Xml 序列化器
            XmlSerializer xmlSerializer = Xml.newSerializer();
            //给序列化器设置输出流和输出流编码
            xmlSerializer.setOutput(fos, "utf-8");
             /* 让序列化器开发写入 xml 的头信息，其本质是写入内容：
            * "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>"    */
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.text(enter);
             /*
                开始写入标签
              */
            xmlSerializer.startTag(null, "folders");
            xmlSerializer.text(enter);
            Log.i(TAG, "folders size = " + folders.size());
            for(int i = 0; i < folders.size(); i++){
                Folder folder = folders.get(i);
                Log.i(TAG, "write i-->" + i + " " + folder.toString());
                //开始 folder 标签
                xmlSerializer.startTag(null, "folder");
                xmlSerializer.text(enter);
                //开始name标签
                xmlSerializer.startTag(null, "name");
                xmlSerializer.text(folder.name);
                xmlSerializer.endTag(null, "name");
                xmlSerializer.text(enter);

                //开始path标签
                xmlSerializer.startTag(null, "path");
                //在 path 标签中间写入文本
                xmlSerializer.text(folder.path);
                //结束path标签
                xmlSerializer.endTag(null, "path");
                xmlSerializer.text(enter);


                //开始 rescanIntervalS标签
                xmlSerializer.startTag(null, "rescanIntervalS");
                //在 rescanIntervalS 标签中间写入文本
                xmlSerializer.text(String.valueOf(folder.rescanIntervalS));
                //结束 rescanIntervalS 标签
                xmlSerializer.endTag(null, "rescanIntervalS");
                xmlSerializer.text(enter);

                //开始needFiles标签
                if(folder.needFiles != null && folder.needFiles.length > 0) {
                    xmlSerializer.startTag(null, "needFiles");
                    StringBuffer sb = new StringBuffer();
                    final int iMax = folder.needFiles.length - 1;
                    for (int j = 0; ; j++) {
                        sb.append(folder.needFiles[j]);
                        if(j == iMax){
                            break;
                        }
                        sb.append(" ");
                    }
                    xmlSerializer.text(sb.toString());
                    xmlSerializer.endTag(null, "needFiles");
                    xmlSerializer.text(enter);
                }
                //结束folder标签
                xmlSerializer.endTag(null, "folder");
                xmlSerializer.text(enter);
            }
            xmlSerializer.text(enter);
            //结束 folders 标签
            xmlSerializer.endTag(null, "folders");
            //结束文档
            xmlSerializer.text(enter);
            xmlSerializer.endDocument();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //使用pull进行解析
    private List<Folder> read(){
        /* 将解析出来的数据封装在 Folder 中，然后保存到 ArrayList 中 Folder 是自定义的一个 JavaBean  */
        List<Folder> folders = null;
        Folder folder = null;
        //打开xml文件
        InputStream fis = null;
        //创建xml解析器
        XmlPullParser xmlPullParser = Xml.newPullParser();
        //关联解析器和文件输入流
        try {
            fis = new FileInputStream(xmlPath);
            xmlPullParser.setInput(fis, "utf-8");
            //获取事件类型
            int event = xmlPullParser.next();
            while(event != XmlPullParser.END_DOCUMENT){
                //获取当前解析到的标签
                String tagName = xmlPullParser.getName();
                Log.i(TAG, "event-->" + event + " XmlPullParser tagName = " + tagName);
                //如果是"开始标签"
                if(event == XmlPullParser.START_TAG){
                    if("folders".equals(tagName)){
                        folders = new ArrayList<>();
                    }else if("folder".equals(tagName)){
                        folder = new Folder();
                    }else if("name".equals(tagName)){
                        folder.name = xmlPullParser.nextText();
                    }else if("path".equals(tagName)){
                        folder.path = xmlPullParser.nextText();
                    }else if("rescanIntervalS".equals(tagName)){
                        folder.rescanIntervalS = Integer.valueOf(xmlPullParser.nextText());
                    }else if("needFiles".equals(tagName)){
                        folder.needFiles = xmlPullParser.nextText().split(" ");
                    }
                }
                //如果是"结束标签"
                else if(event == XmlPullParser.END_TAG){
                    if(folders != null && "folder".equals(tagName)){
                        folders.add(folder);
                    }
                }
                //获取下一个事件
                event = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return folders;
    }
}

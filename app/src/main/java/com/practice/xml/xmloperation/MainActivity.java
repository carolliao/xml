package com.practice.xml.xmloperation;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.practice.xml.utils.Folder;
import com.practice.xml.utils.FolderConfigXml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Folder> folders = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            Folder folder = new Folder();
            folder.name = "顶层文件夹" + i;
            folder.path = Environment.getExternalStorageDirectory().getPath() + File.separator + folder.name;
            //folder.path = "path" + i;
            folder.rescanIntervalS = (10 + i);
            if(i == 1) {
                folder.needFiles = new String[]{".txt", ".jpg"};
            }
            folders.add(folder);
        }
        FolderConfigXml.getInstance().add(folders);
        List<Folder> readFolders = FolderConfigXml.getInstance().query();
        if(readFolders != null){
            for(int i = 0; i < readFolders.size(); i++){
                Folder folder = readFolders.get(i);
                Log.i(TAG, "readFolders i-->" + i + " " + folder.toString());
            }
            //修改其中一个再回写回去
            if(!readFolders.isEmpty()) {
                Folder folder = readFolders.get(0);
                //folder.name = "修改顶层文件夹";
                //folder.path = Environment.getExternalStorageDirectory().getPath() + File.separator + folder.name;
                //folder.path = "ppath";
                folder.rescanIntervalS = 100;
                FolderConfigXml.getInstance().update(folder);
            }
        }
        //删除一个文件夹
        FolderConfigXml.getInstance().delete(folders.get(3).path);

    }
}

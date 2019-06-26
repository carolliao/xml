package com.practice.xml.utils;

import java.util.Arrays;

/**
 * 需要上传的文件夹信息
 * Created by Long on 2019/6/20.
 */

public class Folder {
    public String name; //文件夹名字
    public String path; //文件夹绝对路径
    public int rescanIntervalS; //重复检查间隔时间 单位s
    public String[] needFiles;//只扫描该文件夹下指定类型的文件，默认是需要Folder下的所有文件

    @Override
    public String toString() {
        return "Folder{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", rescanIntervalS=" + rescanIntervalS +
                ", needFiles=" + Arrays.toString(needFiles) +
                '}';
    }
}

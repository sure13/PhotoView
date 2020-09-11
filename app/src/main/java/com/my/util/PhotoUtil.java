package com.my.util;

import android.widget.TextView;

import com.my.R;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class PhotoUtil {


    private static String cureentPath;
    private static ArrayList<String> cureentPhotoList = new ArrayList<>();
    private static List<String> currentList = new ArrayList<>();

    public static boolean isPictureFile(File file){
        String name = file.getName().toUpperCase();
        if ((!name.startsWith("."))
                && ((name.endsWith(".JPG")) ||(name.endsWith(".BMP")) || (name.endsWith(".PNG")) || (name.endsWith(".GIF")))){
            return true;
        }
        return false;
    }


    public static ArrayList<String> getLocalPhotoList(String path) {
        File file = new File(path);
        if (file == null || !file.exists()){
            return null;
        }
        File[] list = file.listFiles();
        if ((list != null) && (list.length > 0)){
            for (File file1:list){
                if (file1.isDirectory()){
                    cureentPath = path + "/" + file1.getName();
                    getLocalPhotoList(cureentPath);
                }else if(isPictureFile(file1)){
                    cureentPhotoList.add(cureentPath + "/" + file1.getName());
                }
            }
        }
        return cureentPhotoList;
    }


    public static List<String> getPhotoList(String currentPath) {
        File file = new File(currentPath);
        if (file == null || !file.exists()){
            return null;
        }
        currentList.clear();
        File[] list = file.listFiles();
        if ((list != null) && (list.length > 0)){
            for (File file1:list){
                if(isPictureFile(file1)){
                    currentList.add(currentPath + "/" + file1.getName());
                }
            }
        }
        return currentList;
    }


 }

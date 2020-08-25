package com.my.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.my.R;
import com.my.adapter.MyAdapter;
import com.my.bean.SimpleData;
import com.my.dialog.PhotoDialog;
import com.my.util.LogUtil;
import com.my.util.MediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
//    private String img1 = "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3933355596,669391437&fm=26&gp=0.jpg";
//    private String img2 = "http://img4.imgtn.bdimg.com/it/u=1445674920,12445384&fm=26&gp=0.jpg";
//    private String img3 = "http://img4.imgtn.bdimg.com/it/u=482593725,2192787331&fm=26&gp=0.jpg";
//    private String img4 = "http://img5.imgtn.bdimg.com/it/u=2306608262,3534874273&fm=26&gp=0.jpg";
//    private String img5 = "http://img3.imgtn.bdimg.com/it/u=1827863856,2661023953&fm=26&gp=0.jpg";
//    private String img6 = "http://img0.imgtn.bdimg.com/it/u=2775335106,966369936&fm=26&gp=0.jpg";
//    private String img7 = "http://img4.imgtn.bdimg.com/it/u=1881496596,2607061598&fm=26&gp=0.jpg";
//    private String img8 = "http://img2.imgtn.bdimg.com/it/u=3129434724,2523766044&fm=26&gp=0.jpg";
//    private String img9 = "http://img0.imgtn.bdimg.com/it/u=1199886885,3035315889&fm=26&gp=0.jpg";

    private String img10 = "http://img3.imgtn.bdimg.com/it/u=2911588266,3715444639&fm=26&gp=0.jpg";
    private String img11 = "http://img2.imgtn.bdimg.com/it/u=1823088812,4234717774&fm=26&gp=0.jpg";
    private String img12 = "http://img0.imgtn.bdimg.com/it/u=1196506210,3510975282&fm=26&gp=0.jpg";
    private String img13 = "http://fms.news.cn/swf/2019_qmtt/10_8_2019_qm_z/images/5.jpg";

    private ArrayList<String> imageData = new ArrayList<>();
    private List<SimpleData> datas = new ArrayList<>();
    private MyAdapter adapter;
    private ImageView ivMain;
    private String UsbPath;
    private ArrayList<File> cureentDirectoryList = new ArrayList<>();
    private ArrayList<String> cureentPhotoList = new ArrayList<>();

    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE};
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        checkActivityPermission();
        initData();
        initView();
        initListener();
    }

    private void checkActivityPermission() {
        int writePermission = checkSelfPermission(permissions[0]);
        int readPermission = checkSelfPermission(permissions[1]);
        if ((writePermission == PackageManager.PERMISSION_GRANTED )&& (readPermission == PackageManager.PERMISSION_GRANTED)){

        }else{
            requestPermissions(permissions,100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Log.i("wj","----request permissions");
            }else{
                Log.i("wj","---- sucessful");
                initData();
            }
        }
    }

    private void initData() {
        SimpleData data = new SimpleData();
        data.setTitle("本地图片");
        UsbPath = MediaUtil.getUsbPath(context);
        getLocalPhotoList(UsbPath);
//        imageData.add(img1);
//        imageData.add(img2);
//        imageData.add(img3);
//        imageData.add(img4);
//        imageData.add(img5);
//        imageData.add(img6);
//        imageData.add(img7);
//        imageData.add(img8);
//        imageData.add(img9);
//        data.setImgs(imageData);
        data.setImgs(cureentPhotoList);

        datas.add(0, data);

        SimpleData data2 = new SimpleData();
        data2.setTitle("EVA");
        ArrayList<String> childData = new ArrayList<>();
        childData.add(img10);
        childData.add(img11);
        childData.add(img12);
        childData.add(img13);
        data2.setImgs(childData);

        datas.add(1, data2);

    }

    private String cureentPath;


    public void getLocalPhotoList(String path) {
        Log.i("wj","-----------path---------" + path);
        File file = new File(path);
        if (file == null || !file.exists()){
            return;
        }
        File[] list = file.listFiles();
        if ((list != null) && (list.length > 0)){
            for (File file1:list){
                if (file1.isDirectory()){
                   cureentPath = path + "/" + file1.getName();
                    getLocalPhotoList(cureentPath);
                }else if(isPictureFile(file1)){
                    cureentPhotoList.add(cureentPath + "/" + file1.getName());
                    Log.i("wj","--------------add------"+file1.getName());
                }
            }
       }

    }

    public boolean isPictureFile(File file){
        String name = file.getName().toUpperCase();
        if ((!name.startsWith("."))
                && ((name.endsWith(".JPG")) ||(name.endsWith(".BMP")) || (name.endsWith(".PNG")) || (name.endsWith(".GIF")))){
            return true;
        }
        return false;
    }

    private void initView() {
        recyclerView = findViewById(R.id.rv_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setData(datas);
    }

    private void initListener() {
        adapter.setCallBack(new MyAdapter.OnMyAdapterCallBack() {
            @Override
            public void onItemClick(int position, ArrayList<String> itemImgs) {
                Bundle bundle = new Bundle();
                ArrayList<String> data = new ArrayList<>();
                data.addAll(itemImgs);
                bundle.putInt("currentPostion", position);
                bundle.putStringArrayList("imageData", itemImgs);

                PhotoDialog photoDialog = new PhotoDialog();
                photoDialog.setArguments(bundle);
                photoDialog.show(getSupportFragmentManager(), "");
            }
        });
    }
}

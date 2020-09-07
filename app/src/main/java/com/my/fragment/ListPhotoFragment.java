package com.my.fragment;


import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.my.R;
import com.my.dao.Dao;
import com.my.util.MediaUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ListPhotoFragment extends Fragment {

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    private WeakReference<Context> weakReference;
    public static  ListPhotoFragment listFragment;
    private Context context;

    private RecyclerView recyclerView;

    private String UsbPath;
    private String cureentPath;
    private List<String> cureentList;

    public static final int REQUEST_CODE = 1;

    public ListPhotoFragment(Context context){
        weakReference = new WeakReference<>(context);
        this.context = context;
    }

    public static ListPhotoFragment getInstance(Context context){
        if (listFragment == null){
            listFragment = new ListPhotoFragment(context);
        }
        return listFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (checkAlertPermission(context)) {
            initData();
            getPhotoListInfo(UsbPath);
        }else{
            requestAlertWindowPermission();
        }
        Log.i("wj","----------onActivityCreated------------------");
    }

    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
                    initData();
                    getPhotoListInfo(UsbPath);
                }
            }
        }
    }

    public boolean checkAlertPermission(Context context){
        Boolean result = true;
        if (Build.VERSION.SDK_INT >= 23){
            try {
                Class classz = Settings.class;
                Method method = classz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) method.invoke(null,context);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    private void initData() {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.packageName = context.getPackageName();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        //大于8.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.gravity = Gravity.CENTER;
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.item_one, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.item_one_rv);
        windowManager.addView(view,layoutParams);
        UsbPath = MediaUtil.getUsbPath(context);
        cureentList = new ArrayList<>();
    }




    private void getPhotoListInfo(String path) {
        File file = new File(path);
        if (file == null || !file.exists()){
            return;
        }
        File[] list = file.listFiles();
            if ((list != null) && (list.length > 0)){
                for (File file1:list){
                    if (file1.isDirectory()){
                        cureentPath = path + "/" + file1.getName();
                   //     Log.i("wxy","--------1-------"+cureentPath);
                        getPhotoListInfo(cureentPath);
                    }else if(isPictureFile(file1)){
                        cureentPath = path +"/";
                        saveCurrentPath(cureentPath);
                  //      Log.i("wxy","--------2-------"+cureentPath);
                    }
                }
            }
    }

    private void saveCurrentPath(String cureentPath) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.action.myprovider/photo_info");
        ContentValues contentValues = new ContentValues();
        contentValues.put(Dao.PATH,cureentPath);
        contentResolver.insert(uri,contentValues);
    }

    public boolean isPictureFile(File file){
        String name = file.getName().toUpperCase();
        if ((!name.startsWith("."))
                && ((name.endsWith(".JPG")) ||(name.endsWith(".BMP")) || (name.endsWith(".PNG")) || (name.endsWith(".GIF")))){
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        getPathList();
        Log.i("wxy","------------size-----------"+cureentList.size());
        super.onStart();
    }

    private void getPathList() {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.action.myprovider/photo_info");
        Cursor cursor = contentResolver.query(uri,null,null,null,null,null);
        if (cursor != null && cursor.moveToFirst() ){
            do {
                String path = cursor.getString(cursor.getColumnIndex(Dao.PATH));
                if (!cureentList.contains(path)) {
                    Log.i("wxy","----------path------------"+path);
                    cureentList.add(path);
                }
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    public class MyAdapter extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item ,parent ,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof MyHolder){

            }
        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }


    public class MyHolder extends RecyclerView.ViewHolder{
        private TextView pathText;
        private TextView totalText;
        private ImageView coverImage;
        private LinearLayout linearLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            pathText = (TextView) itemView.findViewById(R.id.path);
            totalText = (TextView) itemView.findViewById(R.id.total);
            coverImage = (ImageView) itemView.findViewById(R.id.cover);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearlayout);
        }
    }
}

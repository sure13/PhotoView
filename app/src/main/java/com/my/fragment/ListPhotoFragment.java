package com.my.fragment;


import android.content.ContentResolver;
import android.content.Context;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.my.R;
import com.my.activity.PhotoActivity;
import com.my.util.MediaUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListPhotoFragment extends Fragment {


    private WeakReference<Context> weakReference;
    public static  ListPhotoFragment listFragment;
    private Context context;

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private LinearLayoutManager linearLayoutManager;
    private TextView toastText;

    private String UsbPath;
    private ContentResolver contentResolver;
    private ArrayList<String> paths ; //图片的路径列表/所有图片的集合（/storage/8EFB-822B/图片/xxx.png（jpg））
    private List<String> parentDirs ;//图片所在的路径/所有路径的集合(图片)
    private List<String> parentImage;//图片所在的绝对路径(/storage/8EFB-822B/图片/)
    private View view;


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
        view = LayoutInflater.from(context).inflate(R.layout.item_one,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.item_one_rv);
        toastText = (TextView) view.findViewById(R.id.toast_text);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    private void initData() {
        paths = new ArrayList<>();
        parentDirs = new ArrayList<>();
        parentImage= new ArrayList<>();
        contentResolver = context.getContentResolver();
        UsbPath = MediaUtil.getUsbPath(context);
        if (UsbPath == null || UsbPath.equals("")){
            toastText.setVisibility(View.VISIBLE);
        }else{
            toastText.setVisibility(View.GONE);
            getImage();
            myAdapter = new MyAdapter(context,parentDirs,parentImage);
            linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(myAdapter);
            myAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    Intent intent = new Intent(context,PhotoActivity.class);
                    intent.putStringArrayListExtra("paths",paths);
                    intent.putExtra("dirName",parentImage.get(position));
                    context.startActivity(intent);
                }
            });
        }

    }

    private void getImage() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 获得图片
        Cursor mCursor = contentResolver.query(mImageUri, null,
                MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                new String[] { "image/jpeg", "image/png" },MediaStore.Images.Media.DATE_MODIFIED);

        while (mCursor.moveToNext()){
            String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));// 路径
            paths.add(path);
            //获取到本机中所有的图片后，要对图片进行分类，因此通过路径中的parentDir文件来问分类
            File file = new File(path);
            File parentFile= file.getParentFile();
            String parentFileString = parentFile.getAbsolutePath();
            String ParentFileName = parentFileString.substring(parentFileString.lastIndexOf("/")+1);
            if (parentDirs.contains(ParentFileName)){
                continue;
            }else {
                parentImage.add(parentFile.toString());
                parentDirs.add(ParentFileName);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    public class MyAdapter extends RecyclerView.Adapter{

        private Context context;
        private List<String> parentDirs;
        private List<String> parentImage;
        private List<String> currentList;

        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        public MyAdapter(Context context,List<String> parentDirs, List<String> parentImage){
            this.context = context;
            this.parentDirs = parentDirs;
            this.parentImage = parentImage;
            currentList = new ArrayList<>();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_item ,parent ,false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof MyHolder){
                ((MyHolder) holder).pathText.setText(parentDirs.get(position));
                Log.i("wxy","-------------position--------------" + position);
                String currentPath = parentImage.get(position);
                getLocalPhotoList(currentPath);
                ((MyHolder) holder).totalText.setText("一共" + currentList.size() + "张图片");
                Glide.with(context).load(currentList.get(0)).into(((MyHolder) holder).coverImage);
                ((MyHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null){
                            onItemClickListener.onItemClick(position);
                        }
//                        for (int i=0;i<photoList.size();i++){
//                            Log.i("wxy","-------------item--------------"+photoList.get(i));
//                        }
//                        Intent intent = new Intent(context, PhotoActivity.class);
//                        Bundle  bundle = new Bundle();
//                        bundle.putSerializable("data", (Serializable) photoList);
//                        intent.putExtras(bundle);
//                //        intent.putStringArrayListExtra("data", (ArrayList<String>) photoList);
//                        context.startActivity(intent);
                    }
                });
            }
        }

        private void getLocalPhotoList(String currentPath) {
                File file = new File(currentPath);
                if (file == null || !file.exists()){
                    return;
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
        }

        @Override
        public int getItemCount() {
            return parentDirs.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
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

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}

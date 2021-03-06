package com.my.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.my.R;
import com.my.adapter.PhotoAdapter;
import com.my.dialog.PhotoShowActivity;
import com.my.util.MediaUtil;
import com.my.util.PhotoUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LocalPhotoFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private TextView toastText;

    private String UsbPath;
    private String cureentPath;
    private ArrayList<String> cureentPhotoList;
    private Context context;

    private boolean isShowCheckbox;//是否显示Checkbox
    private List<String> selectPositionList;//记录选中的Checkbox

    private static LocalPhotoFragment localPhotoFragment;
    public  WeakReference<Context> weakReference;
    private ProgressDialog progressDialog;

    public IntentPhotoFragment.CallBackListener callBackListener;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE };

    public LocalPhotoFragment(Context context){
        weakReference = new WeakReference<>(context);
        this.context = context;
    }


    public static LocalPhotoFragment getInstance(Context context){
        if (localPhotoFragment == null){
            localPhotoFragment = new LocalPhotoFragment(context);
        }
        return localPhotoFragment;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBackListener = (IntentPhotoFragment.CallBackListener) getActivity();
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
        checkActivityPermission();
        initData();
        refushUI();
        initListener();
    }

    private void checkActivityPermission() {
        int writePermission = context.checkSelfPermission(permissions[0]);
        int readPermission = context.checkSelfPermission(permissions[1]);
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
    private void refushUI() {
        if (adapter == null){
            adapter = new PhotoAdapter(context,cureentPhotoList);
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }


    }

    private void initData() {
         UsbPath = MediaUtil.getUsbPath(context);
         cureentPhotoList = new ArrayList<>();
        if (UsbPath == null || UsbPath.equals("")){
            toastText.setVisibility(View.VISIBLE);
        }else{
            cureentPhotoList = PhotoUtil.getLocalPhotoList(UsbPath);
            selectPositionList = new ArrayList<>();
            isShowCheckbox = false;
            toastText.setVisibility(View.GONE);
            adapter = new PhotoAdapter(context,cureentPhotoList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context,6);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);

        }

    }


    private void initListener() {

        adapter.setOnChildClick(new PhotoAdapter.OnChildClick() {
            @Override
            public void onItemClick(int position, List<String> itemImgs) {
                if (isShowCheckbox){
                    if (selectPositionList.contains(String.valueOf(position))){
                        selectPositionList.remove(String.valueOf(position));
                        refushUI();
                    }else{
                        selectPositionList.add(String.valueOf(position));
                    }
                    Collections.sort(selectPositionList);
                }else {
                    Bundle bundle = new Bundle();
                    ArrayList<String> data = new ArrayList<>();
                    data.addAll(itemImgs);
                    bundle.putInt("currentPostion", position);
                    bundle.putStringArrayList("imageData", (ArrayList<String>) itemImgs);

                    Intent intent = new Intent(context, PhotoShowActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                if (isShowCheckbox){
                    if (callBackListener != null){
                        callBackListener.showAndHideStatubar(false);
                    }
                    adapter.setShowCheckbox(false);
                    refushUI();
                    selectPositionList.clear();
                }else{
                    if (callBackListener != null){
                        callBackListener.showAndHideStatubar(true);
                    }
                    adapter.setShowCheckbox(true);
                    refushUI();
                }
                isShowCheckbox = !isShowCheckbox;
                return true;
            }
        });
    }

    public void deleteAllSeletePosition(){
        if (selectPositionList != null){
            showDialog();
        }
    }

    public AlertDialog dialog = null;
    private void showDialog() {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("是否删除此图片？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectPositionList.size() == cureentPhotoList.size()){
                    adapter.deleteAll();
                }else{
                    for (int j = 0;j<selectPositionList.size();j++){
                        String position = selectPositionList.get(j);
                        int pos = Integer.valueOf(position);
                        if (j == 0){
                            adapter.deleteSelectPosition(pos);
                        }
                        else{
                            adapter.deleteSelectPosition(pos -j);
                        }
                    }
                }
                callBackListener.showAndHideStatubar(false);
                adapter.setShowCheckbox(false);
                isShowCheckbox = false;
                refushUI();
                selectPositionList.clear();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.setShowCheckbox(true);
                isShowCheckbox = true;
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }



    public void selectAllOrNot(boolean isAll) {
        if (isAll){
            isShowCheckbox = true;
            adapter.setShowCheckbox(true);
            adapter.setAllSelect(true);
            for(int i = 0;i<cureentPhotoList.size();i++){
                selectPositionList.add(String.valueOf(i));
            }
            refushUI();
        }else{
            adapter.setAllSelect(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },6000);

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

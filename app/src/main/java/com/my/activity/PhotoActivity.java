package com.my.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my.R;
import com.my.adapter.PhotoAdapter;
import com.my.dialog.PhotoDialog;
import com.my.dialog.PhotoShowActivity;
import com.my.util.PhotoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoActivity  extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private Button chooseButton;
    private Button deleteButton;
    private RelativeLayout bottomRelativeLayout;
    private GridLayoutManager gridLayoutManager;

    private boolean isShowCheckbox;//是否显示Checkbox
    private List<String> selectPositionList;//记录选中的Checkbox
    private Context context;

    private ArrayList<? extends String> paths = new ArrayList<>();
    private String dirName;
    private List<String> images = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.photo_activity;
    }

    @Override
    protected void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        chooseButton = (Button) findViewById(R.id.choose);
        deleteButton = (Button) findViewById(R.id.delete_all);
        bottomRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_statubar);
    }

    @Override
    protected void initData() {
        context = getApplicationContext();
        Intent intent = getIntent();
        paths = intent.getParcelableArrayListExtra("paths");
        dirName = intent.getStringExtra("dirName");
        images = PhotoUtil.getPhotoList(dirName);
        selectPositionList = new ArrayList<>();
        isShowCheckbox = false;
        photoAdapter = new PhotoAdapter(context,images);
        gridLayoutManager = new GridLayoutManager(context,6);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    protected void initListener() {
        photoAdapter.setOnChildClick(new PhotoAdapter.OnChildClick() {
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

                    Intent intent = new Intent(PhotoActivity.this, PhotoShowActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public boolean onItemLongClick(int position) {
                if (isShowCheckbox){
                    showAndHideStatubar(false);
                    photoAdapter.setShowCheckbox(false);
                    refushUI();
                    selectPositionList.clear();
                }else{
                    showAndHideStatubar(true);
                    photoAdapter.setShowCheckbox(true);
                    refushUI();
                }
                isShowCheckbox = !isShowCheckbox;
                return true;
            }
        });
        chooseButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
    }




    private void refushUI() {
        if (photoAdapter == null){
            photoAdapter = new PhotoAdapter(context,images);
            recyclerView.setAdapter(photoAdapter);
        }else {
            photoAdapter.notifyDataSetChanged();
        }
    }


    public void deleteAllSeletePosition(){
        if (selectPositionList != null){
            showDialog();
        }
    }

    public AlertDialog dialog = null;
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
        builder.setMessage("是否删除此图片？");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selectPositionList.size() == images.size()){
                    photoAdapter.deleteAll();
                }else{
                    for (int j = 0;j<selectPositionList.size();j++){
                        String position = selectPositionList.get(j);
                        int pos = Integer.valueOf(position);
                        if (j == 0){
                            photoAdapter.deleteSelectPosition(pos);
                        }
                        else{
                            photoAdapter.deleteSelectPosition(pos -j);
                        }
                    }
                }
                showAndHideStatubar(false);
                photoAdapter.setShowCheckbox(false);
                isShowCheckbox = false;
                refushUI();
                selectPositionList.clear();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                photoAdapter.setShowCheckbox(true);
                isShowCheckbox = true;
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }



    public void selectAllOrNot(boolean isAll) {
        if (isAll){
            isShowCheckbox = true;
            photoAdapter.setShowCheckbox(true);
            photoAdapter.setAllSelect(true);
            for(int i = 0;i<images.size();i++){
                selectPositionList.add(String.valueOf(i));
            }
            refushUI();
        }else{
            photoAdapter.setAllSelect(false);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public  void showAndHideStatubar(boolean show){
        if (show){
            bottomRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            bottomRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose:
                chooseButton.setSelected(true);
                deleteButton.setSelected(false);
                setTextViewColor(true, chooseButton);
                setTextViewColor(false, deleteButton);
                selectAllOrNot(true);
                break;
            case R.id.delete_all:
                chooseButton.setSelected(false);
                deleteButton.setSelected(true);
                setTextViewColor(false, chooseButton);
                setTextViewColor(true, deleteButton);
                deleteAllSeletePosition();
                break;
        }
    }

    public void setTextViewColor(boolean bool, TextView textView){
        if (bool){
            textView.setTextColor(getColor(R.color.colorAccent));
        }else {
            textView.setTextColor(getColor(R.color.black));
        }
    }
}

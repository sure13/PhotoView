package com.my.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my.R;
import com.my.fragment.IntentPhotoFragment;
import com.my.fragment.LocalPhotoFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, IntentPhotoFragment.CallBackListener {


    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE};
    private Context context;

    private FrameLayout frameLayout;
    private TextView localText;
    private TextView intentText;
    private LocalPhotoFragment localPhotoFragment;
    private IntentPhotoFragment intentPhotoFragment;
    private Button chooseButton;
    private Button deleteButton;
    public   RelativeLayout bottomRelativeLayout;
    public Fragment cureentFragment;
    private TextView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        initView();
        initData();
        initListener();
    }

    private void initData() {
        if (localPhotoFragment == null){
            localPhotoFragment = LocalPhotoFragment.getInstance(context);
        }
        if (intentPhotoFragment == null){
            intentPhotoFragment = IntentPhotoFragment.getInstance(context);
        }
        initFragment(localPhotoFragment);
        cureentFragment = localPhotoFragment;
        setTextViewColor(true,localText);

    }

    public void initFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout,fragment);
        transaction.commit();
    }


    private void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        localText = (TextView) findViewById(R.id.local_photo);
        intentText = (TextView) findViewById(R.id.intent_photo);
        chooseButton = (Button) findViewById(R.id.choose);
        deleteButton = (Button) findViewById(R.id.delete_all);
        bottomRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_statubar);
        list = (TextView) findViewById(R.id.list_photo);
    }

    private void initListener() {
        localText.setOnClickListener(this);
        intentText.setOnClickListener(this);
        chooseButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.local_photo:
                initFragment(localPhotoFragment);
                cureentFragment = localPhotoFragment;
                setTextViewColor(true,localText);
                setTextViewColor(false,intentText);
                showAndHideStatubar(false);
                break;
            case R.id.intent_photo:
                initFragment(intentPhotoFragment);
                cureentFragment = intentPhotoFragment;
                setTextViewColor(true,intentText);
                setTextViewColor(false,localText);
                showAndHideStatubar(false);
                break;
            case R.id.choose:
                if (cureentFragment.equals(localPhotoFragment)){
                    localPhotoFragment.selectAllOrNot(true);
                }else if(cureentFragment.equals(intentPhotoFragment)){
                    intentPhotoFragment.selectAllOrNot(true);
                }
                chooseButton.setSelected(true);
                deleteButton.setSelected(false);
                setTextViewColor(true,chooseButton);
                setTextViewColor(false,deleteButton);
                break;
            case R.id.delete_all:
                if (cureentFragment.equals(localPhotoFragment)){
                    localPhotoFragment.deleteAllSeletePosition();
                    localPhotoFragment.selectAllOrNot(false);
                }else if(cureentFragment.equals(intentPhotoFragment)){
                    intentPhotoFragment.deleteAllSeletePosition();
                    intentPhotoFragment.selectAllOrNot(false);
                }
                chooseButton.setSelected(false);
                deleteButton.setSelected(true);
                setTextViewColor(false,chooseButton);
                setTextViewColor(true,deleteButton);
                break;
            case R.id.list_photo:
           //      getListInfo();
                 break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    public void setTextViewColor(boolean bool, TextView textView){
        if (bool){
            textView.setTextColor(getColor(R.color.colorAccent));
        }else {
            textView.setTextColor(getColor(R.color.black));
        }
    }

    public  void showAndHideStatubar(boolean show){
        if (show){
            bottomRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            bottomRelativeLayout.setVisibility(View.GONE);
        }
    }
}

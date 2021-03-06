package com.my.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.Manifest;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.my.R;
import com.my.fragment.IntentPhotoFragment;
import com.my.fragment.ListPhotoFragment;
import com.my.fragment.LocalPhotoFragment;


public class MainActivity extends BaseActivity implements View.OnClickListener, IntentPhotoFragment.CallBackListener {


    private Context context;

    private FrameLayout frameLayout;
    private TextView localText;
    private TextView intentText;
    private LocalPhotoFragment localPhotoFragment;
    private IntentPhotoFragment intentPhotoFragment;
    private ListPhotoFragment listPhotoFragment;
    private Button chooseButton;
    private Button deleteButton;
    public   RelativeLayout bottomRelativeLayout;
    public Fragment cureentFragment;
    private TextView list;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        context = getApplicationContext();
        if (localPhotoFragment == null){
            localPhotoFragment = LocalPhotoFragment.getInstance(context);
        }
        if (intentPhotoFragment == null){
            intentPhotoFragment = IntentPhotoFragment.getInstance(context);
        }
        if (listPhotoFragment == null){
            listPhotoFragment = ListPhotoFragment.getInstance(context);
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

    @Override
    protected void initView() {
        frameLayout = (FrameLayout) findViewById(R.id.framelayout);
        localText = (TextView) findViewById(R.id.local_photo);
        intentText = (TextView) findViewById(R.id.intent_photo);
        chooseButton = (Button) findViewById(R.id.choose);
        deleteButton = (Button) findViewById(R.id.delete_all);
        bottomRelativeLayout = (RelativeLayout) findViewById(R.id.bottom_statubar);
        list = (TextView) findViewById(R.id.list_photo);
    }

    @Override
    protected void initListener() {
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
                setTextViewColor(false,list);
                showAndHideStatubar(false);
                break;
            case R.id.intent_photo:
                initFragment(intentPhotoFragment);
                cureentFragment = intentPhotoFragment;
                setTextViewColor(true,intentText);
                setTextViewColor(false,localText);
                setTextViewColor(false,list);
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
                initFragment(listPhotoFragment);
                cureentFragment = listPhotoFragment;
                setTextViewColor(false,intentText);
                setTextViewColor(false,localText);
                setTextViewColor(true,list);
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

    public  void showAndHideStatubar(boolean show){
        if (show){
            bottomRelativeLayout.setVisibility(View.VISIBLE);
        }else{
            bottomRelativeLayout.setVisibility(View.GONE);
        }
    }
}

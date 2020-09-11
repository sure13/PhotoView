package com.my.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.my.R;
import com.my.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class PhotoShowActivity extends AppCompatActivity {


    private int currentPostion = -1;
    private List<String> imageData = new ArrayList<>();
    private ViewPager viewPager;
    private TextView textView;
    private MyPagerAdapter pagerAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_photo);
        initView();
        initData();
        initListener();
    }


    public void initView() {
        viewPager = findViewById(R.id.dialog_photo_vp);
        textView = findViewById(R.id.dialog_photo_tv);
    }


    public void initData() {
       Intent intent = getIntent();
        if (intent != null) {
            currentPostion =  intent.getIntExtra("currentPostion",0);
            imageData = intent.getStringArrayListExtra("imageData");
        }
        pagerAdapter = new MyPagerAdapter(PhotoShowActivity.this, imageData);
        viewPager.setAdapter(pagerAdapter);
        textView.setText(currentPostion + 1 + "/" + imageData.size());
        viewPager.setCurrentItem(currentPostion, false);
    }


    public void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textView.setText(position + 1 + "/" + imageData.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        pagerAdapter.setCallBack(new MyPagerAdapter.onCallBack() {
            @Override
            public void onItemClick() {
                close();
            }
        });
    }


    private void close() {
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

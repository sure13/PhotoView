package com.my.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.my.R;
import com.my.adapter.MyPagerAdapter;
import com.my.bean.Constant;
import com.my.bean.RequestData;
import com.my.util.MediaUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhotoShowActivity extends AppCompatActivity implements View.OnClickListener {


    private int currentPostion = -1;
    private List<String> imageData = new ArrayList<>();
    private ViewPager viewPager;
    private TextView textView;
    private MyPagerAdapter pagerAdapter;

    private ImageView saveImage;




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
        saveImage = findViewById(R.id.save_image);
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

        saveImage.setOnClickListener(this);
    }


    private void close() {
        this.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.save_image:
                downloadImage();
                break;
        }
    }

    private Bitmap bitmap;

    private void downloadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15,TimeUnit.SECONDS).build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                RequestData requestData = retrofit.create(RequestData.class);
                Observable<ResponseBody> responseBodyObservable = requestData.downloadImage("34264.jpg");
                responseBodyObservable.subscribeOn(Schedulers.io())
                        .subscribe(new Observer<ResponseBody>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(ResponseBody value) {
                                byte[] bytes = new byte[0];
                                try {
                                   bytes = value.bytes();
                                   bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                   runOnUiThread(new Runnable() {
                                       @Override
                                       public void run() {
                                           saveImage.setImageBitmap(bitmap);
                                           try {
                                               saveFile(bitmap);
                                           }catch (Exception e){
                                               e.printStackTrace();
                                           }
                                       }
                                   });
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }

            private void saveFile(Bitmap bitmap) throws IOException {
                String path = MediaUtil.getUsbPath(PhotoShowActivity.this);
                File dirFile = new File(path,"test");
                Log.i("wxy","----------dirFile--------------"+dirFile);
                if (!dirFile.exists()) {
                    dirFile.mkdir();
                }
                String fileName = System.currentTimeMillis() + ".jpg";
                File file = new File(dirFile, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 其次把文件插入到系统图库
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(),
                            file.getAbsolutePath(), fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // 最后通知图库更新
               sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
            }
        }).start();
    }
}

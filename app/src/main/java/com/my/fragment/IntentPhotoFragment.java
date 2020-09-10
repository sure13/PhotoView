package com.my.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.my.R;
import com.my.activity.MainActivity;
import com.my.adapter.PhotoAdapter;
import com.my.dialog.PhotoDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IntentPhotoFragment extends Fragment {

    private String[] images = new String[]{
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3933355596,669391437&fm=26&gp=0.jpg", "http://img4.imgtn.bdimg.com/it/u=1445674920,12445384&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=482593725,2192787331&fm=26&gp=0.jpg","http://img5.imgtn.bdimg.com/it/u=2306608262,3534874273&fm=26&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=1827863856,2661023953&fm=26&gp=0.jpg","http://img0.imgtn.bdimg.com/it/u=2775335106,966369936&fm=26&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=1881496596,2607061598&fm=26&gp=0.jpg","http://img2.imgtn.bdimg.com/it/u=3129434724,2523766044&fm=26&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1199886885,3035315889&fm=26&gp=0.jpg", "http://img3.imgtn.bdimg.com/it/u=2911588266,3715444639&fm=26&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1823088812,4234717774&fm=26&gp=0.jpg","http://img0.imgtn.bdimg.com/it/u=1196506210,3510975282&fm=26&gp=0.jpg",
            "http://fms.news.cn/swf/2019_qmtt/10_8_2019_qm_z/images/5.jpg"
    };

    private View view;
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;

    public static IntentPhotoFragment intentPhotoFragment;
    private WeakReference<Context> weakReference;
    private Context context;
    private ArrayList<String> childData;



    private boolean isShowCheckbox;//是否显示Checkbox
    private List<String> selectPositionList;//记录选中的Checkbox
//    private boolean isShowAndhideStatubar; //是否显示底部状态栏
    public CallBackListener callBackListener;

    private ProgressDialog progressDialog;

    public IntentPhotoFragment(Context context){
        weakReference = new WeakReference<>(context);
        this.context = context;
    }

    public static IntentPhotoFragment getInstance(Context context){
        if (intentPhotoFragment == null){
            intentPhotoFragment = new IntentPhotoFragment(context);
        }
        return intentPhotoFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBackListener = (CallBackListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.item_one,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.item_one_rv);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        refushUI();
        initListener();
    }




    private void initData() {
        childData = new ArrayList<>();
        selectPositionList = new ArrayList<>();
        isShowCheckbox = false;
        for (int i=0;i<images.length;i++){
            childData.add(images[i]);
        }
        adapter = new PhotoAdapter(context,childData);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context,6);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void refushUI() {
        if (adapter == null){
            adapter = new PhotoAdapter(context,childData);
            recyclerView.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
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
                        Log.i("wxy","----------remove------------");
                    }else{
                        selectPositionList.add(String.valueOf(position));
                        Log.i("wxy","----------add------------");
                    }
                    Collections.sort(selectPositionList);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("currentPostion", position);
                    bundle.putStringArrayList("imageData", (ArrayList<String>) itemImgs);

                    PhotoDialog photoDialog = new PhotoDialog();
                    photoDialog.setArguments(bundle);
                    photoDialog.show(getFragmentManager(), "");
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
        if (selectPositionList == null){
        }else{
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
                    if (selectPositionList.size() == childData.size()){
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
                adapter.setShowCheckbox(false);
                callBackListener.showAndHideStatubar(false);
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

    public void selectAllOrNot(boolean isAll) {
        if (isAll){
            isShowCheckbox = true;
            adapter.setShowCheckbox(true);
            adapter.setAllSelect(true);
            for(int i = 0;i<childData.size();i++){
                selectPositionList.add(String.valueOf(i));
            }
            refushUI();
        }else{
            adapter.setAllSelect(false);
        }
    }

    public interface CallBackListener{
        void showAndHideStatubar(boolean show);
    }

}

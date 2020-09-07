package com.my.adapter;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.my.R;
import com.my.util.ButtonUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoAdapter  extends RecyclerView.Adapter{

    private Context context;
    private List<String> dataList;
    private OnChildClick onChildClick;


    private boolean showCheckbox = false; //控制是否显示Checkbox
    private SparseBooleanArray mCheckedState = new SparseBooleanArray();//防止Checkbox错乱 做setTag  getTag操作

    //    private boolean choose = false;//长按状态是判断当前item是否被选中
    private boolean isAllSelect;//是否全部选中


    public void setOnChildClick(OnChildClick onChildClick) {
        this.onChildClick = onChildClick;
    }

    public void setAllSelect(boolean allSelect) {
        isAllSelect = allSelect;
    }


    public PhotoAdapter(Context context , List<String> dataList){
        this.context = context;
        this.dataList = dataList;
    }

//    public boolean isShowCheckbox(){
//        return showCheckbox;
//    }

    public void setShowCheckbox(boolean showCheckbox){
        this.showCheckbox = showCheckbox;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_two,null);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PhotoViewHolder){
            ((PhotoViewHolder) holder).checkBox.setTag(position);////防止复用导致的checkbox显示错乱
            if (showCheckbox){
                ((PhotoViewHolder) holder).checkBox.setVisibility(View.VISIBLE);
                if (isAllSelect){
                    ((PhotoViewHolder) holder).checkBox.setChecked(true);
                }else{
                    ((PhotoViewHolder) holder).checkBox.setChecked(mCheckedState.get(position,false));
                }

            }else{
                ((PhotoViewHolder) holder).checkBox.setVisibility(View.GONE);
                ((PhotoViewHolder) holder).checkBox.setChecked(false);////取消掉Checkbox后不再保存当前选择的状态;
                mCheckedState.clear();
            }
                RequestOptions options = new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher)//图片加载出来前，显示的图片
                        .fallback(R.mipmap.a) //url为空的时候,显示的图片
                        .error(R.mipmap.b);//图片加载失败后，显示的图片 图片加载失败
                Glide.with(context).load(dataList.get(position))
                        .apply(options)
                        .into(((PhotoViewHolder) holder).imageView);

                ((PhotoViewHolder) holder).imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (showCheckbox){
                            ((PhotoViewHolder) holder).checkBox.setChecked(!((PhotoViewHolder) holder).checkBox.isChecked());
                        }
                        if (onChildClick != null) {
                            if (!ButtonUtils.isFastDoubleClick()) {
                                onChildClick.onItemClick(position,dataList);
                            }
                        }
                    }
                });
            ((PhotoViewHolder) holder).imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onChildClick != null){
                        onChildClick.onItemLongClick(position);
                    }
                    return false;
                }
            });
            ((PhotoViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int position = (int) buttonView.getTag();
                        if (isChecked){
                            mCheckedState.put(position,true);
                     //       choose = true;
                        }else{
                      //      choose = false;
                            mCheckedState.delete(position);
                        }
                    }
                });


            }

    }




    //删除Notes
    public void deleteSelectPosition(int postion) {
        dataList.remove(postion);
        notifyDataSetChanged();
    }

    public void deleteAll(){
        dataList.clear();
        notifyDataSetChanged();
    }

      @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CheckBox checkBox;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_two_iv);
            checkBox = (CheckBox) itemView.findViewById(R.id.delte);
        }


    }

    public interface OnChildClick {
        void onItemClick(int position,List<String> itemImgs);
        boolean onItemLongClick(int position);
    }


}

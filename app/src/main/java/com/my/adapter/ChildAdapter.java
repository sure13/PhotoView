package com.my.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.my.R;
import com.my.util.ButtonUtils;

import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> data;

    private OnChildClick onChildClick;

    public ChildAdapter(Context context) {
        this.context = context;
        this.data = new ArrayList<>();
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnChildClick(OnChildClick onChildClick) {
        this.onChildClick = onChildClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_two, null);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            ((MyHolder) holder).buildHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class MyHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_two_iv);
        }

        public void buildHolder(final int position) {
            RequestOptions options = new RequestOptions()
                    .placeholder(R.mipmap.ic_launcher)//图片加载出来前，显示的图片
                    .fallback(R.mipmap.a) //url为空的时候,显示的图片
                    .error(R.mipmap.b);//图片加载失败后，显示的图片 图片加载失败

            Glide.with(context).load(data.get(position))
                    .apply(options)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onChildClick != null) {
                        if (!ButtonUtils.isFastDoubleClick()) {
                            onChildClick.onItemClick(position);
                        }
                    }
                }
            });
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showDialog(position);

                    return false;
                }
            });
        }
        public AlertDialog dialog = null;
        private void showDialog(final int position) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("是否删除此图片？");
            builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    data.remove(position);
                    notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialog.dismiss();
                }
            });
           dialog = builder.show();
        }

    }


    public interface OnChildClick {
        void onItemClick(int position);
    }

}

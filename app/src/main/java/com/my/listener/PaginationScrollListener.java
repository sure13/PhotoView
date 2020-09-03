package com.my.listener;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener  extends RecyclerView.OnScrollListener {

    private GridLayoutManager gridLayoutManager;

    public PaginationScrollListener(GridLayoutManager linearLayoutManager){
        this.gridLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visiableItemCount = gridLayoutManager.getChildCount(); //页面当前可见item的数量
        int totalCount = gridLayoutManager.getItemCount(); //页面item总数量
        int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();  //页面可见的第一个数据的position位置
        if (!isLoading() && !isLastPage()){
            if ((visiableItemCount + firstVisibleItemPosition) >= totalCount && firstVisibleItemPosition >= 0){
                loadMoreItems();
            }
        }
    }



    protected abstract void loadMoreItems(); //加载更多的数据

    public abstract int getTotalPageCount(); //获取总页数

    public abstract boolean isLastPage();//是否是最后一个页面

    public abstract boolean isLoading();//是否正在加载中
}

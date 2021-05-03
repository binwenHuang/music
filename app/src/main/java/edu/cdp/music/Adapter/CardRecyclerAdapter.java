package edu.cdp.music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import edu.cdp.music.R;
import edu.cdp.music.view.RoundTransform;

public class CardRecyclerAdapter extends RecyclerView.Adapter<CardRecyclerAdapter.MyViewHolder> {
    private int[] imglist;
    private List<String> tvlist;
    private Context mContext;
    private OnClickCardListener onClickCardListener;
    //构建
    public CardRecyclerAdapter(int[] imglist, List<String> tvlist, Context mContext){
        this.imglist = imglist;
        this.tvlist = tvlist;
        this.mContext = mContext;
        this.onClickCardListener = onClickCardListener;
    }

    @NonNull
    @Override
    //根据item生成itemview 加载item的布局视图 绑定viewholder
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item,parent, false);
        final MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    //通过viewholder来绑定数据
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        RequestOptions options = new RequestOptions().centerCrop().transform(new RoundTransform (mContext,5));
        Glide.with(mContext).load(imglist[position]).apply(options).into(holder.ivSong);
        holder.tvTitle.setText(tvlist.get(position));
        holder.ivSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickCardListener.onClick(position);
            }
        });
    }

    @Override
    //获取数量
    public int getItemCount() {
        return tvlist.size();
    }

    //自定义的ViewHolder，持有每个item的所有界面元素
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public ImageView ivSong;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById( R.id.tv_title);
            ivSong = itemView.findViewById(R.id.iv_song);
        }
    }

    //设置接口
    public interface OnClickCardListener{
        public void onClick(int postion);
    }
}

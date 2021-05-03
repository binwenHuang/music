package edu.cdp.music.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import edu.cdp.music.R;
import edu.cdp.music.view.RoundTransform;

public class CardRecylerViewAdapter extends RecyclerView.Adapter<CardRecylerViewAdapter.MyviewHolder>{
    private int[] imgList;
    private Context context;
    private List<String> tvlist;

    public CardRecylerViewAdapter(int[] imgList, Context context, List<String> tvlist) {
        this.imgList = imgList;
        this.context = context;
        this.tvlist = tvlist;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_items,parent,false);
        final MyviewHolder holder = new MyviewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        RequestOptions options = new RequestOptions().centerCrop().transform(new RoundTransform(context,5));
        Glide.with(context).load(imgList[position]).apply(options).into(holder.ivSong);
        holder.tvTitle.setText(tvlist.get(position));

    }

    @Override
    public int getItemCount() {
        return tvlist.size();
    }

    public static class MyviewHolder extends RecyclerView.ViewHolder{
        public TextView tvTitle;
        public ImageView ivSong;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tv_title);
            ivSong=itemView.findViewById(R.id.iv_songlist);
        }
    }


}

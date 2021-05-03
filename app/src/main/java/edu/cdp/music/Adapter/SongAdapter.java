package edu.cdp.music.Adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import edu.cdp.music.Activity.MusicActivity;
import edu.cdp.music.R;
import edu.cdp.music.bean.Song;

public class SongAdapter extends BaseAdapter {
    private List<Song> songs;
    private Context context;
    private LayoutInflater myli;
    private SetOnclickListener setOnclickListener;


    public SongAdapter(List<Song> songs, Context context,SetOnclickListener setOnclickListener) {
        this.songs = songs;
        Log.i("adapter", "SongAdapter: "+songs.size());

        this.context = context;
        this.myli = LayoutInflater.from(context);
        this.setOnclickListener=setOnclickListener;
    }


    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return songs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Song song = songs.get(i);
        RelativeLayout tvContent;
        TextView tvSonger,tvSong,tvTime,tvDelete,tvBack,tvFoward;
        View v= myli.inflate(R.layout.item_song,null);
        tvSong = v.findViewById(R.id.tv_song_name_item);
        tvSonger =v.findViewById(R.id.tv_singer_name_item);
        tvTime =v.findViewById(R.id.tv_time_item);
        tvContent = v.findViewById(R.id.rl_content);

        tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnclickListener.onClick(i);
            }
        });

        //设置对话框的长按监听
        tvContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopWindows(v,i);
                return false;
            }
        });


        tvBack=v.findViewById(R.id.tv_backward);
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnclickListener.onBack(i);
            }
        });
        tvDelete=v.findViewById(R.id.tv_delete);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnclickListener.onDelete(i);
            }
        });
        tvFoward=v.findViewById(R.id.tv_forward);
        tvFoward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnclickListener.onTop(i);
            }
        });


        tvSong.setText(song.getSongNameStr());
        tvSonger.setText(song.getSingerNameStr());
        tvTime.setText(ShowTime(Integer.valueOf(song.getSongTimeStr())));
        return v;
    }


    //长按时显示提示框
    private void showPopWindows(View v, final int i) {
        final View mPopView = LayoutInflater.from(context).inflate(R.layout.popup, null);
        final PopupWindow mPopWindow = new PopupWindow(mPopView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //获取弹窗的长宽度
        mPopView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = mPopView.getMeasuredWidth();
        int popupHeight =  mPopView.getMeasuredHeight();
        //获取父控件的位置
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        // 设置位置
        mPopWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1]
                - popupHeight);
        mPopWindow.update();
        mPopView.findViewById(R.id.tv_copy_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置删除会话的操作
                if (mPopWindow != null) {
                    Toast.makeText(context, "已从歌曲列表删除", Toast.LENGTH_LONG).show();
                    songs.remove(i);
                    notifyDataSetChanged();
                    mPopWindow.dismiss();
                }
            }
        });
    }


    //换算时间
    private String ShowTime(int time){
        time /= 1000;
        int minute=time/60;
        int second = time % 60;
        minute %= 60;
        return String.format("%02d:%02d",minute,second);
    }

    public interface SetOnclickListener{
        void onDelete(int position);
        void onBack(int position);
        void onTop(int position);
        void onClick(int position);
    }


}

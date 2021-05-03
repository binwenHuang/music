package edu.cdp.music.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.cdp.music.Adapter.SongAdapter;
import edu.cdp.music.MyApplication;
import edu.cdp.music.R;
import edu.cdp.music.bean.DoubleClickListener;
import edu.cdp.music.bean.MusicPlayer;
import edu.cdp.music.bean.Song;
import edu.cdp.music.service.MusicService;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private List<Song> songs;
    private ListView lvMusic;
    private RelativeLayout content;
    private ImageView ivPlay,ivnext,ivcp,playrule;
    private TextView tvsongname,tvsinger;
    private static SeekBar skbBar;
    private int currentMusic = 0;
    //设置当前状态0x11没有播放，0x12正在播放
    int status = 0x11;
    private MusicPlayer musicplayer;
    private SongAdapter songAdapter;
    //控制播放的广播内容
    public static final String CONTROL="a";
    public static final String UPDATE="f";
    int prule=0x25;


    //运用Handler中的handleMessage方法接收service传递的音乐播放进度信息
    //因为handler管理的是service中发送来到message，如果service不存在，但是handlermessage中
    //还有消息处理室会发生内存泄漏，
    //参考：https://www.jianshu.com/p/23b8ba367c0e
    public static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            skbBar.setProgress(msg.arg1);
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        //初始化对象
        initview();

        //设置toolbar
        setToolbar();


        //通过application获取读取到的数据库中的歌曲
        getApplicationSongs();

        //获取歌曲列表并绑定适配器
        setSong();

        //初始化播放器状态
        setMusicInfo(currentMusic);

        setBroadCast();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
        //通过application获取读取到的数据库中的歌曲
        getApplicationSongs();
        setSong();
        songAdapter.notifyDataSetChanged();
    }


    //启动服务
    private void startService() {
        Log.i("", "startService: "+"服务启动");
        Intent intService = new Intent(MusicActivity.this, MusicService.class);
        startService(intService);
    }




    //获取存放在application中的歌曲
    private void getApplicationSongs(){
        MyApplication myapp = (MyApplication)getApplicationContext();
        songs = myapp.getSongs();
        Log.i("从application获取到的歌曲", "getApplicationSongs: "+songs.size());

    }


    //实例化
    private void initview() {
        playrule=findViewById(R.id.play_rule);
        playrule.setOnClickListener(this);
        toolbar = findViewById(R.id.toolbar);
        lvMusic=findViewById(R.id.lv_music);
        ivPlay=findViewById(R.id.iv_play);
        ivPlay.setOnClickListener(this);
        ivnext=findViewById(R.id.iv_next);
        ivnext.setOnClickListener(this);
        ivcp = findViewById(R.id.iv_head_cp);
        ivcp.setOnClickListener(this);
        tvsinger=findViewById(R.id.tv_song_singer);
        tvsongname = findViewById(R.id.tv_song_name);
        skbBar = findViewById(R.id.seekbar);


        //播放点击的歌曲
        lvMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentMusic=position;
                Intent intControl = new Intent(CONTROL);
                intControl.putExtra("op",0x20);
                intControl.putExtra("status",0x11);
                intControl.putExtra("current",currentMusic);
                sendBroadcast(intControl);
            }
        });




        //对进度条进行监听，拖动时定位播放
        skbBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            //滚动条变化时
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            //用户操作结束时
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(status==0x12){
                    Intent intent = new Intent(CONTROL);
                    intent.putExtra("op",0x23);
                    intent.putExtra("status",0x12);
                    intent.putExtra("current",currentMusic);
                    intent.putExtra("pro",seekBar.getProgress());
                    sendBroadcast(intent);
                }
            }
        });

        //实例化播放结束自动下一曲
        musicplayer = new MusicPlayer(new MusicPlayer.OnMusicCompletion() {
            @Override
            public void OnContinue() {
            }
        });

    }


    //设置toolbar
    private void setToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("播放列表");
        TextView textView = (TextView)toolbar.getChildAt(0);
        textView.getLayoutParams().width= LinearLayout.LayoutParams.MATCH_PARENT;
        textView.setGravity(Gravity.CENTER_HORIZONTAL);

    }



    //设置toolbar返回键的监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            moveTaskToBack(true);
        }
        return super.onOptionsItemSelected(item);
    }


//    //获取歌曲列表并绑定适配器
    private void setSong() {
        songAdapter = new SongAdapter(songs, this, new SongAdapter.SetOnclickListener() {
            @Override
            public void onDelete(int position) {
                songs.remove(position);
                songAdapter.notifyDataSetChanged();
            }

            @Override
            public void onBack(int position) {
                if (position!=songs.size()-1){
                    Collections.swap(songs,position,position+1);
                    songAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(MusicActivity.this,"已经在最后一首了",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onTop(int position) {
                if (position!=0){
                    Collections.swap(songs,position,0);
                    songAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(MusicActivity.this,"已经是第一首了",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onClick(int position) {
                currentMusic=position;
                Intent intControl = new Intent(CONTROL);
                intControl.putExtra("op",0x20);
                intControl.putExtra("status",0x11);
                intControl.putExtra("current",currentMusic);
                sendBroadcast(intControl);
            }
        });

        lvMusic.setAdapter(songAdapter);
    }


    @Override
    //设置监听
    public void onClick(View view) {
        Intent intControl = new Intent(CONTROL);
        switch (view.getId()){

            case R.id.iv_head_cp:
                Intent intent = new Intent(MusicActivity.this,MusicDetailActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_play:
                //播放新的歌曲//0x20用于播放，0x21用于下一曲
                if (status==0x11){
                    intControl.putExtra("op",0x20);
                    intControl.putExtra("status",0x11);
                    intControl.putExtra("current",currentMusic);

                }else if (status==0x12){
                    intControl.putExtra("op",0x20);
                    intControl.putExtra("status",0x12);
                    intControl.putExtra("current",currentMusic);
                }
                else if (status==0x13){
                    intControl.putExtra("op",0x20);
                    intControl.putExtra("status",0x13);
                    intControl.putExtra("current",currentMusic);
                }
                //发送广播
                sendBroadcast(intControl);
                break;

            //下一曲
            case R.id.iv_next:
                intControl.putExtra("op",0x21);
                intControl.putExtra("status",status);
                intControl.putExtra("current",currentMusic);
                sendBroadcast(intControl);
                break;


            case R.id.play_rule:
                Log.i("播放顺序。。。。","改变");
                intControl.putExtra("status",status);
                intControl.putExtra("current",currentMusic);

                if (prule==0x25) {
                    playrule.setImageDrawable(getResources().getDrawable(R.drawable.suijibofang));
                    prule=0x26;
                    intControl.putExtra("op",0x26);
                    sendBroadcast(intControl);
                }
                else if (prule==0x26) {
                    playrule.setImageDrawable(getResources().getDrawable(R.drawable.danquxunhuan));
                    prule=0x27;
                    intControl.putExtra("op",0x27);
                    sendBroadcast(intControl);

                }
                else if (prule==0x27){
                    playrule.setImageDrawable(getResources().getDrawable(R.drawable.xunhuan));
                    prule = 0x25;
                    intControl.putExtra("op",0x28);
                    sendBroadcast(intControl);
                }

                break;
        }

    }



    //接受服务发送的广播数据，用于更新界面
    private class UpdateBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("111", "MusicActivity接受来自MusicService的广播信息");
            status = intent.getIntExtra("status",0x11);
            currentMusic=intent.getIntExtra("current",0);
            Log.i("MA接受传回的状态", String.valueOf(status));
            Log.i("MA接受传回的音乐位置", String.valueOf(currentMusic));
            //根据不同的状态改变图标
            SharedPreferences setting = getSharedPreferences("status", 0);
            setting.edit().putInt("status", status).commit();

            setPlayImage(status);
            setMusicInfo(currentMusic);

        }
    }


    //不同状态改变图标
    private void setPlayImage(int status){
        if (status==0x12){
            ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        }else if(status==0x11 || status==0x13){
            ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
    }


    //切换音乐时更换音乐信息
    private void setMusicInfo(int position){
        Log.i("acdfd", "setMusicInfo: 总数"+songs.size());
        Log.i("hkhjk", "setMusicInfo: 位置"+position);
        tvsongname.setText(songs.get(position).getSongNameStr());
        tvsinger.setText(songs.get(position).getSingerNameStr());
    }

    //设置广播
    private void setBroadCast(){
        UpdateBroadcastReciver updateBroadcastReciver=new UpdateBroadcastReciver();
        IntentFilter ifUpdate = new IntentFilter(UPDATE);
        registerReceiver(updateBroadcastReciver,ifUpdate);
    }


}
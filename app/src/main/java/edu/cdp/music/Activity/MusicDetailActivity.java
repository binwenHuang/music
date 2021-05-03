package edu.cdp.music.Activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.List;

import edu.cdp.music.MyApplication;
import edu.cdp.music.R;
import edu.cdp.music.bean.MusicPlayer;
import edu.cdp.music.bean.Song;
import edu.cdp.music.service.MusicService;
import edu.cdp.music.utils.DBUtils;

public class MusicDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private List<Song> songs;
    private ListView lvMusic;
    private ImageView ivPlay,ivnext,ivlast,ivlike,playrule;
    private TextView tvsongname,tvsinger;
    private static SeekBar skbBar;
    private MusicPlayer musicplayer;
    private int currentMusic = 0;
    //设置当前状态0x11没有播放，0x12正在播放
    int status = 0x11;
    private static final String SYNCHRONIZATION ="w" ;
    ObjectAnimator discObjectAnimator;
    private ImageView ivback;
    public static final String UPDATE="f";
    public static final String UPDATESONGS="s";
    public static final String CONTROL="a";
    int prule=0x25;


    //运用Handler中的handleMessage方法接收service传递的音乐播放进度信息
    //因为handler管理的是service中发送来到message，如果service不存在，但是handlermessage中
    //还有消息处理室会发生内存泄漏，
    //参考：https://www.jianshu.com/p/23b8ba367c0e
    public static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg1) {
            skbBar.setProgress(msg1.arg2);
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        Log.i("创建界面", "...................");
        moveTaskToBack(true);
        //获取歌曲
        getApplicationSongs();
        //实例化
        initView();

        //初始化播放器状态
        setMusicInfo(currentMusic);

        setPlayImage(status);

        //注册广播
        setBroadCast();

        setBroadCast_UpdateSongs();

    }



    @Override
    protected void onStart() {
        super.onStart();
        //获取歌曲
        getApplicationSongs();
        //调用画唱片的方法
        setCPImage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //获取歌曲
        getApplicationSongs();
        //初始化播放器状态
        setMusicInfo(currentMusic);
    }

//    //启动服务
//    private void startService() {
//        Intent intService = new Intent(MusicDetailActivity.this, MusicService.class);
//        startService(intService);
//    }


    //实例化方法
    private  void initView(){
        ivback=findViewById(R.id.iv_houtui);
        ivback.setOnClickListener(this);
        ivPlay=findViewById(R.id.iv_play1);
        ivPlay.setOnClickListener(this);
        ivlast=findViewById(R.id.iv_last);
        ivlast.setOnClickListener(this);
        ivnext=findViewById(R.id.iv_next1);
        ivnext.setOnClickListener(this);
        playrule=findViewById(R.id.play_rule1);
        playrule.setOnClickListener(this);
        ivlike=findViewById(R.id.love);
        ivlike.setOnClickListener(this);
        tvsinger=findViewById(R.id.tv_song_singer);
        tvsongname = findViewById(R.id.tv_song_name);
        skbBar = findViewById(R.id.seekbar1);
        //对进度条进行监听，拖动时定位播放
        skbBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
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
    }

    //获取存放在application中的歌曲
    private void getApplicationSongs(){
        MyApplication myapp = (MyApplication)getApplicationContext();
        songs = myapp.getSongs();
    }


    //设置旋转唱片
    private void setCPImage(){
        //最外部的半透明边线，就是一个椭圆形
        OvalShape ovalShape0 = new OvalShape();
        //这个类相当于形状绘制工具，绘制出来什么形状取决于你传递给他什么形状
        //1 唱片的反光椭圆
        ShapeDrawable drawable0 = new ShapeDrawable(ovalShape0);
        drawable0.getPaint().setColor(0x10000000);
        drawable0.getPaint().setStyle(Paint.Style.FILL);
        drawable0.getPaint().setAntiAlias(true);
//        //2.黑色唱片边框
        RoundedBitmapDrawable drawable1 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.cir));
        drawable1.setCircular(true);
        drawable1.setAntiAlias(true);

        //3.内层黑色边线
        OvalShape ovalShape2 = new OvalShape();
        ShapeDrawable drawable2 = new ShapeDrawable(ovalShape2);
        drawable2.getPaint().setColor(Color.BLACK);
        drawable2.getPaint().setStyle(Paint.Style.FILL);
        drawable2.getPaint().setAntiAlias(true);

        //4.歌手的最里面的图像
        RoundedBitmapDrawable drawable3 = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.cphead));
        drawable3.setCircular(true);
        drawable3.setAntiAlias(true);

        Drawable[] layers = new Drawable[4];
        layers[0] = drawable0;
        layers[1] = drawable1;
        layers[2] = drawable2;
        layers[3] = drawable3;

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int width = 10;
        //针对每一个图层进行填充，使得各个圆环之间相互有间隔，否则就重合成一个了。
        //layerDrawable.setLayerInset(0, width, width, width, width);
        layerDrawable.setLayerInset(1, width , width, width, width );
        layerDrawable.setLayerInset(2, width * 11, width * 11, width * 11, width * 11);
        layerDrawable.setLayerInset(3, width * 12, width * 12, width * 12, width * 12);

        final View discView = findViewById(R.id.myView);
        discView.setBackgroundDrawable(layerDrawable);

        discObjectAnimator = ObjectAnimator.ofFloat(discView, "rotation", 0, 360);
        discObjectAnimator.setDuration(20000);
        //使ObjectAnimator动画匀速平滑旋转
        discObjectAnimator.setInterpolator(new LinearInterpolator());
        //无限循环旋转
        discObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);

        discObjectAnimator.start();
    }


    //设置监听
    @Override
    public void onClick(View v) {
        Intent intControl = new Intent(CONTROL);
        switch(v.getId()){
            case R.id.iv_houtui:
                moveTaskToBack(true);
                break;
            case R.id.iv_play1:
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

                sendBroadcast(intControl);
                break;


            //下一曲
            case R.id.iv_next1:
                intControl.putExtra("op",0x21);
                intControl.putExtra("status",status);
                intControl.putExtra("current",currentMusic);
                sendBroadcast(intControl);
                break;

            //上一曲
            case R.id.iv_last:
                intControl.putExtra("op",0x24);
                intControl.putExtra("status",status);
                intControl.putExtra("current",currentMusic);
                sendBroadcast(intControl);
                break;

            //设置喜欢
            case R.id.love:
                String name=songs.get(currentMusic).getSongNameStr();
                setLove(name);
                break;
        }
    }


    //接受服务发送的广播数据，用于更新界面
    private class UpdateBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("222", "MusicDetailActivity接受来自MusicService的广播信息");
            status = intent.getIntExtra("status",0x11);
            currentMusic=intent.getIntExtra("current",0);

            Log.i("MDA接受传回的音乐位置", String.valueOf(currentMusic));
            Log.i("MDA接受传回的状态", String.valueOf(status));
            Log.i("-----------------------","----------------------------");

            //根据不同的状态改变图标
            setPlayImage(status);
            setMusicInfo(currentMusic);
        }

    }


    //不同状态改变图标
    private void setPlayImage(int status){
//        SharedPreferences setting = getSharedPreferences("status", 0);
//        status = setting.getInt("status",0x11);
        if (status==0x12){
            ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.pause));
        }else if(status==0x11 || status==0x13){
            ivPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
        }
    }


    //切换音乐时更换音乐信息
    private void setMusicInfo(int position){
        DBUtils db = new DBUtils(MusicDetailActivity.this);
        if (songs==null){
            tvsongname.setText("");
            tvsinger.setText("");
        }
        else {
            tvsongname.setText(songs.get(position).getSongNameStr());
            tvsinger.setText(songs.get(position).getSingerNameStr());
            if (db.getSongNamelike(tvsongname.getText().toString())){
                ivlike.setImageDrawable(getResources().getDrawable(R.drawable.islike));
            }else {
                ivlike.setImageDrawable(getResources().getDrawable(R.drawable.like));
            }
        }


    }


    //设置喜欢的歌曲，添加到数据库表
    private void setLove(String name){
        DBUtils db = new DBUtils(MusicDetailActivity.this);
        if (!db.getSongNamelike(name)){
            db.setLoveInDatabase(name);
            ivlike.setImageDrawable(getResources().getDrawable(R.drawable.islike));
            Toast.makeText(MusicDetailActivity.this,"已添加到我喜欢的歌单",Toast.LENGTH_LONG).show();
        }
        else {
            db.DeleteLoveInDatabase(name);
            ivlike.setImageDrawable(getResources().getDrawable(R.drawable.like));
            Toast.makeText(MusicDetailActivity.this,"已从我喜欢的歌单移除",Toast.LENGTH_LONG).show();
        }
        db.closeDb();
    }


    //设置控制播放与界面更新的广播
    public void setBroadCast(){
        UpdateBroadcastReciver updateBroadcastReciver=new UpdateBroadcastReciver();
        IntentFilter ifUpdate = new IntentFilter(UPDATE);
        registerReceiver(updateBroadcastReciver,ifUpdate);
    }


    //接受服务发送的广播数据，用于更新songs
    private class UpdateSongsBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("333", "MusicDetailActivity接受来自MaiActivity的广播信息");
            int op = intent.getIntExtra("op", 0x11);
            if (op==0x30){
                getApplicationSongs();
            }
        }
    }



    //设置更新songs的广播
    public void setBroadCast_UpdateSongs(){
        UpdateSongsBroadcastReciver updateSongsBroadcastReciver=new UpdateSongsBroadcastReciver();
        IntentFilter ifUpdateSongs = new IntentFilter(UPDATESONGS);
        registerReceiver(updateSongsBroadcastReciver,ifUpdateSongs);
    }



}
package edu.cdp.music.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;
import java.util.Random;


import edu.cdp.music.Activity.MusicActivity;
import edu.cdp.music.Activity.MusicDetailActivity;
import edu.cdp.music.MyApplication;
import edu.cdp.music.R;
import edu.cdp.music.bean.MusicPlayer;
import edu.cdp.music.bean.Song;

public class MusicService extends Service {
    private MusicPlayer musicPlayer;
    private List<Song> songs;
    private MyCotrolBroadcastReciver myCotrolBroadcastReciver;
    private int status = 0x11;
    private Notification notification;
    private static final String CHANNEL_ID = "1";
    private static final String CHANNEL_NAME = "MyChannel";
    private static final int NOTIFICATION_ID = 2;
    private RemoteViews remoteViews;
    public static final String UPDATE="f";

    int crrent=0;
    int prule=0;
    int random;



    @Override
    public void onCreate() {
        super.onCreate();
        //实现连续播放
        musicPlayer = new MusicPlayer(new MusicPlayer.OnMusicCompletion() {
            @Override
            public void OnContinue() {
                Log.i("播放结束。。。。。。。。。。。。", "播放状态（1为随机/0为顺序）==== "+prule);
                //自然播放结束时，根据prule的值判断下一曲的播放方式
                if (prule==1){
                    random = new Random().nextInt(songs.size());
                    musicPlayer.PreAndPlay(songs.get(random).getFilepathStr());
                    status=0x12;
                    Intent acintent = new Intent(MusicActivity.UPDATE);
                    acintent.putExtra("status",status);
                    acintent.putExtra("current",random);
                    sendBroadcast(acintent);
                    //更新
                    updateNotification();
                }
                else if (prule==0){
                    crrent = (crrent + 1) % songs.size();
                    musicPlayer.nextMusic(songs.get(crrent).getFilepathStr());
                    status=0x12;
                    Intent acintent = new Intent(MusicActivity.UPDATE);
                    acintent.putExtra("status",status);
                    acintent.putExtra("current",crrent);
                    sendBroadcast(acintent);
                }
                else if (prule==2){
                    musicPlayer.PreAndPlay(songs.get(crrent).getFilepathStr());
                    status=0x12;
                    Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                    intentUpdate1.putExtra("status",status);
                    intentUpdate1.putExtra("current",crrent);
                    sendBroadcast(intentUpdate1);
                }
            }
        });

        thread.start();

    }

    //启动服务
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //调用获取数据
        getApplicationSongs();

        //创建infliter设置广播接受的信息
        myCotrolBroadcastReciver = new MyCotrolBroadcastReciver();
        IntentFilter ifCotrol = new IntentFilter(MusicActivity.CONTROL);
        registerReceiver(myCotrolBroadcastReciver,ifCotrol);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicPlayer.destoryMusic();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //获取application
    private void getApplicationSongs(){
        MyApplication myapp=(MyApplication)getApplicationContext();
        songs=myapp.getSongs();
    }


    //内部广播接受器，内部类,接受来自Activity的广播信息，用于改变状态
    private class MyCotrolBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int op = intent.getIntExtra("op",0x20);
            status = intent.getIntExtra("status",0x11);
            crrent = intent.getIntExtra("current",0);
            Log.i("接收状态", "接受广播信息.....操作："+op+"......状态："+status+"....操作前的歌曲："+crrent);
            //通知栏
            initNotification();
            switch (op){
                case 0x20:
                    if (status==0x11){
                        musicPlayer.PreAndPlay(songs.get(crrent).getFilepathStr());
                        status=0x12;
                    }else if (status==0x12){
                        musicPlayer.pause();
                        status=0x13;
                    }
                    else if (status==0x13){
                        musicPlayer.start();
                        status=0x12;
                    }

                    Intent intentUpdate = new Intent(UPDATE);

                    intentUpdate.putExtra("status",status);
                    //判断播放方式
                    if (prule==0||prule==2){
                        intentUpdate.putExtra("current",crrent);
                    }else if(prule==1){
                        intentUpdate.putExtra("current",random);
                    }

                    sendBroadcast(intentUpdate);
                    //更新
                    updateNotification();
                    break;

                //下一曲时
                case 0x21:
                    Log.i("播放方式","...."+prule+"====(1随机播放，2单曲循环，0顺序播放)");
                    //判断为顺序循环播放
                    if (prule==0){
                        crrent=(crrent+1)%songs.size();
                        musicPlayer.PreAndPlay(songs.get(crrent).getFilepathStr());
                        status=0x12;
                        Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                        intentUpdate1.putExtra("status",status);
                        intentUpdate1.putExtra("current",crrent);
                        sendBroadcast(intentUpdate1);
                        //更新
                        updateNotification();
                    }

                    //判断为随机播放
                    if (prule==1){
                        random = new Random().nextInt(songs.size());
                        musicPlayer.PreAndPlay(songs.get(random).getFilepathStr());
                        Log.i("随机歌曲====",random+"。。。。。。。"+songs.get(random).getFilepathStr());
                        status=0x12;
                        Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                        intentUpdate1.putExtra("status",status);
                        intentUpdate1.putExtra("current",random);
                        crrent=random;
                        sendBroadcast(intentUpdate1);
                        //更新
                        updateNotification();

                    }

                    //判断为单曲循环播放
                    if (prule==2){
                        musicPlayer.PreAndPlay(songs.get(crrent).getFilepathStr());
                        status=0x12;
                        Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                        intentUpdate1.putExtra("status",status);
                        intentUpdate1.putExtra("current",crrent);
                        sendBroadcast(intentUpdate1);

                    }
                    break;


                //上一曲时
                case 0x24:
                    //判断为顺序循环播放
                    if (prule==0){
                        if (crrent>0){
                            crrent=crrent-1;
                        }
                        else {
                            crrent=songs.size()-1;
                        }
                        musicPlayer.PreAndPlay(songs.get(crrent).getFilepathStr());
                        Log.i("播放。。。", "onReceive: "+songs.size());
                        status=0x12;
                        Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                        intentUpdate1.putExtra("status",status);
                        intentUpdate1.putExtra("current",crrent);
                        sendBroadcast(intentUpdate1);
                        //更新
                        updateNotification();
                    }

                    //判断为随机播放
                    if (prule==1){
                        random = new Random().nextInt(songs.size());
                        musicPlayer.PreAndPlay(songs.get(random).getFilepathStr());
                        Log.i("随机歌曲====",random+"。。。。。。。"+songs.get(random).getFilepathStr());
                        status=0x12;
                        Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                        intentUpdate1.putExtra("status",status);
                        intentUpdate1.putExtra("current",random);
                        crrent=random;
                        sendBroadcast(intentUpdate1);
                        //更新
                        updateNotification();

                    }

                    //判断为单曲循环播放
                    if (prule==2){
                        musicPlayer.PreAndPlay(songs.get(crrent).getFilepathStr());
                        status=0x12;
                        Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                        intentUpdate1.putExtra("status",status);
                        intentUpdate1.putExtra("current",crrent);
                        sendBroadcast(intentUpdate1);
                        //更新
                        updateNotification();

                    }
                    break;


                //  拖动进度条的操作
                case 0x23:
                    int pro = intent.getIntExtra("pro",0);
                    musicPlayer.setSeekBar(pro);
                    status=0x12;

                    Intent intentUpdate2 = new Intent(MusicActivity.UPDATE);
                    intentUpdate2.putExtra("status",status);
                    //判断为顺序循环播放或单曲循环播放
                    if (prule==0||prule==2){
                        intentUpdate2.putExtra("current",crrent);
                    }
                    //判断为随机播放
                    else if(prule==1){
                        intentUpdate2.putExtra("current",random);
                    }
                    sendBroadcast(intentUpdate2);
                    break;

                //随机播放时 ，改变prule的值
                case 0x26:
                    prule=1;
                    break;

                //单曲循环播放时 ，改变prule的值
                case 0x27:
                    prule=2;
                    break;

                //循环播放时 ，改变prule的值
                case 0x28:
                    prule=0;
                    break;
                case 0x30:
                    Log.i("", "重新刷新songs");
                    getApplicationSongs();
                    Intent intentUpdate1 = new Intent(MusicActivity.UPDATE);
                    intentUpdate1.putExtra("op",0x30);

                    sendBroadcast(intentUpdate1);
            }
        }
    }

    //开启线程改变进度条
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    Thread.sleep(1000);
                    if (musicPlayer != null && status==0x12){
                        int pro = musicPlayer.getCurrentProgress();
                        Message msg = new Message();
                        msg.arg1=pro;
                        Message msg1 = new Message();
                        msg1.arg2=pro;
                        //发送到musicActivity
                        MusicActivity.handler.sendMessage(msg);
                        //发送到musicDetailActivity
                        MusicDetailActivity.handler.sendMessage(msg1);

//                        Intent intent = new Intent();
//                        intent.setClassName(getPackageName(), "MusicDetailActivity");
//                        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                        if(resolveInfo != null) {
//                            Log.i("", "MusicDetailActivity页面已经创建");
//                        }else{
//                            Log.i("", "MusicDetailActivity页面没有创建");
//                        }

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    });


    /**
     * 初始化Notification（系统默认UI）
     * 这里是谷歌官方使用步骤：
     * A：要开始，您需要使用notificationCompat.builder对象设置通知的内容和通道。
     * B: 在Android 8.0及更高版本上传递通知之前，必须通过将NotificationChannel实例传递给CreateNotificationChannel（），在系统中注册应用程序的通知通道。
     * C: 每个通知都应该响应tap，通常是为了在应用程序中打开与通知对应的活动。为此，必须指定用PendingIntent对象定义的内容意图，并将其传递给setContentIntent（）。
     * D: 若要显示通知，请调用notificationManagerCompat.notify（），为通知传递唯一的ID以及notificationCompat.builder.build（）的结果。
     */
    private  void initNotification(){
        //Build.VERSION.SDK_INT 软件app安装在哪个手机上，该手机的操作系统版本号 比如8.1对应的SDK_INT是27
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // 传入参数：通道ID，通道名字，通道优先级（类似曾经的 builder.setPriority()）
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        remoteViews=new RemoteViews(getPackageName(),R.layout.nf);
        remoteViews.setTextViewText(R.id.tv_song_name,songs.get(crrent).getSongNameStr());
        remoteViews.setTextViewText(R.id.tv_song_singer,songs.get(crrent).getSingerNameStr());

        //下一曲
        Intent intent=new Intent(MusicActivity.CONTROL);
        intent.putExtra("op",0x21);
        intent.putExtra("status",0x11);
        intent.putExtra("current",crrent);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_next3,pendingIntent);

        //播放
        Intent intent1=new Intent(MusicActivity.CONTROL);
        intent1.putExtra("current",crrent);
        intent1.putExtra("status",0x13);
        intent1.putExtra("op",0x20);
        PendingIntent pendingIntent1=PendingIntent.getBroadcast(this,1,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_play3,pendingIntent1);

        //暂停
        Intent intent2=new Intent(MusicActivity.CONTROL);
        intent2.putExtra("current",crrent);
        intent2.putExtra("status",0x12);
        intent2.putExtra("op",0x20);
        PendingIntent pendingIntent2=PendingIntent.getBroadcast(this,2,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_puse3,pendingIntent2);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.cp)//设置通知的图标
                .setContent(remoteViews)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setContentIntent(pendingIntent1)
                .setContentIntent(pendingIntent2);//设置为一个正在进行的通知

        notification=builder.build();//使用builder创建通知



        //创建notification
        NotificationManagerCompat notificationManagerCompat=NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID,notification);

    }
    /**
     * 更新Notification
     */
    public void updateNotification() {
        //remoteViews.setImageViewResource(R.id.iv_play_pause, isPlaying ? R.drawable.pause : R.drawable.play);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        remoteViews.setTextViewText(R.id.tv_song_name,songs.get(crrent).getSongNameStr());
        remoteViews.setTextViewText(R.id.tv_song_singer,songs.get(crrent).getSingerNameStr());

        manager.notify(NOTIFICATION_ID, notification);
    }

    //https://blog.csdn.net/xxdw1992/article/details/80948315
    //PendingIntent可以看作是对Intent的一个封装，但它不是立刻执行某个行为，
    //而是满足某些条件或触发某些事件后才执行指定的行为。





}

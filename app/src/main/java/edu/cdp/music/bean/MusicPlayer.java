package edu.cdp.music.bean;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import java.io.IOException;

import edu.cdp.music.R;


//播放类
public class MusicPlayer{
    private MediaPlayer mediaPlayer;
    private OnMusicCompletion onMusicCompletion;

    public MusicPlayer(final OnMusicCompletion onMusicCompletion) {
        this.mediaPlayer = new MediaPlayer();
        this.onMusicCompletion=onMusicCompletion;

        //监听播放完毕，设置播放完毕后改变状态以及歌曲信息，播放下一首的操作
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onMusicCompletion.OnContinue();
                destoryMusic();
            }
        });
    }

    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**/

    public void PreAndPlay(String musicPath){
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(musicPath);
            mediaPlayer.prepareAsync();
            //需要设置一个监听器
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //暂停
    public void pause(){
        mediaPlayer.pause();
    }

    //开始播放
    public void start(){
        mediaPlayer.start();
    }

    //下一曲
    public void nextMusic(String musicPth){
        mediaPlayer.stop();
        PreAndPlay(musicPth);
    }

    //关闭释放
    public void destoryMusic(){
        if (mediaPlayer !=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    //设置播放进度
    public int getCurrentProgress() {
        int sumPro = mediaPlayer.getDuration();
        int currentPro = mediaPlayer.getCurrentPosition();
        //如果未及时获取歌曲时长时
        while (sumPro==0){
            sumPro = mediaPlayer.getDuration();
        }
        return 100 * currentPro / sumPro;
    }

    public boolean checkIsMusicPlaying(){
        if (mediaPlayer.isPlaying()){
            return true;
        }else {
            return false;
        }
    }


    public void setSeekBar(int pro){
        int sumPro= mediaPlayer.getDuration();
        int currentPro = pro*sumPro/100;
        mediaPlayer.seekTo(currentPro);
    }


    //连续播放的接口回调
    public interface OnMusicCompletion{
        void OnContinue();
    }



}

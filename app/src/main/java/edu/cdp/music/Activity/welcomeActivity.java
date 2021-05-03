package edu.cdp.music.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.cdp.music.R;

public class welcomeActivity extends AppCompatActivity {
    private TextView tv_tz_count;
    private Boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activity);
        setPerfrence();
        thread.start();
    }


    //新建一个子线程处理耗时操作（延时跳转）
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            int count = 3;
            while (flag){
                try {
                    thread.sleep(1000);
                    Message msg = new Message();
                    msg.arg1 = count;
                    count--;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    });

    //新建handler
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            tv_tz_count=findViewById(R.id.tz_count);
            super.handleMessage(msg);
            tv_tz_count.setText(msg.arg1+"S 后跳转");

            if (msg.arg1==0){
                Intent intent_tz = new Intent(welcomeActivity.this,MainActivity.class);
                startActivity(intent_tz);
                Intent intent1 = new Intent(welcomeActivity.this,MusicDetailActivity.class);
                startActivity(intent1);
                finish();
            }

        }
    };


    //没有调用，直接调用了onDestroy()
    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        //设置flag用于关闭子线程，否则延时结束后还会执行子线程的跳转，从而刷新一次页面
        flag = false;
    }


    //设置创建一个文件，并保存一个值，判断是否第一次打开
    private void setPerfrence(){

//        //清空文件
//        SharedPreferences sp=getSharedPreferences("if_first",MODE_PRIVATE);
//        if(sp!=null) {
//            sp.edit().clear().commit();
//            Toast.makeText(welcomeActivity.this, "数据已清空", Toast.LENGTH_LONG).show();
//        }


        SharedPreferences setting = getSharedPreferences("if_first", 0);

        Boolean user_first = setting.getBoolean("FIRST",true);

        if(user_first){
            //第一次
            setting.edit().putBoolean("FIRST", true).commit();
            //Toast.makeText(welcomeActivity.this, "第一次", Toast.LENGTH_LONG).show();

        }else{
//            Intent intent = new Intent();
//            intent.setClass(getApplicationContext(),MainActivity.class);
//            startActivity(intent);


            setting.edit().putBoolean("FIRST", true).commit();
            finish();
            //Toast.makeText(welcomeActivity.this, "不是第一次", Toast.LENGTH_LONG).show();
        }
    }

}
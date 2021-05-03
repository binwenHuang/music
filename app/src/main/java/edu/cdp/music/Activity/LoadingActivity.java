package edu.cdp.music.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.cdp.music.R;
import edu.cdp.music.utils.DBUtils;
import edu.cdp.music.view.Loading_view;


public class LoadingActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "1";
    private Button btn_loading;
    private TextView pswfoeget,Xy,edt_zhuce;
    private TextView editText_id,editText_psw;
    private CheckBox cb;

    String id_user = null;
    String psw_user =null;


    boolean flag = true;


    private Loading_view loading_view;

    private AutoCompleteTextView autoCompleteTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //判断上次的登录
        setPerfrence();

        //实验
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.tv_user);
        initAutoComplete("history", autoCompleteTextView);
        findViewById(R.id.loading_btn).getBackground().setAlpha(180);
        initView();
    }




    private void initView(){
        editText_id = findViewById(R.id.tv_user);
        editText_psw = findViewById(R.id.edit_psw);

        //获得Intent
        Intent intent = this.getIntent();
        //从Intent获得额外信息，设置为TextView的文本
        editText_id.setText(intent.getStringExtra("id"));
        editText_psw.setText(intent.getStringExtra("pwd"));

        cb = findViewById(R.id.tv_notice);

        btn_loading=findViewById(R.id.loading_btn);
        btn_loading.setOnClickListener(this);

        pswfoeget=findViewById(R.id.psw_forget);
        pswfoeget.setOnClickListener(this);

        edt_zhuce=findViewById(R.id.zhuce_tv);
        edt_zhuce.setOnClickListener(this);

        Xy=findViewById(R.id.tv_XY);
        Xy.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.loading_btn:
                loading(view);
                break;
//            case R.id.tv_XY:
//                Intent intent_xy = new Intent(LoadingActivity.this,XYActivity.class);
//                startActivity(intent_xy);
//                cb.setChecked(true);
//                break;
//            case R.id.psw_forget:
//                Intent intent_forget=new Intent(LoadingActivity.this, ForgetActivity.class);
//                startActivityForResult(intent_forget,1111);
//                break;
//            case R.id.zhuce_tv:
//                Intent intent_zhuce = new Intent(LoadingActivity.this, RegisterActivity.class);
//                startActivity(intent_zhuce);
//                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void loading(View view) {
        loading_view = new Loading_view(this, R.style.CustomDialog);

        if (cb.isChecked()){
//            int i = user_id.length;
//            int j=0;
            Log.i(TAG, "loading: 调用验证");

            //获取文本框输入内容
            id_user = editText_id.getText().toString();
            psw_user = editText_psw.getText().toString();

            if (id_user.length() !=0  && psw_user.length() !=0) {
                loading_view.show();
                thread.start();

            }else {
                Toast.makeText(LoadingActivity.this,"用户名或密码不能为空",Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(LoadingActivity.this,"请勾选同意《服务条款》",Toast.LENGTH_LONG).show();
        }
    }


    //登录账号与密码2283805890//1234567890

    //新建一个子线程处理耗时操作（延时模拟登录时连接服务器与验证过程）
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

            DBUtils uService=new DBUtils(LoadingActivity.this);
            boolean flag_user=uService.login(id_user,psw_user);

            if (msg.arg1==0) {
                //关闭登录等待弹窗
                loading_view.dismiss();

                if (flag_user) {
                    Log.i("TAG", "登录成功");
                    //登录成功后更改登录的状态，下次直接跳转
                    setZhuangtai();
                    saveHistory("history", autoCompleteTextView);
                    Toast.makeText(LoadingActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("TAG", "登录失败");
                    Toast.makeText(LoadingActivity.this, "账号或密码有误", Toast.LENGTH_LONG).show();
                }
            }

        }

    };

    //关闭线程
    protected void onStop() {
        super.onStop();
        flag = false;

    }


    //实验
    private void saveHistory(String field, AutoCompleteTextView autoCompleteTextView) {
        String text = autoCompleteTextView.getText().toString();
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString(field, "nothing");
        if (!longhistory.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(longhistory);
            sb.insert(0, text + ",");
            sp.edit().putString("history", sb.toString()).commit();
        }
    }

    private void initAutoComplete(String field, AutoCompleteTextView autoCompleteTextView) {
        SharedPreferences sp = getSharedPreferences("network_url", 0);
        String longhistory = sp.getString("history", "nothing");
        String[] histories = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, histories);
        // 只保留最近的50条的记录
        if (histories.length > 50) {
            String[] newHistories = new String[50];
            System.arraycopy(histories, 0, newHistories, 0, 50);
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, newHistories);
        }
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) { AutoCompleteTextView view = (AutoCompleteTextView) v;
                        if (hasFocus) {
                            view.showDropDown();
                        }
                    }
        });
    }


    //创建一个文件，并保存一个值，判断是否第一次打开
    private void setPerfrence(){

//                //清空文件
//        SharedPreferences sp=getSharedPreferences("loding_success",MODE_PRIVATE);
//        if(sp!=null) {
//            sp.edit().clear().commit();
//            Toast.makeText(LoadingActivity.this, "数据已清空", Toast.LENGTH_LONG).show();
//        }

        SharedPreferences setting = getSharedPreferences("loding_success", 0);
        Boolean user_first = setting.getBoolean("success",false);

        if(user_first){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
            //Toast.makeText(welcomeActivity.this, "上次登录过", Toast.LENGTH_LONG).show();
        }
    }


    //设置状态文件，用于判断登录状态
    private void setZhuangtai(){
        SharedPreferences setting = getSharedPreferences("loding_success", 0);

        setting.edit().putBoolean("success", true).commit();

    }


}
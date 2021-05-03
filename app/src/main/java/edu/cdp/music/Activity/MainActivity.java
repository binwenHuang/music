package edu.cdp.music.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import edu.cdp.music.Adapter.MyPagerAdapter;
import edu.cdp.music.Adapter.SongAdapter;
import edu.cdp.music.MyApplication;
import edu.cdp.music.R;
import edu.cdp.music.bean.MusicPlayer;
import edu.cdp.music.bean.Song;
import edu.cdp.music.utils.DBUtils;
import edu.cdp.music.view.Viewcyuncun;
import edu.cdp.music.view.Viewfaxian;
import edu.cdp.music.view.Viewshiping;
import edu.cdp.music.view.Viewwode;
import edu.cdp.music.view.Viewzhanghao;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lvMusic;
    private EditText edname;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ImageView ivsearch,ivlike,ivall,ivpopular;
    private List<Song> songs;
    private MusicPlayer musicplayer;
    public static final String CONTROL="a";
    public static final String UPDATESONGS="s";
    private ViewPager vpagers;
    private Viewcyuncun viewcyuncun;
    private Viewfaxian viewfaxian;
    private Viewshiping viewshiping;
    private Viewwode viewwode;
    private Viewzhanghao viewzhanghao;
    private ImageView ivfaxian,ivshiping,ivwode,ivyuncun,ivzhanghao;
    private TextView tvfaxian,tvshiping,tvwode,tvyuncun,tvzhanghao;
    private LinearLayout llfaxian,llshiping,llwode,llyuncun,llzhanghao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //实例化方法
        initview();
        //设置标题
        initToolBar();
        setDrawerToggle();
        setNavigationViewListener();
        setToolbarListener();
        getView();
        gettab();
        setStatus(0);

    }


    //实例化
    private void initview(){
        View viewPager = LayoutInflater.from(this).inflate(R.layout.view_faxian, null);
        edname = viewPager.findViewById(R.id.singname_input);

        lvMusic=findViewById(R.id.lv_music);
        toolbar=findViewById(R.id.toolbar);
        navigationView=findViewById(R.id.nav_view);
        drawer =findViewById(R.id.drawlayout);
        ivsearch = findViewById(R.id.search);

        ivlike=findViewById(R.id.like);

        ivpopular=findViewById(R.id.popular);

        ivall=findViewById(R.id.iv_head_cp);

        vpagers = findViewById(R.id.iv_vpager);

    }



    //获取View
    private void getView(){
        viewcyuncun = new Viewcyuncun (this);
        View v1 = viewcyuncun.getView ();
        viewfaxian = new Viewfaxian (this);
        View v2 = viewfaxian.getView ();
        edname=v2.findViewById(R.id.singname_input);
        viewshiping = new Viewshiping (this);
        View v3 = viewshiping.getView ();
        viewwode = new Viewwode (this);
        View v4 = viewwode.getView ();
        viewzhanghao = new Viewzhanghao (this);
        View v5= viewzhanghao.getView ();
        ArrayList<View> views = new ArrayList<View>();
        views.add(v2);
        views.add(v1);
        views.add(v4);
        views.add(v3);
        views.add(v5);
        MyPagerAdapter myPagerAdapter=new MyPagerAdapter(views);
        vpagers.setAdapter(myPagerAdapter);

        //设置滑动监听事件，滑动时改变主导航栏的样式
        vpagers.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setStatus(position);
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置首先显示为消息页面
        vpagers.setCurrentItem(0);

    }


    //实例化导航栏
    private void gettab(){
        llshiping=findViewById(R.id.ll_shiping);
        llshiping.setOnClickListener(this);
        llfaxian=findViewById(R.id.ll_faxian);
        llfaxian.setOnClickListener(this);
        llwode=findViewById(R.id.ll_wode);
        llwode.setOnClickListener(this);
        llyuncun=findViewById(R.id.ll_yuncun);
        llyuncun.setOnClickListener(this);
        llzhanghao=findViewById(R.id.ll_zhanghao);
        llzhanghao.setOnClickListener(this);

        ivshiping=findViewById(R.id.iv_shiping);
        ivfaxian=findViewById(R.id.iv_faxian);
        ivwode=findViewById(R.id.iv_wode);
        ivyuncun=findViewById(R.id.iv_yuncun);
        ivzhanghao=findViewById(R.id.iv_zhanghao);

        tvshiping=findViewById(R.id.tv_shiping);
        tvfaxian=findViewById(R.id.tv_faxian);
        tvwode=findViewById(R.id.tv_wode);
        tvyuncun=findViewById(R.id.tv_yuncun);
        tvzhanghao=findViewById(R.id.tv_zhanghao);

    }

    //设置状态
    private void setStatus(int index) {
        clearstatus();
        switch (index){
            case 0:
                ivfaxian.setImageResource(R.drawable.music1);
                tvfaxian.setTextColor ( Color.parseColor ( "#DB5860" ) );
                break;
            case 1:
                tvshiping.setTextColor ( Color.parseColor ( "#DB5860" ) );
                ivshiping.setImageResource(R.drawable.shiping1);
                break;
            case 2:
                tvwode.setTextColor ( Color.parseColor ( "#DB5860" ) );
                ivwode.setImageResource(R.drawable.yingyue1);
                break;
            case 3:
                tvyuncun.setTextColor ( Color.parseColor ( "#DB5860" ) );
                ivyuncun.setImageResource(R.drawable.dongtai1);
                break;
            case 4:
                tvzhanghao.setTextColor ( Color.parseColor ( "#DB5860" ) );
                ivzhanghao.setImageResource(R.drawable.zhanghao1);
                break;
        }

    }

    //清除状态
    private void clearstatus() {
        tvfaxian.setTextColor(Color.parseColor("#666666"));
        ivfaxian.setImageResource(R.drawable.music);
        tvshiping.setTextColor(Color.parseColor("#666666"));
        ivshiping.setImageResource(R.drawable.shiping);
        tvwode.setTextColor(Color.parseColor("#666666"));
        ivwode.setImageResource(R.drawable.yingyue);
        tvyuncun.setTextColor(Color.parseColor("#666666"));
        ivyuncun.setImageResource(R.drawable.dongtai);
        tvzhanghao.setTextColor(Color.parseColor("#666666"));
        ivzhanghao.setImageResource(R.drawable.zhanghao);
    }

    //设置toolbar
    private void initToolBar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("晚抑");
        TextView textView = (TextView)toolbar.getChildAt(0);
        textView.getLayoutParams().width= LinearLayout.LayoutParams.MATCH_PARENT;
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    //设置页面格式
    private void setDrawerToggle(){
        toggle= new ActionBarDrawerToggle(this,drawer,toolbar,0,0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    //创建一个右上角的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    //设置侧滑栏监听
    private void setNavigationViewListener(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_sylea:
                        Intent intent = new Intent(MainActivity.this,LoadingActivity.class);
                        startActivity(intent);
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    //设置侧滑菜单监听
    private void setToolbarListener(){
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.setting:
                        Toast.makeText(MainActivity.this,"设置",Toast.LENGTH_LONG).show();
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }


    //设置监听
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                searchSongs();
                break;

            case R.id.popular:
                popularMusic();
                break;

            case R.id.like:
                likeSongs();
                break;

            case R.id.dfdas:
                getSongsinSQL();
                Intent intent = new Intent(MainActivity.this,MusicActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_faxian:
                vpagers.setCurrentItem(0);
                setStatus(0);
                break;
            case R.id.ll_shiping:
                vpagers.setCurrentItem(1);
                setStatus(1);
                break;
            case R.id.ll_wode:
                vpagers.setCurrentItem(2);
                setStatus(2);
                break;
            case R.id.ll_yuncun:
                vpagers.setCurrentItem(3);
                setStatus(3);
                break;
            case R.id.ll_zhanghao:
                vpagers.setCurrentItem(4);
                setStatus(4);
                break;
        }
        Intent intControl = new Intent(UPDATESONGS);
        intControl.putExtra("op",0x30);
        sendBroadcast(intControl);
    }


    //调用读取判断全部的歌曲
    private void getSongsinSQL(){
        DBUtils db = new DBUtils(MainActivity.this);
        //判断数据库是否有歌曲
        if (!db.hasSongs()){
            //获取歌曲
            getSong();
            db.saveSongsList(songs);
            Log.i("", "getSongsinSQL++++保存到数据库");
        }
        else {
            songs = db.getSongHistory();
            db.closeDb();
            MyApplication myapp = (MyApplication)getApplicationContext();
            myapp.setSongs(songs);
            Log.i("", "getSongsinSQL++++从数据库读到歌曲数量==============" + songs.size());
        }
    }


    //获取本地歌曲
    private void getSong() {
        songs=new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        while (cursor.moveToNext()) {
            String titleStr, artistStr, filePathStr;
            long durationLon, sizeLong;
            int isMusicInt;
            //歌曲名称
            titleStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            //歌曲演唱者
            artistStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            //歌曲时长
            durationLon = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
            //歌曲文件路径
            filePathStr = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            //歌曲文件的大小
            sizeLong = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            //文件是否为音乐
            isMusicInt = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));

            //Song(String songNameStr, String songerNameStr, String songTimeStr, String fileSizeStr,String filePathStr )
            //对歌曲进行筛选
            if ((isMusicInt != 0) && (sizeLong > 0)) {
                Song song = new Song(titleStr, artistStr, String.valueOf(durationLon),String.valueOf(sizeLong), filePathStr);
                songs.add(song);
            }
        }
        cursor.close();

    }


    //搜索输入的歌曲进行模糊查询和精确查找
    private void searchSongs(){
        DBUtils db = new DBUtils(MainActivity.this);

        String name = edname.getText().toString().trim();
        Log.i("", "====================="+name);

        songs = db.getSongName(name);
        db.closeDb();
        if (name.length()!=0){
            if (songs.size()!=0){
                MyApplication myapp = (MyApplication)getApplicationContext();
                myapp.setSongs(songs);
                Log.i("", "getSongsinSQL++++从数据库读到查找到的歌曲数=============="+songs.size());
                Intent intent = new Intent(MainActivity.this,MusicActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(MainActivity.this,"没有此歌曲相关信息",Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(MainActivity.this,"请输入有效的歌曲名称",Toast.LENGTH_LONG).show();
        }
    }


    //流行歌单
    private void popularMusic(){
        DBUtils db = new DBUtils(MainActivity.this);
        songs=db.getPopularSongs();
        db.closeDb();
        if (songs.size()!=0){
            MyApplication myapp = (MyApplication)getApplicationContext();
            myapp.setSongs(songs);
            Log.i("", "getSongsinSQL++++从数据库读到查找到的歌曲数=============="+songs.size());
            Intent intent = new Intent(MainActivity.this,MusicActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this,"没有此分类的歌曲",Toast.LENGTH_LONG).show();
        }
    }


    //我喜欢的歌曲列表
    private void likeSongs(){
        DBUtils db = new DBUtils(MainActivity.this);
        songs=db.getSongLike();
        db.closeDb();
        if (songs.size()!=0){
            MyApplication myapp = (MyApplication)getApplicationContext();
            myapp.setSongs(songs);
            Log.i("", "getSongsinSQL++++从数据库读到查找到的歌曲数=============="+songs.size());
            Intent intent = new Intent(MainActivity.this,MusicActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(MainActivity.this,"还没有喜欢的歌曲哦！",Toast.LENGTH_LONG).show();
        }
    }


}
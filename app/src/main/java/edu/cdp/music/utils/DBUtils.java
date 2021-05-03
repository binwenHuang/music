package edu.cdp.music.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import edu.cdp.music.bean.Song;
import edu.cdp.music.bean.UserBean;
import edu.cdp.music.sqlite.SQLiteHelper;

/*
*参考1：http://www.360doc.com/content/19/1023/14/61825250_868585796.shtml
* 参考2：https://www.jianshu.com/p/7d098ef952d7
* */
public class DBUtils {
    private SQLiteHelper helper;
    private SQLiteDatabase db;

    public DBUtils(Context context) {
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 保存个人资料信息
     */
    public void saveUserInfo() {
        ContentValues cv = new ContentValues();
        cv.put("id", "11111");
        cv.put("pwd", "11111");
        db.insert(SQLiteHelper.U_USERINFO, null, cv);
    }


    /**
     * 获取个人资料信息
     */
    public UserBean getUserInfo(String userName) {
        String sql = "SELECT * FROM " + SQLiteHelper.U_USERINFO + " WHERE userName=?";
        Cursor cursor = db.rawQuery(sql, new String[]{userName});
        UserBean bean = null;
        while (cursor.moveToNext()) {
            bean = new UserBean();
            bean.userName=cursor.getString(cursor.getColumnIndex("userName"));
            bean.nickName=cursor.getString(cursor.getColumnIndex("nickName"));
            bean.sex=cursor.getString(cursor.getColumnIndex("sex"));
            bean.signature=cursor.getString(cursor
                    .getColumnIndex("signature"));
        }
        cursor.close();
        return bean;
    }


    /**
     * 修改个人资料
     */
    public void updateUserInfo(String key, String value, String userName) {
        ContentValues cv = new ContentValues();
        cv.put(key, value);
        db.update(SQLiteHelper.U_USERINFO, cv, "userName=?",
                new String[]{userName});
    }



    /**
     * 功能：判断歌曲记录是否存在
     * 返回值：存在返回true，不存在返回false
     */
    public boolean hasSongs() {
        boolean hasSong = false;
        String sql = "SELECT * FROM " + SQLiteHelper.U_SONG_PLAY_LIST;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount()>0) {
            hasSong = true;
        }
        cursor.close();
        return hasSong;
    }


    /**
     * 保存歌曲信息到数据库表U_SONG_PLAY_LIST
     */
    public int  saveSongsList(List<Song> songs) {
        long starttime = System.currentTimeMillis();
        System.out.println(starttime+"");
        int i=0;
        db.beginTransaction();//开始设置事务，用于同时执行，减少程序执行时间
        try{
            for (Song s: songs){
                i++;
                ContentValues cv = new ContentValues();
                cv.put("songNameStr", s.getSongNameStr());
                cv.put("singerNameStr", s.getSingerNameStr());
                cv.put("songTimeStr", s.getSongTimeStr());
                cv.put("filePathStr", s.getFilepathStr());
                cv.put("fileSizeStr", s.getFileSizeStr());
                cv.put("songClass", "流行");
                db.insert(SQLiteHelper.U_SONG_PLAY_LIST, null, cv);
                Log.i("", "saveSongsList: 保存成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            i=0;
        }finally {
            db.setTransactionSuccessful();   //设置事务处理成功，不设置会自动回滚不提交
            db.endTransaction();//处理事务
            long endtime = System.currentTimeMillis();
            System.out.println(endtime);
            System.out.println((endtime-starttime)/1000);
            return i;
        }
    }



    /**
     * 读取数据库歌曲
     */
    public List<Song> getSongHistory() {
        saveUserInfo();
        String sql = "SELECT * FROM " + SQLiteHelper.U_SONG_PLAY_LIST;
        Cursor cursor = db.rawQuery(sql, null);
        List<Song> songs = new ArrayList<Song>();
        Song bean;
        while (cursor.moveToNext()) {
            bean = new Song();
            bean.setSongNameStr(cursor.getString(cursor.getColumnIndex("songNameStr"))) ;
            bean.setSingerNameStr(cursor.getString(cursor.getColumnIndex("singerNameStr")));
            bean.setSongTimeStr(cursor.getString(cursor.getColumnIndex("songTimeStr")));;
            bean.setFilepathStr(cursor.getString(cursor.getColumnIndex("filePathStr")));
            bean.setFileSizeStr(cursor.getString(cursor.getColumnIndex("fileSizeStr")));
            songs.add(bean);
            bean = null;
        }
        cursor.close();
        return songs;
    }


    /**
     * 查找指定歌曲信息
     */
    public List<Song> getSongName(String songName) {
        String sql = "SELECT * FROM " + SQLiteHelper.U_SONG_PLAY_LIST+" WHERE songNameStr like '%"+songName+"%'";
        Cursor cursor = db.rawQuery(sql, null);
        List<Song> songs = new ArrayList<Song>();
        Song bean;
        while (cursor.moveToNext()) {
            bean = new Song();
            bean.setSongNameStr(cursor.getString(cursor.getColumnIndex("songNameStr"))) ;
            bean.setSingerNameStr(cursor.getString(cursor.getColumnIndex("singerNameStr")));
            bean.setSongTimeStr(cursor.getString(cursor.getColumnIndex("songTimeStr")));;
            bean.setFilepathStr(cursor.getString(cursor.getColumnIndex("filePathStr")));
            bean.setFileSizeStr(cursor.getString(cursor.getColumnIndex("fileSizeStr")));
            songs.add(bean);
            bean = null;
        }
        cursor.close();
        return songs;
    }

    /**
     * 判断当前歌曲是否已经加入我喜欢的歌单
     * @return
     */
    public boolean getSongNamelike(String songName) {
        boolean hasSong = false;
        String sql = "SELECT * FROM " + SQLiteHelper.U_SONG_LIKE_LIST+" WHERE songNameStr=?";
        Cursor cursor = db.rawQuery(sql,new String[]{songName});
        if (cursor.getCount()>0) {
            hasSong = true;
        }
        cursor.close();
        return hasSong;
    }


    //保存喜欢的歌曲到数据库
    public List<Song> setLoveInDatabase(String name){
        ContentValues cv = new ContentValues();
        cv.put("userName", "1");
        cv.put("songNameStr", name);
        db.insert(SQLiteHelper.U_SONG_LIKE_LIST, null, cv);
        db.close();
        return null;
    }


    //保存喜欢的歌曲到数据库
    public List<Song> DeleteLoveInDatabase(String name){
        String sql = "Delete  from "+SQLiteHelper.U_SONG_LIKE_LIST+" WHERE songNameStr=?";
        db.execSQL(sql, new String[]{name});
        db.close();
        return null;
    }





    /**
     * 查找喜欢的歌曲
     */
    public List<Song> getSongLike() {
        String sql = "select * from "+SQLiteHelper.U_SONG_PLAY_LIST+" inner join "+SQLiteHelper.U_SONG_LIKE_LIST+" on "+SQLiteHelper.U_SONG_PLAY_LIST+".songNameStr = "+SQLiteHelper.U_SONG_LIKE_LIST+".songNameStr";
        Cursor cursor = db.rawQuery(sql, null);
        List<Song> songs = new ArrayList<Song>();
        Song bean;
        while (cursor.moveToNext()) {
            bean = new Song();
            bean.setSongNameStr(cursor.getString(cursor.getColumnIndex("songNameStr"))) ;
            bean.setSingerNameStr(cursor.getString(cursor.getColumnIndex("singerNameStr")));
            bean.setSongTimeStr(cursor.getString(cursor.getColumnIndex("songTimeStr")));;
            bean.setFilepathStr(cursor.getString(cursor.getColumnIndex("filePathStr")));
            bean.setFileSizeStr(cursor.getString(cursor.getColumnIndex("fileSizeStr")));
            songs.add(bean);
            bean = null;
        }
        cursor.close();
        return songs;
    }


    /**
     * 流行歌单
     */
    public List<Song> getPopularSongs() {
        String sql = "SELECT * FROM " + SQLiteHelper.U_SONG_PLAY_LIST+" WHERE songClass='流行'";
        Cursor cursor = db.rawQuery(sql, null);
        List<Song> songs = new ArrayList<Song>();
        Song bean;
        while (cursor.moveToNext()) {
            bean = new Song();
            bean.setSongNameStr(cursor.getString(cursor.getColumnIndex("songNameStr"))) ;
            bean.setSingerNameStr(cursor.getString(cursor.getColumnIndex("singerNameStr")));
            bean.setSongTimeStr(cursor.getString(cursor.getColumnIndex("songTimeStr")));;
            bean.setFilepathStr(cursor.getString(cursor.getColumnIndex("filePathStr")));
            bean.setFileSizeStr(cursor.getString(cursor.getColumnIndex("fileSizeStr")));
            songs.add(bean);
            bean = null;
        }
        cursor.close();
        return songs;
    }


    /**
     * 判断歌曲记录是否存在
     */
    public boolean hasVideoPlay(int chapterId, int videoId,String userName) {
        boolean hasVideo = false;
        String sql = "SELECT * FROM " + SQLiteHelper.U_SONG_PLAY_LIST
                + " WHERE chapterId=? AND videoId=? AND userName=?";
        Cursor cursor = db.rawQuery(sql, new String[] { chapterId + "",
                videoId + "" ,userName});
        if (cursor.moveToFirst()) {
            hasVideo = true;
        }
        cursor.close();
        return hasVideo;
    }



    /**
     * 删除已经存在的歌曲记录
     */
    public boolean delVideoPlay(int chapterId, int videoId,String userName) {
        boolean delSuccess=false;
        int row = db.delete(SQLiteHelper.U_SONG_PLAY_LIST,
                " chapterId=? AND videoId=? AND userName=?", new String[] { chapterId + "",
                        videoId + "" ,userName});
        if (row > 0) {
            delSuccess=true;
        }
        return delSuccess;
    }



    //关闭数据库
    public void closeDb(){
        if(db!=null)
            db.close();
    }


    public boolean login(String user_id_get,String user_password_get){
        String sql="select * from users where id=? and pwd=?";
        Cursor cursor=db.rawQuery(sql, new String[]{user_id_get,user_password_get});
        if(cursor.moveToFirst()==true){
            cursor.close();
            return true;
        }
        return false;
    }

}

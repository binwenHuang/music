package edu.cdp.music.sqllite;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 2;
    public static String DB_NAME = "song.db";
    public static final String U_USERINFO = "userinfo";// 个人资料
    public static final String U_SONG_PLAY_LIST = "songlist";//歌曲表
    public static final String U_SONG_LIKE_LIST = "songlike";//收藏歌曲表
    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * 创建个人信息表
         */
        db.execSQL("CREATE TABLE  IF NOT EXISTS " + U_USERINFO + "( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userName VARCHAR, "// 用户名
                + "nickName VARCHAR, "// 昵称
                + "sex VARCHAR, "// 性别
                + "signature VARCHAR"// 签名
                + ")");
        // 创音乐歌单表
        db.execSQL("CREATE TABLE  IF NOT EXISTS " + U_SONG_PLAY_LIST + "( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "songNameStr VARCHAR, "// 用户名
                +"singerNameStr VARCHAR,"//歌曲名
                +"songTimeStr VARCHAR,"//歌手名称
                +"filePathStr VARCHAR,"//歌曲文件位置
                +"fileSizeStr VARCHAR,"//歌曲大小
                +"songClass VARCHAR"// 歌曲类型
                + ")");

        // 创我的喜爱的音乐歌单表
        db.execSQL("CREATE TABLE  IF NOT EXISTS " + U_SONG_LIKE_LIST + "( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userName VARCHAR,"
                +"songNameStr VARCHAR"//歌曲名
                + ")");
    }

    /**
     * 当数据库版本号增加时才会调用此方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + U_USERINFO);
        db.execSQL("DROP TABLE IF EXISTS " + U_SONG_PLAY_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + U_SONG_LIKE_LIST);
        onCreate(db);
    }
}

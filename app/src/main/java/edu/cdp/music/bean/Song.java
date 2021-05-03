package edu.cdp.music.bean;

public class Song {
    private String songNameStr;
    private String songTimeStr;
    private String singerNameStr;
    private String fileSizeStr;
    private String filepathStr;

    public Song(String songNameStr, String singerNameStr, String songTimeStr, String fileSizeStr, String filepathStr) {
        this.songNameStr = songNameStr;
        this.songTimeStr = songTimeStr;
        this.singerNameStr = singerNameStr;
        this.fileSizeStr = fileSizeStr;
        this.filepathStr = filepathStr;
    }

    public Song() {

    }


    public String getSongNameStr() {
        return songNameStr;
    }

    public String getSongTimeStr() {
        return songTimeStr;
    }

    public String getSingerNameStr() {
        return singerNameStr;
    }

    public String getFileSizeStr() {
        return fileSizeStr;
    }

    public String getFilepathStr() {
        return filepathStr;
    }

    public void setSongNameStr(String songNameStr) {
        this.songNameStr = songNameStr;
    }

    public void setSongTimeStr(String songTimeStr) {
        this.songTimeStr = songTimeStr;
    }

    public void setSingerNameStr(String singerNameStr) {
        this.singerNameStr = singerNameStr;
    }

    public void setFileSizeStr(String fileSizeStr) {
        this.fileSizeStr = fileSizeStr;
    }

    public void setFilepathStr(String filepathStr) {
        this.filepathStr = filepathStr;
    }
}

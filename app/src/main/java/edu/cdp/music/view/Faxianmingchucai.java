package edu.cdp.music.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import edu.cdp.music.R;

public class Faxianmingchucai {
    private Context context;
    private LayoutInflater myli;

    public  Faxianmingchucai(Context context){
        this.context = context;
        this.myli = LayoutInflater.from(context);

    }
    public View getView(){
        return myli.inflate( R.layout.fx_mingchucai,null);
    }

}

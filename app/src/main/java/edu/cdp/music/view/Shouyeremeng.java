package edu.cdp.music.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import edu.cdp.music.R;

public class Shouyeremeng {
    private Context context;
    private LayoutInflater myli;

    public  Shouyeremeng(Context context){
        this.context = context;
        this.myli = LayoutInflater.from(context);

    }
    public View getView(){
        return myli.inflate( R.layout.sy_remeng,null);
    }

}

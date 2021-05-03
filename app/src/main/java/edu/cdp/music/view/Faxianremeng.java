package edu.cdp.music.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import edu.cdp.music.R;

public class Faxianremeng {
    private Context context;
    private LayoutInflater myli;

    public  Faxianremeng(Context context){
        this.context = context;
        this.myli = LayoutInflater.from(context);

    }
    public View getView(){
        return myli.inflate( R.layout.fx_remeng,null);
    }

}

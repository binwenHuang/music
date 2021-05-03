package edu.cdp.music.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import edu.cdp.music.R;

public class Viewfaxian implements View.OnClickListener {
    private Context context;
    private LayoutInflater myli;

    public Viewfaxian(Context context){
        this.context = context;
        this.myli = LayoutInflater.from(context);

    }

    public View getView(){
        return myli.inflate( R.layout.view_faxian,null);
    }


    @Override
    public void onClick(View v) {

    }
}

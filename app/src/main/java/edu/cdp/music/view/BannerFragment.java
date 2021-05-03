package edu.cdp.music.view;

import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.cdp.music.R;

public class BannerFragment extends Fragment {
    private int imgRes;

    public BannerFragment() {
        // Required empty public constructor
    }
    protected View onCreate(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        super.onCreate ( savedInstanceState );
        View inflate=inflater.inflate(R.layout.fragment, container, false);
        ImageView ivBanner=inflate.findViewById(R.id.iv_guanggao);
        ivBanner.setImageResource(imgRes);
        return inflate;
    }
    public void setImage(int imgRes)
    {
        this.imgRes=imgRes;
    }

}
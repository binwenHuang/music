package edu.cdp.music.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class FaxianAdapter extends PagerAdapter {
    private ArrayList<View> fxviews;

    @Override
    public int getCount() {
        return fxviews.size ();
    }

    public FaxianAdapter(ArrayList<View> viewLists){
        this.fxviews = viewLists;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(fxviews.get(position));
        return fxviews.get(position);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView (fxviews.get(position));
    }
}

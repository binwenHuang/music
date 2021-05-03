package edu.cdp.music.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class WodeAdpater extends PagerAdapter {
    private ArrayList<View> wdviews;
    @Override
    public int getCount() {
        return wdviews.size ();
    }

    public WodeAdpater(ArrayList<View> viewLists){
        this.wdviews = viewLists;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(wdviews.get(position));
        return wdviews.get(position);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView (wdviews.get(position));
    }
}

package edu.cdp.music.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class ShouyeAdapter extends PagerAdapter {
    private ArrayList<View> syviews;
    @Override
    public int getCount() {
        return syviews.size ();
    }

    public ShouyeAdapter(ArrayList<View> viewLists){
        this.syviews = viewLists;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(syviews.get(position));
        return syviews.get(position);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView (syviews.get(position));
    }
}

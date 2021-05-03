package edu.cdp.music.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import edu.cdp.music.view.Shipingjiqiao;

public class ShipingAdapter extends PagerAdapter {
    private ArrayList<View> spviews;
    @Override
    public int getCount() {
        return spviews.size ();
    }

    public ShipingAdapter(ArrayList<View> viewLists){
        this.spviews = viewLists;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(spviews.get(position));
        return spviews.get(position);
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView (spviews.get(position));
    }
}

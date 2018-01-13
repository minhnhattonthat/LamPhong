package com.lamphongstore.lamphong.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.material.RoundedImageView;
import com.lamphongstore.lamphong.model.AdsItem;

import java.util.ArrayList;

/**
 * Created by HungNguyen on 3/15/17.
 */

public class AdsPagerAdapter extends PagerAdapter {

    private ArrayList<AdsItem> fakeList;

    private Context context;
    private int maxWidth;
    private int maxHeight;
    private LayoutInflater inflater;

    public AdsPagerAdapter(ArrayList<AdsItem> adsList, Context context) {
        this.fakeList = adsList;
        this.context = context;
        maxWidth = dpToPx(300);
        maxHeight = dpToPx(300);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return fakeList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View rootView = inflater.inflate(R.layout.page_promotion_item, container, false);

        RoundedImageView imagePage = (RoundedImageView) rootView.findViewById(R.id.image_item_promotion);
        imagePage.setMinimumWidth(maxWidth);
        imagePage.setMinimumHeight(maxHeight);
        imagePage.setImageUrl(fakeList.get(position).getImage().getImgUrl());
        imagePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(fakeList.get(position).getLink_to());

                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                Toast.makeText(context, fakeList.get(position).getLink_to(), Toast.LENGTH_SHORT).show();
            }
        });

        container.addView(rootView);
        return rootView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}

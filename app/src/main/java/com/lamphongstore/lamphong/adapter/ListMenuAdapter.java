package com.lamphongstore.lamphong.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lamphongstore.lamphong.R;
import com.lamphongstore.lamphong.material.SideMenu;

/**
 * Created by HungNguyen on 3/16/17.
 */

public class ListMenuAdapter extends BaseAdapter {

    private Context context;
    private SideMenu.SideMenuItem[] arrayList;
    private Typeface mainFont;

    public ListMenuAdapter(Context context, SideMenu.SideMenuItem[] arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        mainFont = Typeface.createFromAsset(context.getAssets(), "fonts/SF-UI-Text-Regular.otf");
    }

    @Override
    public int getCount() {
        return arrayList.length;
    }

    @Override
    public Object getItem(int position) {
        return arrayList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_menu, parent, false);
            holder = new ViewHolder();

            holder.iconMenu = (ImageView) convertView.findViewById(R.id.menu_icon);
            holder.titleMenu = (TextView) convertView.findViewById(R.id.menu_title);
            holder.titleMenu.setTypeface(mainFont);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SideMenu.SideMenuItem sideMenuItem = arrayList[position];
        holder.iconMenu.setImageResource(sideMenuItem.getIconMenu());
        holder.titleMenu.setText(sideMenuItem.getTitleMenu());
        if (position == getCount() - 1) {
            Log.e("Check", position + "");
            holder.titleMenu.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        if (position == 0) {
            holder.titleMenu.setTextColor(Color.parseColor("#7b7b7b"));
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView iconMenu;
        private TextView titleMenu;
    }
}

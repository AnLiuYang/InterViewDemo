package com.yang.interviewdemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amap.api.maps2d.model.Text;
import com.lidroid.xutils.BitmapUtils;
import com.yang.interviewdemo.R;
import com.yang.interviewdemo.data.StoreData;

import java.util.ArrayList;

public class StoreAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<StoreData> list;
    Context mContext;

    public StoreAdapter(Context context){
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        list = new ArrayList<StoreData>();
    }

    public void upData(ArrayList<StoreData> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyView view;
        if (convertView == null){
            view = new MyView();
            convertView = inflater.inflate(R.layout.adapter_store, null);
            view.img_store = (ImageView) convertView.findViewById(R.id.img_item_store_image);
            view.txt_name = (TextView) convertView.findViewById(R.id.txt_item_store_name);
            view.txt_consume = (TextView) convertView.findViewById(R.id.txt_item_store_consume);
            view.txt_fondType = (TextView) convertView.findViewById(R.id.txt_item_store_foodType);
            view.rating_score = (RatingBar) convertView.findViewById(R.id.rat_item_store_score);
            view.txt_like = (TextView) convertView.findViewById(R.id.txt_item_store_like);

            convertView.setTag(view);
        } else {
          view = (MyView) convertView.getTag();
        }
        StoreData store = list.get(position);
        String picUrl = store.getPicUrl();
        if(!picUrl.equals("") || picUrl != null){
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.display(view.img_store, picUrl);
        }
        view.txt_name.setText(store.getName());
        view.txt_consume.setText(store.getConsume() + " " +  mContext.getString(R.string.consume));
        String store_detail = store.getFoodType() + "   " + store.getBusinessCenter() + "   " + store.getDistance();
        view.txt_fondType.setText(store_detail);
        view.rating_score.setRating(store.getTotalScore() / 2);
        view.txt_like.setText(store.getLikeCount() + "");

        return convertView;
    }

    private class MyView{
        ImageView img_store;
        TextView txt_name;
        TextView txt_consume;
        TextView txt_fondType;
        RatingBar rating_score;
        TextView txt_like;
    }
}

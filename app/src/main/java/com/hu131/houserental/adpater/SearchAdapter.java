package com.hu131.houserental.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hu131.houserental.R;

import java.util.zip.Inflater;

/**
 * Created by Hu131 on 2016/3/18.
 */
public class SearchAdapter extends BaseAdapter {

    Context context;

    public SearchAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_search, null);
        return view;
    }
}

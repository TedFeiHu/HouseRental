package com.hu131.houserental.adpater;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.hu131.houserental.R;
import com.hu131.houserental.bean.HouseInfo;
import com.hu131.houserental.utils.BitmapCache;

import java.util.List;

/**
 * 发现的Adapter
 * Created by Hu131 on 2016/3/18.
 */
public class SearchAdapter extends BaseAdapter {

    Context context;
    List<HouseInfo> houseInfoList;
    RequestQueue requestQueue;

    public SearchAdapter(Context context, List<HouseInfo> houseInfoList, RequestQueue requestQueue) {
        this.context = context;
        this.houseInfoList = houseInfoList;
        this.requestQueue = requestQueue;
    }

    @Override
    public int getCount() {
        return houseInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return houseInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_search, null);
            holder.image = (NetworkImageView) convertView.findViewById(R.id.search_item_image);
            holder.overview = (TextView) convertView.findViewById(R.id.search_item_overview);
            holder.title = (TextView) convertView.findViewById(R.id.search_item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.overview.setText(houseInfoList.get(position).getOverview());
        holder.title.setText(houseInfoList.get(position).getTitle());

        try {
            ImageLoader loader = new ImageLoader(requestQueue, new BitmapCache());
            String url = houseInfoList.get(position).getImages().get(0);
            if (!TextUtils.isEmpty(url)){
                holder.image.setImageUrl(url, loader);
                holder.image.setDefaultImageResId(R.mipmap.loading_holder);
                holder.image.setErrorImageResId(R.mipmap.no_photo_80x60);
            }
            Log.i("-------", houseInfoList.get(position).getImages().get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    class ViewHolder {
        NetworkImageView image;
        TextView overview;
        TextView title;
    }
}

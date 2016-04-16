package com.hu131.houserental.adpater;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hu131.houserental.R;
import com.hu131.houserental.activity.LocationActivity;
import com.hu131.houserental.utils.DatabaseHelper;

import java.util.List;

/**
 * 最近选择列表的adapter
 * Created by Administrator on 2016/4/14.
 */
public class HistoryCityAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> hitCities;

    public HistoryCityAdapter(Context context, List<String> hitCities) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.hitCities = hitCities;
    }
    @Override
    public int getCount() {
        return hitCities.size();
    }

    @Override
    public Object getItem(int position) {
        return hitCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_city, null);
        TextView cityTv = (TextView) convertView.findViewById(R.id.city);
        cityTv.setText(hitCities.get(position));
        final String city = hitCities.get(position);
        cityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, city,Toast.LENGTH_SHORT).show();
                insertCity(city);
                LocationActivity.city = city;
            }
        });
        return convertView;
    }
    public void insertCity(String name) {
        DatabaseHelper hisDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = hisDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from recentcity where name = '" + name + "'", null);
        if (cursor.getCount() > 0) { //
            db.delete("recentcity", "name = ?", new String[]{name});
        }
        db.execSQL("insert into recentcity(name, date) values('" + name + "', " + System.currentTimeMillis() + ")");
        cursor.close();
        db.close();
    }
}

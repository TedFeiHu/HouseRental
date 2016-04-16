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
import com.hu131.houserental.bean.City;
import com.hu131.houserental.utils.DatabaseHelper;

import java.util.ArrayList;

public class ResultListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<City> results;
    private Context context;

    public ResultListAdapter(Context context, ArrayList<City> results) {
        inflater = LayoutInflater.from(context);
        this.results = results;
        this.context = context;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_location, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView
                    .findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(results.get(position).getName());
        final String city = results.get(position).getName();
        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, city,Toast.LENGTH_SHORT).show();
                insertCity(city);
                LocationActivity.city = city;
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView name;
    }
    public void insertCity(String name) {
        DatabaseHelper hisDbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = hisDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from recentcity where name = '" + name + "'", null);
        if (cursor.getCount() > 0) { //
            db.delete("recentcity", "name = ?", new String[] { name });
        }
        db.execSQL("insert into recentcity(name, date) values('" + name + "', " + System.currentTimeMillis() + ")");
        cursor.close();
        db.close();
    }
}
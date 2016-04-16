package com.hu131.houserental.adpater;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hu131.houserental.R;
import com.hu131.houserental.activity.LocationActivity;
import com.hu131.houserental.bean.City;
import com.hu131.houserental.utils.BDMapUtil;
import com.hu131.houserental.utils.DatabaseHelper;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 定位列表的adapter
 * Created by Administrator on 2016/4/14.
 */
public class LocationListAdapter extends BaseAdapter {
    private Context context;
    private List<City> list;
    private List<City> hotList;
    private List<String> hisCity;
    private LayoutInflater inflater;

    //    private final String[] sections; //放的是排序后的城市名字
    final int VIEW_TYPE = 5;

    public LocationListAdapter(Context context, List<City> list, List<City> hotList, List<String> hisCity) {
        this.context = context;
        this.hotList = hotList;
        this.hisCity = hisCity;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
//        sections = new String[list.size()];
        for (int i = 0; i < list.size(); i++) { //排序，
            // 当前汉语拼音首字母
            String currentStr = getAlpha(list.get(i).getPinyi());
            // 上一个汉语拼音首字母，如果不存在为" "
            String previewStr = (i - 1) >= 0 ? getAlpha(list.get(i - 1).getPinyi()) : " ";
            if (!previewStr.equals(currentStr)) {
                String name = getAlpha(list.get(i).getPinyi());
                LocationActivity.alphaIndexer.put(name, i);
//                sections[i] = name;
            }
        }
    }


    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        return position < 4 ? position : 4;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    ViewHolder holder;

    private class ViewHolder {
        TextView alpha; // 首字母标题
        TextView name; // 城市名字
    }

    TextView city;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (getItemViewType(position)) {
            case 0: //定位
                convertView = inflater.inflate(R.layout.frist_list_item, null);
                TextView locateHint = (TextView) convertView.findViewById(R.id.locateHint);
                city = (TextView) convertView.findViewById(R.id.lng_city);
                city.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (BDMapUtil.LOCATE_PROCESS == 2) { //如果已经定位成功，城市已经写到textView里
                            Toast.makeText(context, city.getText().toString(), Toast.LENGTH_SHORT).show();
                            LocationActivity.city = city.getText().toString();
                        } else if (BDMapUtil.LOCATE_PROCESS == 3) { //重新定位
                            LocationListAdapter.this.notifyDataSetChanged(); //恐怕。。。
                            BDMapUtil.locationStop();
                            BDMapUtil.location(context);
                        }
                    }
                });

                ProgressBar pbLocate = (ProgressBar) convertView.findViewById(R.id.pbLocate);
                if (BDMapUtil.LOCATE_PROCESS == 1) { // 正在定位
                    locateHint.setText("正在定位");
                    city.setVisibility(View.GONE);
                    pbLocate.setVisibility(View.VISIBLE);
                } else if (BDMapUtil.LOCATE_PROCESS == 2) { // 定位成功
                    locateHint.setText("当前定位城市");
                    city.setVisibility(View.VISIBLE);
                    city.setText(BDMapUtil.city);
                    BDMapUtil.locationStop();
                    pbLocate.setVisibility(View.GONE);
                } else if (BDMapUtil.LOCATE_PROCESS == 3) {
                    locateHint.setText("未定位到城市,请选择");
                    city.setVisibility(View.VISIBLE);
                    city.setText("重新选择");
                    pbLocate.setVisibility(View.GONE);
                }
                break;

            case 1: // 最近访问城市
                convertView = inflater.inflate(R.layout.recent_city, null);
                GridView recentCity = (GridView) convertView.findViewById(R.id.recent_city);
                recentCity.setAdapter(new HistoryCityAdapter(context, hisCity));
                recentCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(context, hisCity.get(position), Toast.LENGTH_SHORT).show();
                        LocationActivity.city = hisCity.get(position);
                        insertCity(hisCity.get(position));
                    }
                });
                TextView recentHint = (TextView) convertView.findViewById(R.id.recentHint);
                recentHint.setText("最近访问的城市");
                break;
            case 2: // 热门城市
                convertView = inflater.inflate(R.layout.recent_city, null);
                GridView hotCity = (GridView) convertView.findViewById(R.id.recent_city);
                hotCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(context, hotList.get(position).getName(), Toast.LENGTH_SHORT).show();
                        LocationActivity.city = hotList.get(position).getName();
                        insertCity(hotList.get(position).getName());
                    }
                });
                hotCity.setAdapter(new HotCityAdapter(context, hotList));
                TextView hotHint = (TextView) convertView.findViewById(R.id.recentHint);
                hotHint.setText("热门城市");
                break;
            case 3: //全部城市
                convertView = inflater.inflate(R.layout.total_item, null);
                break;
            case 4:
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item_location, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (position >= 1) {
                    holder.name.setText(list.get(position).getName());
                    final String city = list.get(position).getName();
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, city, Toast.LENGTH_SHORT).show();
                            insertCity(city);
                            LocationActivity.city = city;
                        }
                    });
                    String currentStr = getAlpha(list.get(position).getPinyi());
                    String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getPinyi()) : " ";
                    if (!previewStr.equals(currentStr)) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        holder.alpha.setText(currentStr);
                    } else {
                        holder.alpha.setVisibility(View.GONE);
                    }
                }
                break;
        }

        return convertView;
    }


    // 获得汉语拼音首字母
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else if (str.equals("0")) {
            return "定位";
        } else if (str.equals("1")) {
            return "最近";
        } else if (str.equals("2")) {
            return "热门";
        } else if (str.equals("3")) {
            return "全部";
        } else {
            return "#";
        }
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

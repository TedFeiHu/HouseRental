package com.hu131.houserental.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hu131.houserental.R;
import com.hu131.houserental.adpater.ResultListAdapter;
import com.hu131.houserental.bean.City;
import com.hu131.houserental.utils.DBHelper;
import com.hu131.houserental.utils.DatabaseHelper;
import com.hu131.houserental.view.LocationLetterListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/5.
 * 定位activity
 */
public class LocationActivity extends AppCompatActivity implements AbsListView.OnScrollListener {
    private BaseAdapter adapter;
    private ResultListAdapter resultListAdapter;
    private ListView personList;
    private ListView resultList;
    private TextView overlay; // 对话框首字母textview
    private LocationLetterListView letterListView; // A-Z listview
    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
    private String[] sections;// 存放存在的汉语拼音首字母
    private Handler handler;
    private OverlayThread overlayThread; // 显示首字母对话框
    private ArrayList<City> allCity_lists; // 所有城市列表
    private ArrayList<City> city_lists;// 城市列表
    private ArrayList<City> city_hot;
    private ArrayList<City> city_result;
    private ArrayList<String> city_history;
    private EditText sh;
    private TextView tv_noresult;

//    private LocationClient mLocationClient;
//    private MyLocationListener mMyLocationListener;

    //    private String currentCity; // 用于保存定位到的城市
    private int locateProcess = 1; // 记录当前定位的状态 正在定位-定位成功-定位失败
    private boolean isNeedFresh;

    private DatabaseHelper helper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        initToolbar();

        initData();
        initCity();
        initHotCity();
        initHisCity();
    }


    //初始化toolBar
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_layer2);
        ((TextView) toolbar.findViewById(R.id.toolbar_layer2_title)).setText("选择城市");
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.layer2_toolbar_btn_off);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //toolBar返回键回调事件
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化全局对象与数据
     */
    private void initData() {
        handler = new Handler();
        overlayThread = new OverlayThread();
        helper = new DatabaseHelper(this); //最近访问城市的DatabaseHelper
        allCity_lists = new ArrayList<>();//所有的城市列表
        city_hot = new ArrayList<>(); // 城市列表
        city_result = new ArrayList<>();
        city_history = new ArrayList<>(); //历史访问城市
        personList = (ListView) findViewById(R.id.list_view);
        resultList = (ListView) findViewById(R.id.search_result);
        sh = (EditText) findViewById(R.id.sh);
        sh.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString() == null || "".equals(s.toString())) {
                    letterListView.setVisibility(View.VISIBLE);
                    personList.setVisibility(View.VISIBLE);
                    resultList.setVisibility(View.GONE);
                    tv_noresult.setVisibility(View.GONE);
                } else {
                    city_result.clear();
                    letterListView.setVisibility(View.GONE);
                    personList.setVisibility(View.GONE);
                    getResultCityList(s.toString());
                    if (city_result.size() <= 0) {
                        tv_noresult.setVisibility(View.VISIBLE);
                        resultList.setVisibility(View.GONE);
                    } else {
                        tv_noresult.setVisibility(View.GONE);
                        resultList.setVisibility(View.VISIBLE);
                        resultListAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        }); // 输入城市编辑框的文字改变事件的监视
        tv_noresult = (TextView) findViewById(R.id.tv_noresult);
        letterListView = (LocationLetterListView) findViewById(R.id.list_view_location_letter);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
        alphaIndexer = new HashMap<>();
        isNeedFresh = true;
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position >= 4) {

                    Toast.makeText(getApplicationContext(),
                            allCity_lists.get(position).getName(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        locateProcess = 1;

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL
                || scrollState == SCROLL_STATE_FLING) {
            isScroll = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    // 设置overlay不可见
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    /**
     * 全部城市
     */
    public void initCity() {
        City city = new City("定位", "0"); // 当前定位城市
        allCity_lists.add(city);
        city = new City("最近", "1"); // 最近访问的城市
        allCity_lists.add(city);
        city = new City("热门", "2"); // 热门城市
        allCity_lists.add(city);
        city = new City("全部", "3"); // 全部城市
        allCity_lists.add(city);
        city_lists = getCityList();
        allCity_lists.addAll(city_lists);

    }

    /**
     * 热门城市
     */
    private void initHotCity() {
        City city = new City("上海", "2");
        city_hot.add(city);
        city = new City("北京", "2");
        city_hot.add(city);
        city = new City("广州", "2");
        city_hot.add(city);
        city = new City("深圳", "2");
        city_hot.add(city);
        city = new City("武汉", "2");
        city_hot.add(city);
        city = new City("天津", "2");
        city_hot.add(city);
        city = new City("西安", "2");
        city_hot.add(city);
        city = new City("南京", "2");
        city_hot.add(city);
        city = new City("杭州", "2");
        city_hot.add(city);
        city = new City("成都", "2");
        city_hot.add(city);
        city = new City("重庆", "2");
        city_hot.add(city);

    }

    /**
     * 历史访问
     */
    private void initHisCity() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from recentcity order by date desc limit 0, 3", null);
        while (cursor.moveToNext()) {
            city_history.add(cursor.getString(1));
        }
        cursor.close();
        db.close();
    }


    /**
     * 输入城市编辑框的文字改变事件的监视器
     */
    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString() == null || "".equals(s.toString())) {
                letterListView.setVisibility(View.VISIBLE);
                personList.setVisibility(View.VISIBLE);
                resultList.setVisibility(View.GONE);
                tv_noresult.setVisibility(View.GONE);
            } else {
                city_result.clear();
                letterListView.setVisibility(View.GONE);
                personList.setVisibility(View.GONE);
                getResultCityList(s.toString());
                if (city_result.size() <= 0) {
                    tv_noresult.setVisibility(View.VISIBLE);
                    resultList.setVisibility(View.GONE);
                } else {
                    tv_noresult.setVisibility(View.GONE);
                    resultList.setVisibility(View.VISIBLE);
                    resultListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * 得到匹配到的结果(可匹配中文，匹配拼音)
     *
     * @param keyword 输入框输入的key
     */
    @SuppressWarnings("unchecked")
    private void getResultCityList(String keyword) {
        DBHelper dbHelper = new DBHelper(this);
        try {
            dbHelper.createDataBase();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(
                    "select * from city where name like \"%" + keyword
                            + "%\" or pinyin like \"%" + keyword + "%\"", null);
            City city;
            Log.e("info", "length = " + cursor.getCount());
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(1), cursor.getString(2));
                city_result.add(city);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(city_result, comparator);
    }

    /**
     * a-z排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyi().substring(0, 1);
            String b = rhs.getPinyi().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    //从数据库得到城市列表
    @SuppressWarnings("unchecked")
    private ArrayList<City> getCityList() {
        DBHelper dbHelper = new DBHelper(this);
        ArrayList<City> list = new ArrayList<>();
        try {
            dbHelper.createDataBase();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from city", null);
            City city;
            while (cursor.moveToNext()) {
                city = new City(cursor.getString(1), cursor.getString(2));
                list.add(city);
            }
            cursor.close();
            db.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(list, comparator);
        return list;
    }

    private boolean isScroll = false;
    //678
    private class LetterListViewListener implements LocationLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                personList.setSelection(position);
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
                handler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                handler.postDelayed(overlayThread, 1000);
            }
        }
    }

}

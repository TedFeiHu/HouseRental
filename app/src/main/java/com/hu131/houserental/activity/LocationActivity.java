package com.hu131.houserental.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hu131.houserental.R;
import com.hu131.houserental.adpater.LocationListAdapter;
import com.hu131.houserental.adpater.ResultListAdapter;
import com.hu131.houserental.bean.City;
import com.hu131.houserental.utils.BDMapUtil;
import com.hu131.houserental.utils.Constants;
import com.hu131.houserental.utils.DBHelper;
import com.hu131.houserental.utils.DatabaseHelper;
import com.hu131.houserental.view.LocationLetterListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * 重写LocationActivity2
 *
 * 想法，定位因该在欢迎界面做，这里的activity打开，就是让用户选择的城市的，除非未定位到城市，
 * 启动定位放到欢迎页　结束可以放在这　
 * Created by Administrator on 2016/4/12.
 */
public class LocationActivity extends AppCompatActivity {
    public static HashMap<String, Integer> alphaIndexer = new HashMap<>();// 存放存在的汉语拼音首字母和与之对应的列表位置
    private ArrayList<City> allCity_lists = new ArrayList<>(); //总共allCityListView上要显示的列表
    private ArrayList<City> city_hot = new ArrayList<>(); //热门城市
    private ArrayList<String> city_history = new ArrayList<>(); //历史城市
    private LocationLetterListView letterListView;  //右边的a~z列表
    private ListView allCityListView;  //所有城市的列表
    private EditText inputCityEdit;  //输入框
    private TextView overlay; // 对话框首字母textView
    private OverlayThread overlayThread;
    private Handler mHandler = new Handler();
    private ListView resultList;  //编辑框输入字符返回的结果
    private ArrayList<City> city_result = new ArrayList<>();  //返回的结果集
    private TextView tv_noresult;  //无返回结果显示这个TextView
    private ResultListAdapter resultListAdapter;
    public static String city = "";  //用户最后确定的city

    @Override
    protected void onResume() {
        super.onResume();
        BDMapUtil.location(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        resultListAdapter = new ResultListAdapter(this, city_result);
        initToolbar();
        initCity();
        initView();
    }

    @Override
    protected void onStop() {
        BDMapUtil.locationStop();
        super.onStop();
    }

    private void initView() {
        overlayThread = new OverlayThread();
        initOverlay();
        letterListView = (LocationLetterListView) findViewById(R.id.list_view_location_letter);
        initLetterListView();
        resultList = (ListView) findViewById(R.id.search_result);
        tv_noresult = (TextView) findViewById(R.id.tv_noresult);
        inputCityEdit = (EditText) findViewById(R.id.et_input_city);
        initInputEdit();
        resultList.setAdapter(resultListAdapter);
        allCityListView = (ListView) findViewById(R.id.list_view);
        initAllCityView();

    }

    //编辑框文字改变事件
    private void initInputEdit() {
        inputCityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || "".equals(s.toString())) {
                    letterListView.setVisibility(View.VISIBLE);
                    allCityListView.setVisibility(View.VISIBLE);
                    resultList.setVisibility(View.GONE);
                    tv_noresult.setVisibility(View.GONE);
                } else {
                    city_result.clear();
                    letterListView.setVisibility(View.GONE);
                    allCityListView.setVisibility(View.GONE);
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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



    // 初始化汉语拼音首字母弹出提示框
    private void initOverlay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        overlay = (TextView) inflater.inflate(R.layout.location_overlay, null);
        overlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlay, lp);
    }

    // 设置overlay不可见的线程
    private class OverlayThread implements Runnable {
        @Override
        public void run() {
            overlay.setVisibility(View.GONE);
        }
    }

    /**
     * 触摸旁边的LetterListView的监听事件,
     * 触摸到哪个字符，listView相应的跳转
     * 并显示overlay
     */
    private void initLetterListView() {
        letterListView.setOnTouchingLetterChangedListener(new LocationLetterListView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (alphaIndexer.get(s) != null) {
                    int position = alphaIndexer.get(s);
                    allCityListView.setSelection(position);
                }
                overlay.setText(s);
                overlay.setVisibility(View.VISIBLE);
                mHandler.removeCallbacks(overlayThread);
                // 延迟一秒后执行，让overlay为不可见
                mHandler.postDelayed(overlayThread, 1000);
            }
        });
    }

    /**
     * 初始化listView
     */
    private void initAllCityView() {
        allCityListView.setAdapter(new LocationListAdapter(this, allCity_lists, city_hot, city_history));
        allCityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void initCity() {
        City city = new City("定位", "0"); // 当前定位城市
        allCity_lists.add(city);
        city = new City("最近", "1"); // 最近访问的城市
        allCity_lists.add(city);
        city = new City("热门", "2"); // 热门城市
        allCity_lists.add(city);
        city = new City("全部", "3"); // 全部城市
        allCity_lists.add(city);
        ArrayList<City> city_lists = getCityList();// 数据库来的城市列表
        allCity_lists.addAll(city_lists);
        initHotCity();
        initHisCity();
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
        DatabaseHelper hisDbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = hisDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from recentcity order by date desc limit 0, 3", null);
        while (cursor.moveToNext()) {
            city_history.add(cursor.getString(1));
        }
        cursor.close();
        db.close();
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


    Intent intent = new Intent();
    /**
     * 用户点击返回，判断确认用户选择的城市
     */
    @Override
    public void finish() {
        if (city.equals("")) {
            if (BDMapUtil.city == null) { //定位于用户选择都为空，返回默认北京
                city = "北京";
                intent.putExtra("city", city);
                setResult(Constants.LOCATION_ACTIVITY_RESULT_CODE, intent);
                LocationActivity.super.finish();
            } else {  //选择空，定位有值，返回定位
                city = BDMapUtil.city;
                intent.putExtra("city", city);
                setResult(Constants.LOCATION_ACTIVITY_RESULT_CODE, intent);
                LocationActivity.super.finish();
            }
        } else {
            if (!(BDMapUtil.city == null)) {  //选择不为空　，定位不为空
                if (!city.equals(BDMapUtil.city)) {
                    new AlertDialog.Builder(this).setMessage("您当前选择的位置与定位不一致，是否继续？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    intent.putExtra("city", city);
                                    setResult(Constants.LOCATION_ACTIVITY_RESULT_CODE, intent);
                                    LocationActivity.super.finish();
                                }
                            }).setNegativeButton("取消", null).show();
                }else { //都不为空，但两者值一样
                    intent.putExtra("city", city);
                    setResult(Constants.LOCATION_ACTIVITY_RESULT_CODE, intent);
                    LocationActivity.super.finish();
                }
            }
        }
    }


}

/*
*
*  if (!(city.equals("")&&BDMapUtil.city.equals(""))){
            if (!city.equals(BDMapUtil.city)) {
                new AlertDialog.Builder(this).setMessage("您当前选择的位置与定位不一致，是否继续？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //把城市返回出去，并记录，选择的城市
                            }
                        }).setNegativeButton("取消", null).show();
            }
        }
*
* */
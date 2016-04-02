package com.hu131.houserental.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hu131.houserental.R;
import com.hu131.houserental.fragment.RequestRentalFragment;
import com.hu131.houserental.fragment.ShareRentalFragment;
import com.hu131.houserental.fragment.WholeRentalFragment;

/**
 * 整租，合租，求租列表activity
 * Created by Hu131 on 2016/3/19.
 */
public class ListActivity extends AppCompatActivity {

    private String title;
    private int id;
    private WholeRentalFragment wholeRentalFragment;
    private ShareRentalFragment shareRentalFragment;
    private RequestRentalFragment requestRentalFragment;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentDates();
        setContentView(R.layout.activity_list);
        initView();

    }

    /**
     * 初始化数据，数据有
     * title toolbar的标题
     * id 首页点击的view的id
     */
    private void getIntentDates() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        title = extras.getString("title");
        id = extras.getInt("id");
    }

    /**
     * 初始化view
     * 1，初始化标题栏
     * 2，各id对应的fragment
     */
    private void initView() {
        //设置标题栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (id) {
            case R.id.home_iv_whole:
                if (wholeRentalFragment == null) {
                    wholeRentalFragment = new WholeRentalFragment();
                    fragmentTransaction.add(R.id.frame_list_activity, wholeRentalFragment, "whole");
                } else {
                    fragmentTransaction.show(wholeRentalFragment);
                }
                break;
            case R.id.home_iv_share:
                if (shareRentalFragment == null) {
                    shareRentalFragment = new ShareRentalFragment();
                    fragmentTransaction.add(R.id.frame_list_activity, shareRentalFragment, "share");
                } else {
                    fragmentTransaction.show(shareRentalFragment);
                }
                break;
            case R.id.home_iv_request:
                if (requestRentalFragment == null) {
                    requestRentalFragment = new RequestRentalFragment();
                    fragmentTransaction.add(R.id.frame_list_activity, requestRentalFragment, "request");
                } else {
                    fragmentTransaction.show(requestRentalFragment);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_edit://信息发布
                Log.i("--main--", "信息发布");
                break;
            case R.id.menu_action_search://搜索
                Log.i("--main--", "索搜");
                break;
            case android.R.id.home://返回键
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

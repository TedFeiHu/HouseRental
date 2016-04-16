package com.hu131.houserental.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hu131.houserental.R;
import com.hu131.houserental.activity.ListActivity;
import com.hu131.houserental.activity.LocationActivity;
import com.hu131.houserental.utils.Constants;

/**
 * Created by Hu131 on 2016/2/24.
 * 主页fragment
 */
public class Home extends Fragment implements View.OnClickListener {



    private ImageView ivWhole;
    private ImageView ivShare;
    private ImageView ivRequest;
    private Intent intentToList;
    private Bundle extras;
    private Intent intentToLocation;
    private TextView toolbarLocation;
    private TextView tvVillage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        initDates();
        initView(view);
        initListener();
        return view;
    }

    private void initDates() {
        intentToList = new Intent(getActivity(), ListActivity.class);
        extras = new Bundle();
        intentToLocation = new Intent(getActivity(), LocationActivity.class);
    }


    /**
     * 初始化监听
     */
    private void initListener() {
        toolbarLocation.setOnClickListener(this);
        ivWhole.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivRequest.setOnClickListener(this);
        tvVillage.setOnClickListener(this);
    }

    /**
     * 初始化view
     *
     * @param view fragment_home
     */
    void initView(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layer1);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText("房屋租赁系统");
        toolbarLocation = (TextView) toolbar.findViewById(R.id.toolbar_tv_location);
        ivWhole = (ImageView) view.findViewById(R.id.home_iv_whole);
        ivShare = (ImageView) view.findViewById(R.id.home_iv_share);
        ivRequest = (ImageView) view.findViewById(R.id.home_iv_request);
        tvVillage = (TextView) view.findViewById(R.id.home_rental_village);

    }


    /**
     * 点击事件
     * @param v 点击的view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_iv_whole: //整租房按钮
                extras.putString("title", "整租房");
                extras.putInt("id", R.id.home_iv_whole);
                intentToList.putExtras(extras);
                startActivity(intentToList);
                break;
            case R.id.home_iv_share: //合租房按钮
                extras.putString("title", "合租房");
                extras.putInt("id", R.id.home_iv_share);
                intentToList.putExtras(extras);
                startActivity(intentToList);
                break;
            case R.id.home_iv_request: //求租房按钮
                extras.putString("title", "求租房");
                extras.putInt("id", R.id.home_iv_request);
                intentToList.putExtras(extras);
                startActivity(intentToList);
                break;
            case R.id.toolbar_tv_location: //toolbar的定位按钮
                startActivityForResult(intentToLocation, 0);
                break;
            case R.id.home_rental_village: //社区找房
                break;
        }
    }

    /**
     * 返回事件
     * @param requestCode 请求码
     * @param resultCode 返回码
     * @param data 数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.LOCATION_ACTIVITY_RESULT_CODE){
            String city = data.getStringExtra("city");
            toolbarLocation.setText(city);
        }
    }
}

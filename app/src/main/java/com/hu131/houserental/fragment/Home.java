package com.hu131.houserental.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hu131.houserental.R;
import com.hu131.houserental.activity.ListActivity;

/**
 * Created by Hu131 on 2016/2/24.
 * 主页fragment
 */
public class Home extends Fragment implements View.OnClickListener {

    private ImageView ivWhole;
    private ImageView ivShare;
    private ImageView ivRequest;
    private Intent intent;
    private Bundle extras;

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
        intent = new Intent(getActivity(), ListActivity.class);
        extras = new Bundle();
    }


    /**
     * 初始化监听
     */
    private void initListener() {
        ivWhole.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivRequest.setOnClickListener(this);
    }

    /**
     * 初始化view
     *
     * @param view fragment_home
     */
    void initView(View view) {
        ivWhole = (ImageView) view.findViewById(R.id.home_iv_whole);
        ivShare = (ImageView) view.findViewById(R.id.home_iv_share);
        ivRequest = (ImageView) view.findViewById(R.id.home_iv_request);
    }


    /**
     * 点击事件
     * @param v 点击的view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_iv_whole:
                extras.putString("title", "整租房");
                extras.putInt("id", R.id.home_iv_whole);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.home_iv_share:
                extras.putString("title", "合租房");
                extras.putInt("id", R.id.home_iv_share);
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.home_iv_request:
                extras.putString("title", "求租房");
                extras.putInt("id", R.id.home_iv_request);
                intent.putExtras(extras);
                startActivity(intent);
                break;
        }
    }
}

package com.hu131.houserental.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.hu131.houserental.R;
import com.hu131.houserental.fragment.RequestRentalFragment;
import com.hu131.houserental.fragment.ShareRentalFragment;
import com.hu131.houserental.fragment.WholeRentalFragment;

/**
 * 列表activity
 * Created by Hu131 on 2016/3/19.
 */
public class ListActivity extends Activity {

    private String title;
    private int id;
    private WholeRentalFragment wholeRentalFragment;
    private ShareRentalFragment shareRentalFragment;
    private RequestRentalFragment requestRentalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentDates();
        setContentView(R.layout.activity_list);
        initView();

    }

    private void getIntentDates() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        title = extras.getString("title");
        id = extras.getInt("id");
    }

    private void initView() {
        TextView tvTitle = (TextView) findViewById(R.id.list_activity_title_text);
        tvTitle.setText(title);
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
}

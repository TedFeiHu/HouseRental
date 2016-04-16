package com.hu131.houserental.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.hu131.houserental.R;
import com.hu131.houserental.fragment.Home;
import com.hu131.houserental.fragment.Search;
import com.hu131.houserental.fragment.User;

public class MainActivity extends AppCompatActivity {

    private Home homeFragment;
    private Search searchFragment;
    private User userFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        透明状态栏(沉浸式状态栏)
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_main);
        fragmentManager = getFragmentManager();

        checkable(radioGroup.getCheckedRadioButtonId());//先显示默认的
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checkable(checkedId);
            }
        });
    }

    /**
     * 根据被选中的radioButton决定要显示的fragment
     * 先把原来已经显示的隐藏起来
     * 如果是第一次显示则创建新的对象，否则直接显示（show）
     *
     * @param checkedId radioButton的ID
     */
    private void checkable(int checkedId) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (checkedId) {
            case R.id.home:
                hide();
                if (homeFragment == null) {
                    homeFragment = new Home();
                    fragmentTransaction.add(R.id.frame_main, homeFragment, "home");
                } else {
                    fragmentTransaction.show(homeFragment);
                }
                break;
            case R.id.search:
                hide();
                if (searchFragment == null) {
                    searchFragment = new Search();
                    fragmentTransaction.add(R.id.frame_main, searchFragment, "search");
                } else {
                    fragmentTransaction.show(searchFragment);
                }
                break;
            case R.id.user:
                hide();
                if (userFragment == null) {
                    userFragment = new User();
                    fragmentTransaction.add(R.id.frame_main, userFragment, "user");
                } else {
                    fragmentTransaction.show(userFragment);
                }
                break;
        }

        fragmentTransaction.commit();
    }

    /**
     * 把之前显示的FRAGMENT隐藏起来
     */
    private void hide() {
        if (homeFragment != null) {
            fragmentTransaction.hide(homeFragment);
        }

        if (searchFragment != null) {
            fragmentTransaction.hide(searchFragment);
        }

        if (userFragment != null) {
            fragmentTransaction.hide(userFragment);
        }
    }
}
package com.hu131.houserental.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.hu131.houserental.R;
import com.hu131.houserental.adpater.SearchAdapter;
import com.hu131.houserental.bean.HouseInfo;
import com.hu131.houserental.bean.HttpInfo;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Created by Hu131 on 2016/2/24.
 * 查找fragment
 */
public class Search extends Fragment {
    List<HouseInfo> houseInfoList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        ListView lv = (ListView) view.findViewById(R.id.listview_search);
        getData();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final SearchAdapter adapter = new SearchAdapter(this.getActivity(), houseInfoList, requestQueue);
        lv.setAdapter(adapter);
        return view;
    }

    void getData() {
        try {
            InputStream open = getActivity().getAssets().open("rh.json");
            String json = IOUtils.toString(open, "utf-8"); //输入流转换为字符串
            if (!TextUtils.isEmpty(json)) {
                Gson gson = new Gson();
                HttpInfo httpInfo = gson.fromJson(json, HttpInfo.class);
//                Log.i("-----",httpInfo.getResult().get(1).getImages().get(0));
                houseInfoList = httpInfo.getResult();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

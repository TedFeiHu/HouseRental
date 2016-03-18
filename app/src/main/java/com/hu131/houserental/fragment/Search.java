package com.hu131.houserental.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.hu131.houserental.R;
import com.hu131.houserental.adpater.SearchAdapter;

/**
 * Created by Hu131 on 2016/2/24.
 * 查找fragment
 */
public class Search extends Fragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        ListView lv = (ListView) view.findViewById(R.id.listview_search);
        SearchAdapter adapter = new SearchAdapter(this.getActivity());
        lv.setAdapter(adapter);
        return view;
    }


}

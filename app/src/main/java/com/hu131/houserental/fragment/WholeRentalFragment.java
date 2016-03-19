package com.hu131.houserental.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hu131.houserental.R;

/**
 * Created by Hu131 on 2016/3/19.
 */
public class WholeRentalFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rental_whole, null);
        return view;
    }
}

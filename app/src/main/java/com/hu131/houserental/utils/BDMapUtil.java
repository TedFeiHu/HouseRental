package com.hu131.houserental.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 地图相关的工具类
 * Created by Administrator on 2016/4/12.
 */
public class BDMapUtil {

    public static String city = null;
    public static int errorCode;
    public static LocationClient mLocationClient;
    //    private static Handler HANDLER;
    private static MyLocationListener myLocationListener = new MyLocationListener();
    public static int LOCATE_PROCESS; // 记录当前定位的状态 1正在定位-2定位成功-3定位失败

    /**
     * 定位城市
     */
    public static void location(Context context) {
        mLocationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        //option.setLocationMode(LocationMode.NORMAL);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);

        mLocationClient.registerLocationListener(myLocationListener);
        mLocationClient.start();
        LOCATE_PROCESS = 1;
    }

    public static void locationStop() {
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(myLocationListener);

    }

    /**
     * 实现实位回调监听
     */
    static class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation != null) {
                city = bdLocation.getCity();
                errorCode = bdLocation.getLocType();
                if (city.equals("")) {
                    LOCATE_PROCESS = 3;
                } else {
                    LOCATE_PROCESS = 2;
                }
            }
        }
    }
}

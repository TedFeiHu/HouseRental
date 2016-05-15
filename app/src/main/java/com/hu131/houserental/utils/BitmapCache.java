package com.hu131.houserental.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * 图片缓存
 * Created by Hu131 on 2016/3/18.
 */
public class BitmapCache implements ImageLoader.ImageCache{

    private LruCache<String,Bitmap> cacheMap;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public BitmapCache(){
        int maxMemory = (int)Runtime.getRuntime().maxMemory() / 1024;
        int cacheSize = maxMemory/10;
        cacheMap = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }


    @Override
    public Bitmap getBitmap(String url) {
        return cacheMap.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        cacheMap.put(url,bitmap);
    }
}

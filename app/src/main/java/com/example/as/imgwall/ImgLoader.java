package com.example.as.imgwall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by as on 2016/8/1.
 */
public class ImgLoader {
    private static LruCache<String,Bitmap> MemoryCache;
    private static ImgLoader loader;
    private int wid;
    private ImgLoader(){
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory/8;
        MemoryCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    public void setWid(int wid){
        this.wid = wid;
    }
    public int getWid() {
        return wid;
    }
    public static  ImgLoader getInstance(){
        if(loader == null){
            synchronized (ImgLoader.class){
                if(loader == null){
                    loader = new ImgLoader();
                }
            }
        }
        return loader;
    }
    public void putImg(String key,Bitmap bitmap){
        if(getImg(key) == null){
            MemoryCache.put(key,bitmap);
        }
    }
    public Bitmap getImg(String key) {
        if (key != null) {
            return MemoryCache.get(key);
        }
        return null;
    }
    public int getSampleSize(BitmapFactory.Options options){
        int width = options.outWidth;
        int ratio = 1;
        if(width > wid){
            ratio = Math.round((float)width/(float)wid);
        }
        return ratio;
    }

}

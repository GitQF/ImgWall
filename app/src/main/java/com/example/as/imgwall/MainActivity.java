package com.example.as.imgwall;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class MainActivity extends AppCompatActivity {
    private LinearLayout[] lines = new LinearLayout[3];
    private ScrollView scroll;
    private int currentImg = 0;
    boolean hasMeasured = false;
    private int hei;
    private int wid;
    private int count = 0;
    private int[] h = new int[3];
    private ImgLoader loader;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            init();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addpic();

        new downPageTask(handler).execute("https://www.zhihu.com/topic/19624174/hot");
    }

    private void init() {
        loader = ImgLoader.getInstance();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        hei = metrics.heightPixels;
        wid = metrics.widthPixels;
        lines[0] = (LinearLayout) findViewById(R.id.l1);
        lines[1] = (LinearLayout) findViewById(R.id.l2);
        lines[2] = (LinearLayout) findViewById(R.id.l3);
        ViewTreeObserver vto = lines[1].getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (hasMeasured == false) {
                    int width = lines[1].getMeasuredWidth();
                    ImgLoader.getInstance().setWid(width);
                    hasMeasured = true;
                }
                return true;
            }
        });

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentImg < Img.girls.size()) {
                    new LoadImgTask(lines[j]).execute(Img.girls.get(currentImg++));
                }
            }
        }
        scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    checkVisible();
                    if (scroll.getScrollY() + hei > lines[0].getHeight() - 20) {
                        lines[0].setLayoutParams(new LinearLayout.LayoutParams(wid / 3, lines[0].getHeight() + 500));
                    }
                    add();
                }
                return false;
            }
        });
    }

    public void checkVisible() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lines[i].getChildCount(); j++) {
                ImageView view = (ImageView) (lines[i].getChildAt(j));
                if (view.getY() + view.getHeight() < scroll.getScrollY()
                        || view.getY() > scroll.getScrollY() + hei) {
                    //Bitmap bitmap = ((BitmapDrawable) (view.getDrawable())).getBitmap();
                    view.setImageBitmap(null);
                    count++;
                    if(count >=5){
                        System.gc();
                        count = 0;
                    }
                    lines[i].removeView(view);
                    //if (bitmap != null && !bitmap.isRecycled()) {
                        //bitmap.recycle();
                    //}
                } else {
                    if (((BitmapDrawable) view.getDrawable()).getBitmap() == null) {
                        try {
                            Bitmap bm = loader.getImg((String) view.getTag());
                            if (bm == null) {
                                new LoadImgTask(view).execute((String) view.getTag());
                            } else {
                                try {
                                    view.setImageBitmap(bm);
                                }catch (Exception e){

                                }
                            /*Log.d("ggggggggggggggg","fffffffffffff"+bm.getByteCount());
                            Log.d("ggggggggggggggg","fffffffffffff"+bm.isRecycled());*/
                            }
                        } catch (OutOfMemoryError e) {
                            view.setImageBitmap(null);
                            return;
                        }

                    }
                }
            }
        }
    }

    private int getMinHeight() {
        h[0] = 0;
        h[1] = 0;
        h[2] = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < lines[i].getChildCount(); j++) {
                h[i] += lines[i].getChildAt(j).getHeight();
            }
        }
        int min;
        if (h[0] < h[1] && h[0] < h[2]) {
            min = 0;
        } else if (h[1] < h[0] && h[1] < h[2]) {
            min = 1;
        } else {
            min = 2;
        }
        Log.d("add at line", "" + min);
        return min;
    }

    private void add() {
        int l = getMinHeight();
        for (int i = 0; i < 3; i++) {
            int c = 0;
            while (h[(l + i) % 3] + 20 < scroll.getScrollY() + hei) {
                if(c++ > 3)break;
                if (currentImg < Img.girls.size()) {
                    new LoadImgTask(lines[(l + i) % 3]).execute(Img.girls.get(currentImg++));
                    h[(l + i) % 3] += 500;
                }
            }
        }
    }

    private void addpic() {
        for (int i = 0; i < Img.imgs.length; i++) {
            Img.girls.add(Img.imgs[i]);
        }
    }
}

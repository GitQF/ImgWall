package com.example.as.imgwall;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by as on 2016/8/1.
 */
public class LoadImgTask extends AsyncTask<String,Void,Bitmap> {
    private int w, h;
    private LinearLayout layout;
    private String url;
    private ImageView view = null;
    private ImgLoader loader = ImgLoader.getInstance();
    public LoadImgTask(LinearLayout l){
        this.layout = l;
    }
    public LoadImgTask(ImageView view){
        this.view = view;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap img = loader.getImg(strings[0]);
        for(int i = 0;i<1;i++) {
            if (img == null) {
                img = downLoadImg(strings[0]);
            }else {
                break;
            }
        }
        return img;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(view == null){
            addImg(bitmap);
        }else{
            view.setImageBitmap(bitmap);
        }
        destory();
        bitmap = null;
        super.onPostExecute(bitmap);
    }
    private Bitmap downLoadImg(String url){
        HttpURLConnection connection;
        Bitmap img = null;
        this.url = url;
        try{
            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(connection.getInputStream(),null,options);
            int temp = loader.getSampleSize(options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = temp;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            connection.disconnect();
            connection = (HttpURLConnection) u.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(2000);
            connection.connect();
            img = BitmapFactory.decodeStream(connection.getInputStream(),null,options);
            w = img.getWidth();
            h = img.getHeight();
            //Log.d("压缩比例",""+temp);
            //Log.d("压缩后wid",""+w);
            //Log.d("压缩后hei",""+h);
            loader.putImg(url,img);
            connection.disconnect();
        }catch (Exception e){
            e.printStackTrace();
        }catch (OutOfMemoryError e){
            return null;
        }
        finally {
            if(img != null) {
                //loader.putImg(url,img);
                return img;
            }
            return null;
        }
    }
    private void addImg(Bitmap bitmap){
        if(bitmap != null) {
            ImageView view = new ImageView(layout.getContext());
            view.setTag(url);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(loader.getWid(), bitmap.getHeight() * loader.getWid() / bitmap.getWidth());
            view.setLayoutParams(params);
            view.setPadding(3, 3, 3, 3);
            layout.addView(view);
            view.setImageBitmap(bitmap);
        }
    }
    private void destory(){
        this.layout = null;
        this.view = null;
    }
}

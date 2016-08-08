package com.example.as.imgwall;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by as on 2016/8/2.
 */
public class downPageTask extends AsyncTask<String,Void,String> {

    private Handler handler;
    public downPageTask(Handler handler) {
        super();
        this.handler = handler;
    }

    @Override
    protected String doInBackground(String... strings) {
        String s = getWebpage(strings[0]);
        return s;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        handler.sendMessage(Message.obtain());
    }

    private String getWebpage(String url){
        URLConnection connection;
        String res = null;
        BufferedReader br;
        try{
            URL u = new URL(url);
            connection = u.openConnection();
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
            StringBuffer sb = new StringBuffer();
            String s;
            while((s = br.readLine())!= null){
                matchImg(s);
            }
            br.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.d("gggggggggggggggg","gggggggggggggggg");
        }finally {

            return res;
        }
    }
    public static void matchImg(String data){
        Pattern p = Pattern.compile("https:.{40,70}[^bm]\\.((jpg)|(png))");
        Matcher m = p.matcher(data);
        while(m.find()){
            //Log.d("gggggggggggggggg",m.group());
            Img.girls.add(m.group());
        }
    }
    public static void matchPage(String data){
        Pattern p = Pattern.compile("http://www\\..{3,15}\\.com");
        Matcher m = p.matcher(data);
        while(m.find()){
            Log.d("gggggggggggggggg",m.group());
            if(!Img.girls.contains(m.group())) {
                Img.girls.add(m.group());
            }
        }
    }
}

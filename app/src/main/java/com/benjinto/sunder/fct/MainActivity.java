package com.benjinto.sunder.fct;

import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;

import okhttp3.*;

public class MainActivity extends AppCompatActivity implements Presenter.FromMain{
    private File fileContent;
    private Presenter presenter;
    private JSONObject objectContent = null;
    private OkHttpClient client = null;
    private long fiveMin = 5*60*1000;
    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Handler mainHandler = new Handler(Looper.getMainLooper());
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {

            if(doNeedUpdate()){
                if(presenter.alertDialog.isShowing()){
                    doUpdate();
                    presenter.dismisPopup();
                }else{
                    presenter.showPopup(0);
                }
            }else{
                if(presenter.alertDialog.isShowing()){
                    presenter.dismisPopup();
                }
            }
            timerHandler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
        presenter = new Presenter(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        findViewById(R.id.main_activity).post(timerRunnable);

    }
    private boolean doNeedUpdate(){
        if(fileContent == null || fileNeedsReload() || objectContent == null)return true;
        return false;
    }
    public void doUpdate(){
        if(fileContent == null){
            fileContent = new File(getFilesDir(),"file_out.txt");
            if(!fileContent.exists()){
                try {
                    fileContent.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (fileNeedsReload()) {

            objectContent = requestHttpResponse();
            writeAllImages(objectContent);
            writeFile(objectContent);
            presenter.updateJsonObject(objectContent);

        }
        if(objectContent == null){

            objectContent = readFile();
            writeAllImages(objectContent);
            presenter.updateJsonObject(objectContent);
        }
    }
    public void writeAllImages(JSONObject jsonObject){
        JSONArray jsonArticles = null;
        try {
            jsonArticles = jsonObject.getJSONArray("articles");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int i,j=0;
        byte[] imageInByte = null;

        for(i=0;i<jsonArticles.length();i++){
            imageInByte = getImageStream(jsonArticles.optJSONObject(i).optString("urlToImage"));
            writeImage(imageInByte,i);
        }
    }
    public void writeFile(JSONObject jsonObject){
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(fileContent,false);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public JSONObject readFile(){
        FileReader fileReader;
        JSONObject jsonObject = null;
        CharBuffer charBuffer = CharBuffer.allocate(100000);
        charBuffer.rewind();
        try{
            fileReader = new FileReader(fileContent);
            fileReader.read(charBuffer);
            charBuffer.rewind();
            jsonObject = new JSONObject(charBuffer.toString());
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        charBuffer.clear();
        return jsonObject;
    }
    public void writeImage(byte[] image, int id){
        File file = new File(getFilesDir(),"image_"+id);
        FileOutputStream fileOutputStream = null;
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            fileOutputStream = new FileOutputStream(file,false);
            fileOutputStream.write(image);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] readImage(int id){
        File file = new File(getFilesDir(),"image_"+id);
        FileInputStream fileInputStream = null;
        byte[] outStream = new byte[100000];
        try{
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(outStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outStream;
    }
    public boolean fileNeedsReload(){
        if(fileContent == null) return true;
        if(fileContent.lastModified()==0){

            return true;
        }
        long time = System.currentTimeMillis() - fileContent.lastModified();
        if(time > fiveMin){
            return true;
        }
        if(fileContent.length()==0L) return true;

        return false;
    }
    public byte[] getImageStream(String url){

        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        byte[] response = null;

        try {
            Response r = call.execute();
            response = r.body().bytes();
            if(!r.isSuccessful()) presenter.showPopup(1);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return response;
    }
    public JSONObject requestHttpResponse(){
        String url = "https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=6946d0c07a1c4555a4186bfcade76398";
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        String response = null;

        try {
            Response r = call.execute();
            response = r.body().string();
            if(!r.isSuccessful()) presenter.showPopup(1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}

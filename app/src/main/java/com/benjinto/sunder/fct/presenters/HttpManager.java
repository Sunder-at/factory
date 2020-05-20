package com.benjinto.sunder.fct.presenters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.benjinto.sunder.fct.views.ErrorAlertDialog;
import com.benjinto.sunder.fct.misc.Note;
import com.benjinto.sunder.fct.misc.NoteDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpManager {
    private final static String API_KEY = "6946d0c07a1c4555a4186bfcade76398";
    private HttpUrl URL;
    private OkHttpClient client;
    private NoteDao mNoteDao;
    private ContentPresenter mContentPresenter = null;
    private ListPresenter mListPresenter = null;
    private TimerManager mTimerManager;
    private NoteListListener mNoteListListener;

    public HttpManager(NoteDao noteDao, NoteListListener listener){
        mNoteDao = noteDao;
        mNoteListListener = listener;
        URL = new HttpUrl.Builder().scheme("https").host("newsapi.org")
                .addPathSegment("v1").addPathSegment("articles")
                .addQueryParameter("source","bbc-news")
                .addQueryParameter("sortBy","top")
                .addQueryParameter("apiKey",API_KEY)
                .build();
        client = new OkHttpClient.Builder().build();
    }

    /**
     * Adds {@link Runnable} to the main looper that starts {@link #requestHttpResponse(long)}.
     * When it catches any Exception it shows {@link ErrorAlertDialog}
     */
    public boolean requestCall(){
        try {
            long time = new Date().getTime();
            requestHttpResponse(time);
            notifyDatasetChange();
            mTimerManager.updateDate(time);
        } catch (Exception e) {
            showError();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Calls whichever activity is active to display error dialog
     */
    private void showError(){
        if(mListPresenter != null) mListPresenter.onHttpError();
        if(mContentPresenter != null) mContentPresenter.onHttpError();
    }
    /**
     * Fetches data via {@link Response} in form of {@link JSONObject}, finds any image links and
     * fetches them. Then deletes all in {@link NoteDao} and repopulates it with new data.
     *
     * @throws Exception Can be caused by {@link Call#execute()}, {@link JSONException} or invalid status
     */
    private void requestHttpResponse(long time) throws Exception {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        String responseString = response.body().string();
        response.close();
        JSONObject jsonObject = new JSONObject(responseString);
        if (!jsonObject.getString("status").matches("ok"))
            throw new Exception("Invalid body content");
        JSONArray articles = jsonObject.getJSONArray("articles");
        mNoteDao.deleteAll();
        List<byte[]> bufferList = new ArrayList<>();
        for (int i=0;  i < articles.length(); i++) {
            byte[] is = getImageStream(((JSONObject)articles.opt(i)).getString("urlToImage"));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeByteArray(is,0,is.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG,40,os);
            bufferList.add(os.toByteArray());
        }
        for (int i=0;  i < articles.length(); i++) {
            mNoteDao.insert(new Note(
                    ((JSONObject)articles.get(i)).getString("author"),
                    ((JSONObject)articles.get(i)).getString("title"),
                    ((JSONObject)articles.get(i)).getString("description"),
                    ((JSONObject)articles.get(i)).getString("url"),
                    ((JSONObject)articles.get(i)).getString("urlToImage"),
                    ((JSONObject)articles.get(i)).getString("publishedAt"),
                    bufferList.get(i),
                    time
            ));
        }
    }

    /**
     * Notifies change in the database
     */
    private void notifyDatasetChange(){
        mNoteListListener.update();
        if(mContentPresenter != null) mContentPresenter.updateDataset();
        if(mListPresenter != null) mListPresenter.updateDataset();
    }

    /**
     * Fetches a single image
     *
     * @param url URL that points to an image
     * @return Image in byte[] format
     * @throws IOException Caused by {@link Call#execute()}
     */
    byte[] getImageStream(String url) throws Exception{
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        byte[] response;
        Response r = call.execute();
        response = r.body().bytes();
        r.close();
        return response;
    }

    public void registerListener(ContentPresenter contentPresenter) {
        mContentPresenter = contentPresenter;
    }
    public void registerListener(ListPresenter listPresenter) {
        mListPresenter = listPresenter;
    }
    public void registerListener(TimerManager timerManager) {
        mTimerManager = timerManager;
    }
    public void unregisterListener(ContentPresenter contentPresenter) {
        mContentPresenter = null;
    }
    public void unregisterListener(ListPresenter listPresenter) {
        mListPresenter = null;
    }
    public void unregisterListener(TimerManager timerManager) {
        mTimerManager = null;
    }

    public interface NoteListListener{
        void update();
    }
}

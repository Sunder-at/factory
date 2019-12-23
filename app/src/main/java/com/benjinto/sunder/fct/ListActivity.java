package com.benjinto.sunder.fct;

import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.URLUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListActivity extends AppCompatActivity implements ListPresenter.ListView{
    private final static String DATA_DATE_FILENAME = "dataCreatedTime";
    private final static long DATA_REFRESH_TIMER = 5*60*1000;
    private final static String API_KEY = "6946d0c07a1c4555a4186bfcade76398";
    private static String URL = "https://newsapi.org/v1/articles?source=bbc-news&sortBy=top&apiKey=" + API_KEY;
    /**
     * Represents time in {@link long} milliseconds when was data last time fetched.
     * Refresh occurs if data is older than {@value #DATA_REFRESH_TIMER} milliseconds
     */
    private long dataCreated;
    private ListPresenter listPresenter;
    private AlertDialog alertDialog = null;
    private Handler mHandler;
    private File cacheTime;
    private OkHttpClient client;
    private ListedItemsAdapter listedItemsAdapter;

    /**
     * Repeating thread that checks if {@link #dataCreated} is older than {@value #DATA_REFRESH_TIMER}
     * milliseconds.
     * Calls {@link }
     */
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) return;
            long timeNow = new Date().getTime();
            if(!isFinishing() &&
                    (timeNow-dataCreated) > DATA_REFRESH_TIMER){
                ((SwipeRefreshLayout)findViewById(R.id.main_activity)).setRefreshing(true);
                listPresenter.requestDataDownload();
            }
            mHandler.postDelayed(this, DATA_REFRESH_TIMER);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Map<String,String> map = null;
        map.put("source","bbc-news");
        map.put("sortBy","top");
        map.put("apiKey",API_KEY);
        URL = parseUrl("https://newsapi.org/v1/articles",map);
        mHandler = new Handler(Looper.getMainLooper());
        listPresenter = new ListPresenter(this);
        client = new OkHttpClient.Builder().build();
        viewsSetup();
        cacheTimeSetup();
    }

    private String parseUrl(String url, Map<String, String> map) {
        url += '?';
        for(String key:map.keySet()){
            String value = map.get(key);
            url += key + '=' + value;
            if(map.keySet().toArray()[map.size()-1] == key) continue;
            url += '&';
        }
        return url;
    }

    @Override
    protected void onResume() {
        super.onResume();
        readTimeOnResume();
        findViewById(R.id.main_activity).post(timerRunnable);
    }
    @Override
    protected void onPause() {
        super.onPause();
        writeTimeOnPause();
    }

    /**
     * Sets up Views, Adapters, Layouts
     * Called only by {@link #onCreate}
     */
    private void viewsSetup(){
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.main_activity);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            listPresenter.requestDataDownload();
        });
        listedItemsAdapter = new ListedItemsAdapter(listPresenter);
        RecyclerView listItemsView = findViewById(R.id.listed_items_view);
        listItemsView.setHasFixedSize(true);
        listItemsView.setLayoutManager(new LinearLayoutManager(ListActivity.this));
        listItemsView.setAdapter(listedItemsAdapter);
        listedItemsAdapter.notifyDataSetChanged();
        getSupportActionBar().setTitle("Factory");
    }

    /**
     * Sets up timer and checks existence
     * Called only by {@link #onCreate}
     */
    private void cacheTimeSetup() {
        cacheTime = new File(getCacheDir(),DATA_DATE_FILENAME);
        if(!cacheTime.exists()) {
            try {
                cacheTime.createNewFile();
                dataCreated = new Date().getTime();
                FileWriter fileWriter = new FileWriter(cacheTime);
                fileWriter.write(String.valueOf(dataCreated));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts {@link ContentActivity} and passes {@link Note} for display.
     * Used by {@link ListedItemsAdapter#onBindViewHolder(ListedItemsAdapter.ListedItemsViewHolder, int)}
     *
     * @param note Its data is packed in {@link Intent} and used by {@link ContentActivity}
     */
    @Override
    public void startContentActivity(Note note){
        Intent intent = new Intent(this,ContentActivity.class);
        intent.putExtra("NOTE", note);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public NoteDao getNoteDao() {
        return  ((App) getApplication()).getDaoSession().getNoteDao();
    }

    @Override
    public void notifyDatasetChanged() {
        listedItemsAdapter.notifyDataSetChanged();
    }

    /**
     * Adds {@link Runnable} to the main looper that starts {@link #requestHttpResponse()}.
     * When it catches any Exception it calls {@link ListPresenter#showErrorAlertDialog()}.
     * Stops {@link SwipeRefreshLayout} from refreshing
     */
    @Override
    public void requestCall(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    requestHttpResponse();
                    dataCreated = new Date().getTime();
                    listPresenter.doLocalDataUpdate();
                } catch (Exception e) {
                    listPresenter.showErrorAlertDialog();
                    e.printStackTrace();
                }
                ((SwipeRefreshLayout)findViewById(R.id.main_activity)).setRefreshing(false);
            }
        });
    }

    /**
     * Shows {@link AlertDialog} with error messaging
     */
    @Override
    public void alertError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setCancelable(false).setTitle("Greška").setMessage("Ups, došlo je do pogreške");
        builder.setPositiveButton("U REDU", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listPresenter.dismissErrorAlertDialog();
            }
        });
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(alertDialog != null)
                    alertDialog.dismiss();
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    /**
     * Dismisses {@link AlertDialog} created by {@link #alertError()}.
     */
    @Override
    public void dismissAlertError() {
        alertDialog.dismiss();
    }

    /**
     * Fetches data via {@link Response} in form of {@link JSONObject}, finds any image links and
     * fetches them. Then deletes all in {@link NoteDao} and repopulates it with new data.
     *
     * @throws Exception Can be caused by {@link Call#execute()}, {@link JSONException} or invalid status
     */
    public void requestHttpResponse() throws Exception {
        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        String response;
        Response r = call.execute();
        response = r.body().string();
        r.close();

        JSONObject jsonObject;
        jsonObject = new JSONObject(response);

        if (!jsonObject.optString("status").matches("ok"))
            throw new Exception("Invalid body content");

        JSONArray articles = jsonObject.optJSONArray("articles");
        NoteDao noteDao = getNoteDao();
        noteDao.deleteAll();
        for (int i=0;  i < articles.length(); i++) {
            byte[] imageBytes = getImageStream(((JSONObject)articles.opt(i)).optString("urlToImage"));

            noteDao.insert(new Note(
                    ((JSONObject)articles.opt(i)).optString("author"),
                    ((JSONObject)articles.opt(i)).optString("title"),
                    ((JSONObject)articles.opt(i)).optString("description"),
                    ((JSONObject)articles.opt(i)).optString("URL"),
                    ((JSONObject)articles.opt(i)).optString("urlToImage"),
                    ((JSONObject)articles.opt(i)).optString("publishedAt"),
                    imageBytes
            ));
        }
    }
    /**
     * Called by {@link #requestHttpResponse()} to fetch a single image provided by URL and return
     *
     * @param url URL that points to an image
     * @return Image in byte[] format
     * @throws IOException Caused by {@link Call#execute()}
     */
    private byte[] getImageStream(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        byte[] response = null;
        Response r = call.execute();
        response = r.body().bytes();
        r.close();
        return response;
    }
    /**
     * Called only by {@link #onResume()} to read from {@link #cacheTime} and update {@link #dataCreated}
     */
    private void readTimeOnResume(){
        try {
            FileReader fileReader = new FileReader(cacheTime);
            CharBuffer stream = CharBuffer.allocate(20);
            int count = fileReader.read(stream);
            fileReader.close();
            stream.rewind();
            String string = stream.subSequence(0,count).toString();
            dataCreated = Long.valueOf(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Called only by {@link #onPause()} to write {@link #dataCreated} to {@link #cacheTime}
     */
    private void writeTimeOnPause(){
        try {
            FileWriter fileWriter = new FileWriter(cacheTime);
            fileWriter.write(String.valueOf(dataCreated));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

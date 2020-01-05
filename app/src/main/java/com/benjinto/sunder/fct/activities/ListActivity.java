package com.benjinto.sunder.fct.activities;

import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.benjinto.sunder.fct.App;
import com.benjinto.sunder.fct.adapters.ListedItemsAdapter;
import com.benjinto.sunder.fct.Note;
import com.benjinto.sunder.fct.NoteDao;
import com.benjinto.sunder.fct.R;
import com.benjinto.sunder.fct.presenters.ListPresenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListActivity extends AppCompatActivity implements ListPresenter.ListView{
    private final static String DATA_DATE_FILENAME = "dataCreatedTime";
    private final static long DATA_REFRESH_TIMER = 5*60*1000;
    private final static long TIMER = 60*1000;
    private final static String API_KEY = "6946d0c07a1c4555a4186bfcade76398";
    /**
     * Represents time in {@link long} milliseconds the moment data was last updated.
     */
    private long dataCreated;
    /**
     * If {@link #onPause()} is called, this tells if app is going into the background or
     * {@link ContentActivity} has been started
     */
    private boolean isInBackground = true;
    private HttpUrl URL;
    private ListPresenter listPresenter;
    private AlertDialog alertDialog;
    private HandlerThread handlerThread;
    private Handler mHandler;
    private Handler mainHandler;
    private File cacheTime;
    private OkHttpClient client;
    private ListedItemsAdapter listedItemsAdapter;

    /**
     * Repeating thread that checks if {@link #dataCreated} is older than {@value #DATA_REFRESH_TIMER}
     * milliseconds. Called every {@value #TIMER} ms
     * Calls {@link }
     */
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                long timeNow = new Date().getTime();
                if (!isFinishing() &&
                        (timeNow - dataCreated) > DATA_REFRESH_TIMER) {
                    listPresenter.setRefreshing(true);
                    listPresenter.requestDataDownload();
                }
            }
            mHandler.postDelayed(this, TIMER);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        URL = new HttpUrl.Builder().scheme("https").host("newsapi.org")
                .addPathSegment("v1").addPathSegment("articles")
                .addQueryParameter("source","bbc-news")
                .addQueryParameter("sortBy","top")
                .addQueryParameter("apiKey",API_KEY)
                .build();
        handlerThread = new HandlerThread("loop");
        handlerThread.start();

        mHandler = new Handler(handlerThread.getLooper());
        mainHandler = new Handler(getMainLooper());
        listPresenter = new ListPresenter(this);
        client = new OkHttpClient.Builder().build();
        viewsSetup();
        setupDataCreateFile();
        loadDataCreated();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(isInBackground) {
            mHandler.post(timerRunnable);
        }else{
            isInBackground = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isInBackground) {
            mHandler.removeCallbacks(timerRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        writeDataCreated();
        destroyHandlerThread();

    }
    /**
     * Cleans up {@link #handlerThread}, called only by {@link #onDestroy()}
     */
    private void destroyHandlerThread(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(handlerThread != null){
                    handlerThread.quit();
                    handlerThread.interrupt();
                }
            }
        });
    }
    /**
     * Sets up date of data created file and checks existence.
     * Called only by {@link #onCreate}
     */
    private void setupDataCreateFile() {
        cacheTime = new File(getFilesDir(),DATA_DATE_FILENAME);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
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
        });
    }
    /**
     * Called only by {@link #onCreate(Bundle)} to read from {@link #cacheTime} file and update {@link #dataCreated}
     */
    private void loadDataCreated(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
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
        });
    }
    /**
     * Called only by {@link #onDestroy()} to write {@link #dataCreated} to {@link #cacheTime} file
     */
    private void writeDataCreated(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    FileWriter fileWriter = new FileWriter(cacheTime);
                    fileWriter.write(String.valueOf(dataCreated));
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * Sets up Views, Adapters, Layouts, AlertDialogs
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

        AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
        builder.setCancelable(false).setTitle("Greška").setMessage("Ups, došlo je do pogreške");
        builder.setPositiveButton("U REDU", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listPresenter.dismissErrorAlertDialog();
            }
        });
        alertDialog = builder.create();
        getSupportActionBar().setTitle("Factory");
    }
    /**
     * Calls {@link SwipeRefreshLayout#setRefreshing(boolean)}.
     * @param bool true to set refresh animation, false to cancel
     */
    @Override
    public void setRefreshing(boolean bool){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                ((SwipeRefreshLayout)findViewById(R.id.main_activity)).setRefreshing(bool);
            }
        });
    }
    /**
     * Starts {@link ContentActivity} and passes {@link Note} for display.
     * Used by {@link ListedItemsAdapter#onBindViewHolder(ListedItemsAdapter.ListedItemsViewHolder, int)}
     *
     * @param note Its data is packed in {@link Intent} and used by {@link ContentActivity}
     */
    @Override
    public void startContentActivity(Note note){
        isInBackground = false;
        Intent intent = new Intent(this,ContentActivity.class);
        intent.putExtra("NOTE", note);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    /**
     * DAO for the table
     * @return DAO for table Note
     */
    @Override
    public NoteDao getNoteDao() {
        return  ((App) getApplication()).getDaoSession().getNoteDao();
    }

    @Override
    public void notifyDatasetChanged() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                listedItemsAdapter.notifyDataSetChanged();
            }
        });
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
                listPresenter.setRefreshing(false);
            }
        });
    }

    /**
     * Shows {@link AlertDialog} with error messaging
     */
    @Override
    public void alertError(){
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if(!alertDialog.isShowing()) alertDialog.show();
            }
        });
    }

    /**
     * Dismisses {@link AlertDialog} created by {@link #alertError()}.
     */
    @Override
    public void dismissAlertError() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        });
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
        if (!jsonObject.getString("status").matches("ok"))
            throw new Exception("Invalid body content");
        JSONArray articles = jsonObject.getJSONArray("articles");
        NoteDao noteDao = getNoteDao();
        noteDao.deleteAll();
        List<byte[]> bufferList = new ArrayList<>();
        for (int i=0;  i < articles.length(); i++) {
            byte[] is = getImageStream(((JSONObject)articles.opt(i)).getString("urlToImage"));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeByteArray(is,0,is.length);
            bitmap.compress(Bitmap.CompressFormat.JPEG,40,os);
            bufferList.add(os.toByteArray());
        }
        for (int i=0;  i < articles.length(); i++) {
            noteDao.insert(new Note(
                    ((JSONObject)articles.get(i)).getString("author"),
                    ((JSONObject)articles.get(i)).getString("title"),
                    ((JSONObject)articles.get(i)).getString("description"),
                    ((JSONObject)articles.get(i)).getString("url"),
                    ((JSONObject)articles.get(i)).getString("urlToImage"),
                    ((JSONObject)articles.get(i)).getString("publishedAt"),
                    bufferList.get(i)
            ));
        }
    }
    /**
     * Called by {@link #requestHttpResponse()} to fetch a single image provided by URL
     *
     * @param url URL that points to an image
     * @return Image in byte[] format
     * @throws IOException Caused by {@link Call#execute()}
     */
    private byte[] getImageStream(String url) throws Exception{
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        byte[] response;
        Response r = call.execute();
        response = r.body().bytes();
        r.close();
        return response;
    }

}

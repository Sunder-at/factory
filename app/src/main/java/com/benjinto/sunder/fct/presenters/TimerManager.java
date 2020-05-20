package com.benjinto.sunder.fct.presenters;

import android.os.Handler;
import android.os.HandlerThread;

import com.benjinto.sunder.fct.misc.NoteDao;

import java.util.Date;

public class TimerManager {
    private final static long TIMER = 5*60*1000;
    private long mDataCreated = 0;
    private Handler handler;
    private HandlerThread handlerThread;
    private HttpManager mHttpManager;
    private NoteDao mNoteDao;
    private Runnable timerRunnable = this::runner;

    public TimerManager(NoteDao noteDao) {
        mNoteDao = noteDao;
        handlerThread = new HandlerThread("loop");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    /**
     * Updates timerRunnables refresh cycle
     * @param dataCreated time when database was created
     */
    public void updateDate(long dataCreated){
        mDataCreated = dataCreated;
        handler.removeCallbacks(timerRunnable);
        handler.postDelayed(timerRunnable,100);
    }

    /**
     * Updates timerRunnables refresh cycle
     */
    public void updateDate(){
        if(mNoteDao.count() != 0){
            mDataCreated = mNoteDao.queryBuilder().orderAsc(NoteDao.Properties.Id).list().get(0).getTimeCreated();
        }else{
            mDataCreated = 0;
        }
        System.err.printf("\n---updateDate---%1$d---\n",mDataCreated);
        handler.removeCallbacks(timerRunnable);
        handler.postDelayed(timerRunnable,100);
    }

    private void runner(){
        long timeDiff = new Date().getTime() - mDataCreated;
        if (timeDiff > TIMER) {
            if(!mHttpManager.requestCall()){
                handler.postDelayed(timerRunnable, TIMER);
                System.err.println("---requestCall = false----");
            }
        }else{
            System.err.println("----else handler.postDelayed");
            handler.postDelayed(timerRunnable, TIMER - timeDiff + 100);
        }
    }

    public void registerListener(HttpManager httpManager){
        mHttpManager = httpManager;
    }


    /**
     * Called by main activitys onDestroy, performs cleanup
     */
    public void onDestroy() {
        handler.removeCallbacks(timerRunnable);
        handlerThread.quit();
        handlerThread.interrupt();
    }
}

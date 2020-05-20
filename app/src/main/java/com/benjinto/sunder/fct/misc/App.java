package com.benjinto.sunder.fct.misc;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.benjinto.sunder.fct.presenters.HttpManager;
import com.benjinto.sunder.fct.presenters.TimerManager;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;


public class App extends Application {

    private DaoSession daoSession;
    private HttpManager httpManager;
    private TimerManager timerManager;
    private List<Note> noteList;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.OpenHelper helper = new NoteOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();
        noteList = new ArrayList<>();
        daoSession = new DaoMaster(db).newSession();
        httpManager =  new HttpManager(daoSession.getNoteDao(), this::updateNoteList);
        timerManager = new TimerManager(daoSession.getNoteDao());
        httpManager.registerListener(timerManager);
        timerManager.registerListener(httpManager);
        updateNoteList();
    }

    public List<Note> getNoteList(){
        return noteList;
    }

    public HttpManager getHttpManager() {
        return httpManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public void updateNoteList(){
        noteList = daoSession.getNoteDao().queryBuilder().orderAsc(NoteDao.Properties.Id).list();
    }

    private static class NoteOpenHelper extends DaoMaster.OpenHelper{

        public NoteOpenHelper(Context context, String name) {
            super(context, name);

        }

        public NoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

    }

}

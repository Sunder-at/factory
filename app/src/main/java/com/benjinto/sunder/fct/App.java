package com.benjinto.sunder.fct;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;

import java.util.Date;


public class App extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.OpenHelper helper = new NoteOpenHelper(this, "notes-db");
        Database db = helper.getWritableDb();

        daoSession = new DaoMaster(db).newSession();

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private class NoteOpenHelper extends DaoMaster.OpenHelper{

        public NoteOpenHelper(Context context, String name) {
            super(context, name);

        }

        public NoteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

    }
}

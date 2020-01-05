package com.benjinto.sunder.fct.presenters;

import com.benjinto.sunder.fct.Note;
import com.benjinto.sunder.fct.NoteDao;

import java.util.List;


public class ListPresenter{
    private ListView mListView;
    private NoteDao noteDao;
    private List<Note> noteList;

    public ListPresenter(ListView listView){
        mListView = listView;
        noteDao = mListView.getNoteDao();
        noteList = getNoteList();
    }

    public interface ListView{
        void requestCall();
        void alertError();
        void dismissAlertError();
        void startContentActivity(Note note);
        NoteDao getNoteDao();
        void notifyDatasetChanged();
        void setRefreshing(boolean bool);
    }

    /**
     * Starts or cancels loading animation provided by {@link android.support.v4.widget.SwipeRefreshLayout}.
     * @param bool true to start loading, false to stop loading.
     */
    public void setRefreshing(boolean bool) {
        mListView.setRefreshing(bool);
    }

    /**
     * Returns all elements in order from {@link #noteDao} in form of {@link List}.
     * @return Executed query returned as a list of all elements
     */
    private List<Note> getNoteList() {
        return noteDao.queryBuilder().orderAsc(NoteDao.Properties.Id).build().list();
    }

    /**
     * Returns amount of elements in {@link #noteList}.
     * @return Amount of elements in {@link #noteList}
     */
    public int getDatasetSize() {
        return noteList.size();
    }

    /**
     * Returns element from {@link #noteList}.
     * @param i index of element to return
     * @return item from {@link #noteList}
     */
    public Note getNote(int i) {
        return noteList.get(i);
    }

    /**
     * Starts {@link com.benjinto.sunder.fct.activities.ContentActivity} activity.
     * @param note Database item from {@link #noteDao}
     */
    public void startContentActivity(Note note) {
        mListView.startContentActivity(note);
    }

    /**
     * Dismiss Error {@link android.app.AlertDialog}.
     */
    public void dismissErrorAlertDialog() {
        mListView.dismissAlertError();
    }

    /**
     * Shows Error {@link android.app.AlertDialog}.
     */
    public void showErrorAlertDialog() {
        mListView.alertError();
    }

    /**
     * Starts download process.
     */
    public void requestDataDownload() {
        mListView.requestCall();
    }

    /**
     * Updates {@link #noteDao} and {@link com.benjinto.sunder.fct.adapters.ListedItemsAdapter}.
     */
    public void doLocalDataUpdate() {
        noteList = getNoteList();
        mListView.notifyDatasetChanged();
    }


}

package com.benjinto.sunder.fct;

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

    }
    private List<Note> getNoteList() {
        return noteDao.queryBuilder().orderAsc(NoteDao.Properties.Id).build().list();
    }

    public int getDatasetSize() {
        return noteList.size();
    }

    public Note getNote(int i) {
        return noteList.get(i);
    }

    public void startContentActivity(Note note) {
        mListView.startContentActivity(note);
    }

    public void dismissErrorAlertDialog() {
        mListView.dismissAlertError();
    }

    public void showErrorAlertDialog() {
        mListView.alertError();
    }

    public void requestDataDownload() {
        mListView.requestCall();
    }

    public void doLocalDataUpdate() {
        noteList = getNoteList();
        mListView.notifyDatasetChanged();
    }


}

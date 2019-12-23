package com.benjinto.sunder.fct;


public class ContentPresenter{
    private ContentView mContentView;

    public ContentPresenter(ContentView contentView){
        mContentView = contentView;
    }

    public interface ContentView{
        void finish();
        void setNote(Note note);
    }

    public void endActivity() {
        mContentView.finish();
    }

    public void setContent(Note note){
        mContentView.setNote(note);
    }

}

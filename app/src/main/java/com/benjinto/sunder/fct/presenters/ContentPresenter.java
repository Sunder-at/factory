package com.benjinto.sunder.fct.presenters;

import com.benjinto.sunder.fct.views.ContentView;


public class ContentPresenter{
    private ContentView mContentView;

    public ContentPresenter(ContentView contentView){
        mContentView = contentView;
    }

    public void updateDataset(){
        mContentView.updateDataset();
    }

    public void onHttpError() {
        mContentView.showHttpError();
    }
}

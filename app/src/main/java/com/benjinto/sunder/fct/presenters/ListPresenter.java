package com.benjinto.sunder.fct.presenters;

import com.benjinto.sunder.fct.views.ListView;


public class ListPresenter{
    private ListView mListView;

    public ListPresenter(ListView listView){
        mListView = listView;
    }

    public void updateDataset() {
        mListView.updateDataset();
    }

    public void onClick(int i) {
        mListView.startContentActivity(i);
    }

    public void onHttpError() {
        mListView.showHttpError();
    }
}

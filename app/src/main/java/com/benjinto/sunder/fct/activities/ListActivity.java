package com.benjinto.sunder.fct.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.benjinto.sunder.fct.misc.App;
import com.benjinto.sunder.fct.views.ErrorAlertDialog;
import com.benjinto.sunder.fct.adapters.ListedItemsAdapter;
import com.benjinto.sunder.fct.misc.Note;
import com.benjinto.sunder.fct.R;
import com.benjinto.sunder.fct.presenters.ListPresenter;
import com.benjinto.sunder.fct.views.ListView;


public class ListActivity extends AppCompatActivity implements ListView {
    private ListPresenter listPresenter;
    private ErrorAlertDialog errorAlertDialog;
    private ListedItemsAdapter listedItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listPresenter = new ListPresenter(this);
        setContentView(R.layout.list_activity);
        listedItemsAdapter = new ListedItemsAdapter(
                () -> ((App)getApplication()).getNoteList(),
                i -> listPresenter.onClick(i));
        RecyclerView listItemsView = findViewById(R.id.listed_items_view);
        listItemsView.setHasFixedSize(true);
        listItemsView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        listItemsView.setAdapter(listedItemsAdapter);
        getSupportActionBar().setTitle("Factory News");
        runOnUiThread(() -> errorAlertDialog = new ErrorAlertDialog(ListActivity.this));
        runOnUiThread(() -> ((App)getApplication()).getTimerManager().updateDate());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App)getApplication()).getHttpManager().registerListener(listPresenter);
        updateDataset();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((App)getApplication()).getHttpManager().unregisterListener(listPresenter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((App)getApplication()).getTimerManager().onDestroy();
    }

    @Override
    public void updateDataset() {
        runOnUiThread(() -> listedItemsAdapter.notifyDataSetChanged());
    }

    @Override
    public void showHttpError() {
        runOnUiThread(() -> errorAlertDialog.show());
    }
    /**
     * Starts {@link ContentActivity} and passes {@link Note} for display.
     * Used by {@link ListedItemsAdapter#onBindViewHolder(ListedItemsAdapter.ListedItemsViewHolder, int)}
     *
     * @param noteID Item on list that was clicked on
     */
    @Override
    public void startContentActivity(int noteID){
        Intent intent = new Intent(this,ContentActivity.class);
        intent.putExtra("NOTE", noteID);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}

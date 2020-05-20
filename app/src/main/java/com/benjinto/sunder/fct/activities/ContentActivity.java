package com.benjinto.sunder.fct.activities;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.benjinto.sunder.fct.misc.App;
import com.benjinto.sunder.fct.views.ErrorAlertDialog;
import com.benjinto.sunder.fct.misc.Note;
import com.benjinto.sunder.fct.R;
import com.benjinto.sunder.fct.adapters.ContentPagerAdapter;
import com.benjinto.sunder.fct.presenters.ContentPresenter;
import com.benjinto.sunder.fct.views.ContentView;

import java.util.ArrayList;
import java.util.List;


public class ContentActivity extends AppCompatActivity implements ContentView {

    private ContentPresenter contentPresenter;
    private ViewPager viewPager;
    private ContentPagerAdapter contentPagerAdapter;
    private ErrorAlertDialog errorAlertDialog;
    private List<String> titleList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity);
        contentPresenter =  new ContentPresenter(this);
        titleList = new ArrayList<>();
        int currentItem = getIntent().getIntExtra("NOTE",0);
        viewPager = findViewById(R.id.content_view_pager);
        contentPagerAdapter = new ContentPagerAdapter(
                getSupportFragmentManager(), () -> ((App)getApplication()).getNoteList());
        viewPager.setAdapter(contentPagerAdapter);
        viewPager.setCurrentItem(currentItem);
        getSupportActionBar().setTitle(((App) getApplication()).getNoteList().get(currentItem).getTitle());
        viewPager.addOnPageChangeListener(new PageListener(this));
        runOnUiThread(() -> errorAlertDialog = new ErrorAlertDialog(ContentActivity.this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((App)getApplication()).getHttpManager().registerListener(contentPresenter);
        updateDataset();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((App)getApplication()).getHttpManager().unregisterListener(contentPresenter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void updateDataset(){
        contentPagerAdapter.notifyDataSetChanged();
        titleList.clear();
        for (Note note :((App)getApplication()).getNoteList()) {
            titleList.add(note.getTitle());
        }
    }

    @Override
    public void showHttpError() {
        runOnUiThread(() -> errorAlertDialog.show());
    }

    private class PageListener implements ViewPager.OnPageChangeListener{
        AppCompatActivity mActivity;
        public PageListener(AppCompatActivity activity) {
            mActivity = activity;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mActivity.getSupportActionBar().setTitle(titleList.get(position));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}

package com.benjinto.sunder.fct.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.benjinto.sunder.fct.Note;
import com.benjinto.sunder.fct.R;
import com.benjinto.sunder.fct.presenters.ContentPresenter;
import com.bumptech.glide.Glide;


public class ContentActivity extends AppCompatActivity implements ContentPresenter.ContentView{

    private ContentPresenter contentPresenter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_activity);
        contentPresenter =  new ContentPresenter(this);
        Intent intent = getIntent();
        Note note = (Note)intent.getSerializableExtra("NOTE");
        contentPresenter.setContent(note);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                contentPresenter.endActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set {@link R.id#text_content_title}
     * @param title String it is set to
     */
    @Override
    public void setTitle(String title){
        ((TextView)findViewById(R.id.text_content_title)).setText(title);
    }
    /**
     * Set {@link R.id#text_content_body}
     * @param description String it is set to
     */
    public void setDescription(String description){
        ((TextView)findViewById(R.id.text_content_body)).setText(description);
    }
    /**
     * Set {@link R.id#image_content_view}
     * @param image Image it is set to
     */
    public void setImage(byte[] image){
        Glide.with(this).load(image).into((ImageView)findViewById(R.id.image_content_view));
    }
    /**
     * Set {@link R.layout#content_activity}'s {@link android.support.v7.app.ActionBar}
     * @param title String its set to
     */
    public void setSupportActionBar(String title){
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        contentPresenter.endActivity();
    }

}

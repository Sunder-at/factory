package com.benjinto.sunder.fct;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

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

    /**
     * Fills {@link R.layout#content_activity}'s elements using {@link Note}
     * @param note Used for its data
     */
    public void setNote(Note note){
        ((TextView)findViewById(R.id.text_content_title)).setText(note.getTitle());
        ((TextView)findViewById(R.id.text_content_body)).setText(note.getDescription());
        Glide.with(this).load(note.getImage()).into((ImageView)findViewById(R.id.image_content_view));
        getSupportActionBar().setTitle(note.getTitle());
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        contentPresenter.endActivity();
    }

}

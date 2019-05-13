package com.benjinto.sunder.fct;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;


public class ContentView extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.content_view);
        ((TextView)findViewById(R.id.text_content_title)).setText(intent.getStringExtra("title"));
        ((TextView)findViewById(R.id.text_content_body)).setText(intent.getStringExtra("body"));
        //Drawable drawable = BitmapDrawable.createFromPath(filePath).;
        //Drawable.createFromStream(new ByteArrayInputStream(intent.getByteArrayExtra("image")),"");
        String filePath = getFilesDir().getAbsolutePath()+"/image_"+intent.getIntExtra("id",0);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath,opt);

        ((ImageView)findViewById(R.id.image_content_view)).setImageBitmap(bitmap);
        //System.err.print(intent.getByteArrayExtra("image"));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}

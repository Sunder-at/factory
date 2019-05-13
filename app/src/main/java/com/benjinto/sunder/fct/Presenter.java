package com.benjinto.sunder.fct;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class Presenter implements ItemInList.OnFragmentInteractionListener{
    private Context baseContext;
    private JSONObject jsonObject;
    private JSONArray contentJson;
    private ContentView contentView;
    private FromMain mFromMain;
    public AlertDialog alertDialog = null;
    private AlertDialog.Builder builder = null;
    ArrayList<ItemInList> itemArray;
    View.OnTouchListener touchListener;
    private OkHttpClient client = new OkHttpClient();

    public Presenter(Context context){
        //View view = mFromMain.getLayoutInflater().inflate(R.layout.popup_layout,(ViewGroup)mFromMain.findViewById(R.id.extra_layout),false);
        builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        alertDialog = builder.create();
        jsonObject = null;
        contentJson = null;
        baseContext = context;
        itemArray = new ArrayList<>();
        itemArray.clear();
        contentView = new ContentView();
        if(context instanceof FromMain){
            mFromMain = (FromMain) context;
        }

    }
    public void showPopup(int code){
        if(alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        if(code == 0) {
            builder.setMessage("Učitavanje");
        }
        if(code == 1){
            builder.setMessage("Greška u učitavanju");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.dismiss();
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.show();
    }
    public void dismisPopup(){
        alertDialog.dismiss();
    }
//only main calls this, passes data to be presented
    public void updateJsonObject(JSONObject jsonObject) {
        if(jsonObject == null)return;
        int i;
        ItemInList item;
        try {
            contentJson = jsonObject.getJSONArray("articles");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentManager fm = mFromMain.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 4;
        Bitmap bitmap = null;
        for(i=0;i < itemArray.size();i++){
            ft.remove(itemArray.get(i));
        }
        itemArray.clear();
        for(i=0;i < contentJson.length();i++){
            String filePath = mFromMain.getFilesDir().getAbsolutePath()+"/image_"+i;
            item = ItemInList.newInstance(i,this);
            bitmap = BitmapFactory.decodeFile(filePath,opt);
            //item.setDrawable(Drawable.createFromStream(new ByteArrayInputStream(mFromMain.readImage(i)),""));
            item.setBitmap(bitmap);
            item.setText(contentJson.optJSONObject(i).optString("title"));
            itemArray.add(item);
            ft.add(R.id.list_layout,item);
        }
        ft.commit();
    }

    public interface FromMain{
        File getFilesDir ();
        FragmentManager getSupportFragmentManager();
        Context getApplicationContext();
        Context getBaseContext();
        void startActivity (Intent intent);
        <T extends android.view.View> T findViewById(@IdRes int id);
        byte[] readImage(int i);
        LayoutInflater getLayoutInflater();
    }
    @Override
    public View.OnTouchListener getListener() {
        return touchListener;
    }

    @Override
    public void ItemTouchEvent(int id) {

        Intent intent = new Intent(mFromMain.getBaseContext(),contentView.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.ACTION_VIEW);
        intent.putExtra("title",contentJson.optJSONObject(id).optString("title"));
        intent.putExtra("body",contentJson.optJSONObject(id).optString("description"));
        intent.putExtra("id",id);

        mFromMain.startActivity(intent);

    }

}

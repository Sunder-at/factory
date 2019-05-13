package com.benjinto.sunder.fct;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.InputStream;

/**
 * A fragment with a Google +1 button.
 * Activities that contain this fragment must implement the
 * {@link ItemInList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemInList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemInList extends Fragment {
    public Drawable drawableImage;
    public Bitmap bitmapImage;
    private int number;
    private CharSequence textTitle;
    private OnFragmentInteractionListener mListener;
    private int thisId;
    private InputStream imageInByte;

    public ItemInList() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemInList newInstance(int i, Presenter presenter) {
        ItemInList fragment = new ItemInList();
        fragment.setNumber(i);
        fragment.setFragmentInteractionListener(presenter);
        return fragment;
    }

    private void setFragmentInteractionListener(Presenter presenter) {
        if (presenter instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) presenter;
        } else {
            throw new RuntimeException(presenter.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_single).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    mListener.ItemTouchEvent(number);
                }
                return true;
            }
        });
        ((TextView)view.findViewById(R.id.text_single_title)).setText(textTitle);
        ((ImageView)view.findViewById(R.id.image_single)).setImageBitmap(bitmapImage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_content_view,container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void setDrawable(Drawable drawable){
        this.drawableImage = drawable;
    }
    public void setText(CharSequence charSequence){
        this.textTitle = charSequence;
    }
    public void setNumber(int number){
        this.number = number;
    }
    public int getNumber(){
        return number;
    }
    public int getThisId(){
        return thisId;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmapImage = bitmap;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        View.OnTouchListener getListener();
        void ItemTouchEvent(int id);
    }

}

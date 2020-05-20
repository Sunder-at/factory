package com.benjinto.sunder.fct.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benjinto.sunder.fct.R;
import com.benjinto.sunder.fct.misc.Note;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

public class ContentScreenSlideFragment extends Fragment {

    private final static String ARG_NOTE = "NOTE";
    private Note note;
    public ContentScreenSlideFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        note = (Note)getArguments().getSerializable(ARG_NOTE);
        View view = inflater.inflate(R.layout.content_item, container, false);
        ((TextView)view.findViewById(R.id.text_content_title)).setText(note.getTitle());
        ((TextView)view.findViewById(R.id.text_content_body)).setText(note.getDescription());
        Glide.with(view).load(note.getImage()).override(Target.SIZE_ORIGINAL).
                into((ImageView)view.findViewById(R.id.image_content_view));
        return view;
    }

}

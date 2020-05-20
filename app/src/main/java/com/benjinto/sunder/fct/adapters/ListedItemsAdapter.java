package com.benjinto.sunder.fct.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benjinto.sunder.fct.misc.Note;
import com.benjinto.sunder.fct.R;
import com.bumptech.glide.Glide;

import java.util.List;


public class ListedItemsAdapter extends
        RecyclerView.Adapter<ListedItemsAdapter.ListedItemsViewHolder> {
    private Listener mListener;
    private GetList mGetList;
    public ListedItemsAdapter(GetList getList, Listener listener) {
        mGetList = getList;
        mListener = listener;
    }

    public static class ListedItemsViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public ImageView image;
        public ListedItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_single_title);
            image = itemView.findViewById(R.id.image_single);
        }
    }

    @NonNull
    @Override
    public ListedItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);

        return new ListedItemsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListedItemsViewHolder holder, int i) {
        Note note = mGetList.getList().get(i);
        holder.title.setText(note.getTitle());
        Glide.with(holder.itemView).load(note.getImage()).centerCrop().into(holder.image);
        holder.itemView.setOnClickListener(view -> {
           mListener.onClick(i);
        });
    }

    @Override
    public int getItemCount() {
        return mGetList.getList().size();
    }
    public interface Listener{
        void onClick(int i);
    }
    public interface GetList{
        List<Note> getList();
    }
}
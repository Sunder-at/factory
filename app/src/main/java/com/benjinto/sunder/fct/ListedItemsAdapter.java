package com.benjinto.sunder.fct;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class ListedItemsAdapter extends
        RecyclerView.Adapter<ListedItemsAdapter.ListedItemsViewHolder> {
    private ListPresenter listPresenter;

    public ListedItemsAdapter(ListPresenter listPresenter) {
        this.listPresenter = listPresenter;
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
                .inflate(R.layout.item_note, viewGroup, false);

        return new ListedItemsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListedItemsViewHolder holder, int i) {
        Note note = listPresenter.getNote(i);
        holder.title.setText(note.getTitle());
        Glide.with(holder.itemView).load(note.getImage()).centerCrop().into(holder.image);
        holder.itemView.setOnClickListener(view -> {
           listPresenter.startContentActivity(note);
        });
    }

    @Override
    public int getItemCount() {
        return listPresenter.getDatasetSize();
    }
}
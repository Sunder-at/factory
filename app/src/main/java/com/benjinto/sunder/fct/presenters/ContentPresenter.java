package com.benjinto.sunder.fct.presenters;


import com.benjinto.sunder.fct.Note;

public class ContentPresenter{
    private ContentView mContentView;

    public ContentPresenter(ContentView contentView){
        mContentView = contentView;
    }

    public interface ContentView{
        void finish();
        void setTitle(String title);
        void setDescription(String description);
        void setImage(byte[] image);
        void setSupportActionBar(String title);
    }

    /**
     * Ends this {@link com.benjinto.sunder.fct.activities.ContentActivity} instance
     */
    public void endActivity() {
        mContentView.finish();
    }

    /**
     * Sets all elements from content_activity layout to appropriate {@link Note}'s elements
     * @param note element passed
     */
    public void setContent(Note note){
        mContentView.setTitle(note.getTitle());
        mContentView.setDescription(note.getDescription());
        mContentView.setImage(note.getImage());
        mContentView.setSupportActionBar(note.getTitle());
    }

}

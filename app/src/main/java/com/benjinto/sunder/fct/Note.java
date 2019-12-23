package com.benjinto.sunder.fct;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Entity mapped to table "NOTE".
 */
@Entity
public class Note implements Serializable {

    static final long serialVersionUID = 1L;
    @Id
    private Long id;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String published;
    private byte[] image;

    public Note(String author, String title, String description,
                String url, String urlToImage, String published, byte[] image) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.published = published;
        this.image = image;
    }

    @Generated(hash = 1636246781)
    public Note(Long id, String author, String title, String description,
            String url, String urlToImage, String published, byte[] image) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.urlToImage = urlToImage;
        this.published = published;
        this.image = image;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return this.urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublished() {
        return this.published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }


}

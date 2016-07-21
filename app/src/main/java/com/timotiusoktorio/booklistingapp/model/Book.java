package com.timotiusoktorio.booklistingapp.model;

import java.util.Arrays;

/**
 * Created by Timotius on 2016-07-20.
 */

public class Book {

    private String mId;
    private String mTitle;
    private String[] mAuthors;
    private String mThumbnail;

    public Book(String id, String title, String[] authors, String thumbnail) {
        mId = id;
        mTitle = title;
        mAuthors = authors;
        mThumbnail = thumbnail;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String[] getAuthors() {
        return mAuthors;
    }

    public void setAuthors(String[] authors) {
        mAuthors = authors;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        mThumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Book{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mAuthors=" + Arrays.toString(mAuthors) +
                ", mThumbnail='" + mThumbnail + '\'' +
                '}';
    }

}
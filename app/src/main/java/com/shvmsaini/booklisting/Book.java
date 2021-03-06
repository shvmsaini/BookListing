package com.shvmsaini.booklisting;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.net.URL;

public class Book {
    String mTitle;
    String mAuthor;
    String mPrice;
    Bitmap imageBitmap;
    URL mSelfLink;
    Drawable mimageDrawable;

    /* For Book Details */
    String Description;
    String buyLink;
    String publishingDate;
    String pageCount;
    String pdfLink;

    public Book(String title, String author, String price, Bitmap imageBitmap,URL selfLink) {
        mTitle = title;
        mAuthor = author;
        mPrice = price;
        this.imageBitmap = imageBitmap;
        mSelfLink = selfLink;
    }
    public Book(String title, String author, String price, Drawable imageDrawable, URL selfLink) {
        mTitle = title;
        mAuthor = author;
        mPrice = price;
        mimageDrawable = imageDrawable;
        mSelfLink = selfLink;
    }

    public Book(String description, String buyLink, String publishingDate, String pageCount, String pdfLink) {
        Description = description;
        this.buyLink = buyLink;
        this.publishingDate = publishingDate;
        this.pageCount = pageCount;
        this.pdfLink = pdfLink;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmPrice() {
        return mPrice;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public URL getmSelfLink() {
        return mSelfLink;
    }

    public String getmDescription() {
        return Description;
    }

    public String getmBuyLink() {
        return buyLink;
    }

    public String getmPublishingDate() {
        return publishingDate;
    }

    public String getmPageCount() {
        return pageCount;
    }

    public String getmPdfLink() {
        return pdfLink;
    }

    public Drawable getMimageDrawable() {
        return mimageDrawable;
    }
}

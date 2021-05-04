package com.shvmsaini.booklisting;

import android.graphics.Bitmap;

public class Book {
    String mTitle;
    String mAuthor;
    String mPrice;
    Bitmap imageBitmap;

    public Book(String title, String author, String price, Bitmap imageBitmap) {
        mTitle = title;
        mAuthor = author;
        mPrice = price;
        this.imageBitmap = imageBitmap;

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
}

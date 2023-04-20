package com.example.bookquery;

import android.graphics.Bitmap;

public interface BookInfo {

    public String getTitle();

    public void setTitle(String title);

    public String getThumbUrl();

    public void setThumbUrl(String thumbUrl);

    public Bitmap getThumbBitmap();

    public void setThumbBitmap(Bitmap thumbBitmap);
}

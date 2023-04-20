package com.example.bookquery.bookInfo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.bookquery.BookInfo;

//Stores full info about a book
public class FullBookInfo implements BookInfo, Parcelable {
    String title, description, volumeId, publisher, publishedDate, language;
    double avgRating;
    String[] authors, categories;
    String thumbUrl;
    Bitmap thumbBitmap = null;
    int pageCount, ratingsCount;

    public FullBookInfo(String title, double avgRating, String description, String volumeId, String publisher, String publishedDate, String language, String[] authors, String[] categories, int pageCount, int ratingsCount, String thumbUrl) {
        this.title = title;
        this.avgRating = avgRating;
        this.description = description;
        this.volumeId = volumeId;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.language = language;
        this.authors = authors;
        this.categories = categories;
        this.pageCount = pageCount;
        this.ratingsCount = ratingsCount;
        this.thumbUrl = thumbUrl;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getThumbUrl() {
        return thumbUrl;
    }

    @Override
    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @Override
    public Bitmap getThumbBitmap() {
        return thumbBitmap;
    }

    @Override
    public void setThumbBitmap(Bitmap thumbBitmap) {
        this.thumbBitmap = thumbBitmap;
    }


    //Parcel method starts from here
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(volumeId);
        dest.writeString(publisher);
        dest.writeString(publishedDate);
        dest.writeString(language);
        dest.writeDouble(avgRating);
        dest.writeStringArray(authors);
        dest.writeStringArray(categories);
        dest.writeString(thumbUrl);
        dest.writeParcelable(thumbBitmap, flags);
        dest.writeInt(pageCount);
        dest.writeInt(ratingsCount);
    }

    protected FullBookInfo(Parcel in) {
        title = in.readString();
        description = in.readString();
        volumeId = in.readString();
        publisher = in.readString();
        publishedDate = in.readString();
        language = in.readString();
        avgRating = in.readDouble();
        authors = in.createStringArray();
        categories = in.createStringArray();
        thumbUrl = in.readString();
        thumbBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        pageCount = in.readInt();
        ratingsCount = in.readInt();
    }

    public static final Creator<FullBookInfo> CREATOR = new Creator<FullBookInfo>() {
        @Override
        public FullBookInfo createFromParcel(Parcel in) {
            return new FullBookInfo(in);
        }

        @Override
        public FullBookInfo[] newArray(int size) {
            return new FullBookInfo[size];
        }
    };
}

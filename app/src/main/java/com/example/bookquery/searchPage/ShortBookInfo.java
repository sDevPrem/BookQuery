package com.example.bookquery.searchPage;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.bookquery.BookInfo;


//Store info of books that will be displayed in search Result
//that is title, average rating, description, authors name, small Thumbnail url, and also the bitmap of small Thumbnail
public class ShortBookInfo implements Parcelable, BookInfo {
    String title, avgRating, description, volumeId;
    String[] authors;
    String smallThumbUrl;
    Bitmap smallThumbBitmap = null;

    public ShortBookInfo(String title, String avgRating, String description, String[] authors, String smallThumbUrl) {
        this.title = title;
        this.avgRating = avgRating;
        this.description = description;
        this.authors = authors;
        this.smallThumbUrl = smallThumbUrl;
    }

    public ShortBookInfo(String title, String avgRating, String description, String volumeId, String[] authors, String smallThumbUrl) {
        this.title = title;
        this.avgRating = avgRating;
        this.description = description;
        this.volumeId = volumeId;
        this.authors = authors;
        this.smallThumbUrl = smallThumbUrl;
    }

    public String getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(String volumeId) {
        this.volumeId = volumeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbUrl() {
        return smallThumbUrl;
    }

    public void setThumbUrl(String smallThumbUrl) {
        this.smallThumbUrl = smallThumbUrl;
    }

    public Bitmap getThumbBitmap() {
        return smallThumbBitmap;
    }

    public void setThumbBitmap(Bitmap smallThumbBitmap) {
        this.smallThumbBitmap = smallThumbBitmap;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    //parcel method starts

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(avgRating);
        dest.writeString(description);
        dest.writeStringArray(authors);
        dest.writeString(smallThumbUrl);
        dest.writeParcelable(smallThumbBitmap, flags);
    }

    protected ShortBookInfo(Parcel in) {
        title = in.readString();
        avgRating = in.readString();
        description = in.readString();
        authors = in.createStringArray();
        smallThumbUrl = in.readString();
        smallThumbBitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Creator<ShortBookInfo> CREATOR = new Creator<ShortBookInfo>() {
        @Override
        public ShortBookInfo createFromParcel(Parcel in) {
            return new ShortBookInfo(in);
        }

        @Override
        public ShortBookInfo[] newArray(int size) {
            return new ShortBookInfo[size];
        }
    };
}

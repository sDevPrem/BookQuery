package com.example.bookquery.bookInfo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.bookquery.QueryUtils;

public class BookInfoLoader extends AsyncTaskLoader<FullBookInfo> {
    String url;
    FullBookInfo mFullBookInfo;

    public BookInfoLoader(@NonNull Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mFullBookInfo == null)
            forceLoad();
    }

    @Nullable
    @Override
    public FullBookInfo loadInBackground() {
        if (!QueryUtils.isConnected(getContext()) || url == null || url.length() < 1)
            return null;
        else {
            mFullBookInfo = QueryUtils.retrieveFullBookInfo(url);
            return mFullBookInfo;
        }
    }
}

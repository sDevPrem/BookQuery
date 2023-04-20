package com.example.bookquery.searchPage;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.bookquery.QueryUtils;

import java.util.ArrayList;

public class SearchBookLoader extends AsyncTaskLoader<SearchResult> {

    boolean isSearchedByTheUser;
    SearchResult mSearchResult;
    String query;
    public boolean isRunning;

    public SearchBookLoader(boolean isSearchedByTheUser, @NonNull Context context, SearchResult result, String query) {
        super(context);
        this.query = query;
        this.isSearchedByTheUser = isSearchedByTheUser;
        this.mSearchResult = result;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        //if the user has searched then start the loading
        //else do nothing
        if (isSearchedByTheUser) {
            isSearchedByTheUser = false;
            isRunning = true;
            forceLoad();
        }
    }

    @Nullable
    @Override
    public SearchResult loadInBackground() {

        boolean isConnected = QueryUtils.isConnected(getContext());
        SearchResult currentResult;
        String url = makeUrl();

        //if the device is not connected to the internet or url is not valid
        //then create a searchResult of empty List
        //else search for the result and return it
        if (!isConnected || url == null || url.length() < 1) {
            currentResult = new SearchResult(new ArrayList<>(), isConnected, 0);
        } else {
            currentResult = QueryUtils.searchBooks(url);
        }

        currentResult.setQuery(query);
        if (mSearchResult == null) {
            mSearchResult = currentResult;
        } else mSearchResult.addSearchResult(currentResult);
        return mSearchResult;
    }

    private String makeUrl() {
        int startIndex = 0;
        //if searchResult is not null
        //make a url that searches for the books after the end of the previous result
        if (mSearchResult != null)
            startIndex += mSearchResult.getBooks().size();
        return QueryUtils.makeSearchURL(query, startIndex);
    }

    @Override
    public void deliverResult(@Nullable SearchResult data) {
        super.deliverResult(data);
        isRunning = false;
    }

}

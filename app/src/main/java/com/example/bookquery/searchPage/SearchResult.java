package com.example.bookquery.searchPage;

import java.util.List;

//This contains the result of the search and connection status of the internet while searching
//and the https response code
public class SearchResult {
    List<ShortBookInfo> mShortBookList;
    boolean isConnected;
    int responseCode;
    String query = "";
    SearchResult latestResult;

    public SearchResult(List<ShortBookInfo> shortBookList, boolean isConnected, int responseCode) {
        mShortBookList = shortBookList;
        this.isConnected = isConnected;
        this.responseCode = responseCode;
        latestResult = this;
    }

    public List<ShortBookInfo> getBooks() {
        return mShortBookList;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void addSearchResult(SearchResult result) {
        latestResult = result;
        mShortBookList.addAll(result.mShortBookList);
        isConnected = result.isConnected();
        responseCode = result.responseCode;
        query = result.query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void clearSearchResult() {
        mShortBookList.clear();
        query = "";
        isConnected = false;
    }

    public SearchResult getLatestResult() {
        return latestResult;
    }
}

package com.example.bookquery.searchPage;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.bookquery.R;

import java.util.ArrayList;

public class SearchManager {
    StringBuilder query = new StringBuilder("");
    SearchResult mSearchResult;
    ListView bookListView;
    TextView emptyTextView, loadDataButton;
    LinearLayout emptyView;
    View loadingBar;
    LoaderManager loaderManager;

    //tells whether the user initiated the search
    boolean isSearchedByTheUser;

    BookListAdapter listAdapter;
    static final int LOADER_ID = 1;
    Context context;
    LoaderManager.LoaderCallbacks<SearchResult> loaderCallback;
    ViewGroup parentViewGroup;

    public SearchManager(ViewGroup parentViewGroup, LinearLayout emptyView, LoaderManager loaderManager, Context context) {
        this.parentViewGroup = parentViewGroup;
        this.bookListView = parentViewGroup.findViewById(R.id.book_list);
        this.emptyView = emptyView;
        this.loaderManager = loaderManager;
        this.context = context;
        this.loaderCallback = new LoaderCallbacks();
        listAdapter = new BookListAdapter(context, 0, new ArrayList<ShortBookInfo>());
        emptyTextView = emptyView.findViewById(R.id.empty_TextView);
        loadDataButton = emptyView.findViewById(R.id.loadData_button);
        loadingBar = emptyView.findViewById(R.id.loading_spinner);
        loadDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
//        isSearchedByTheUser = new MutableBoolean(false);
        isSearchedByTheUser = false;
        setNewSearchLayout();
        loaderManager.initLoader(LOADER_ID, null, loaderCallback);
        SearchBookLoader loader = (SearchBookLoader) loaderManager.<SearchResult>getLoader(LOADER_ID);
        //if the loader is running then visible the loading bar
        if (loader.isRunning)
            loadingBar.setVisibility(View.VISIBLE);
    }

    //This method make a new search based on the new query typed by the user
    public void makeNewSearch(String query) {

        //sets the new search layout
        //store the query typed by the user
        //clear the previous result if available
        //and initiates the loader

        isSearchedByTheUser = true;
        setNewSearchLayout();
        loadingBar.setVisibility(View.VISIBLE);
        this.query.delete(0, this.query.length());
        this.query.append(query);
        if (mSearchResult != null)
            mSearchResult.clearSearchResult();
        loaderManager.destroyLoader(LOADER_ID);
        loaderManager.initLoader(LOADER_ID, null, loaderCallback);
    }

    //Sets the layout for new search
    private void setNewSearchLayout() {

        //clear the adatper and set that adapter to the listView
        listAdapter.clear();
        bookListView.setAdapter(listAdapter);

        //remove emptyView from the footer of the listView
        //if parentViewGroup doesn't have emptyView
        //then attach new RelativeLayoutParams to the emptyView
        //and add emptyView to the parentViewGroup
        bookListView.removeFooterView(emptyView);
        if (parentViewGroup.indexOfChild(emptyView) == -1) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            emptyView.setLayoutParams(layoutParams);
            parentViewGroup.addView(emptyView);
        }

        //visible th emptyView and Make visibility GONE to its child Views
        //It is upto the caller to set the visibility of the child views as required
        emptyView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        loadDataButton.setVisibility(View.GONE);
        loadingBar.setVisibility(View.GONE);
    }

    //re-search again for the previous query
    //This method works as retry button when previous search was failed
    //And also as a MoreResult button when the user wants to get more data on the same query
    public void loadData() {

        //Visible the circular progress bar
        //and make the button and the textView of the emptyView invisible
        //and re-initiate the search

        isSearchedByTheUser = true;
        loadingBar.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        loadDataButton.setVisibility(View.GONE);
        loadDataButton.setText(R.string.more_books);
        loaderManager.restartLoader(1, null, loaderCallback);
    }

    private void handleError(SearchResult searchResult) {
        //If listAdapter is empty then It should be a newSearch error
        //else it is a pagination error
        if (listAdapter.getCount() == 0) {
            handleNewSearchError(searchResult);
        } else
            handlePaginationError(searchResult);
    }

    //It handles the new search errors
    private void handleNewSearchError(@NonNull SearchResult searchResult) {
        //As in the new search emptyView was added to the parent of the listView, there is no need to insert it again
        //so change only the error message of the textView
        //And if required visible the button view

        //Visible the emptyView
        emptyTextView.setVisibility(View.VISIBLE);

        //This method will be invoked for only two reasons
        //If the user is not connected to the internet then show the retry Button to retry the search
        //else there are no books have been found matching the query, so set text no_books to the emptyTextView
        if (!searchResult.isConnected) {
            loadDataButton.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_connection);
            loadDataButton.setText(R.string.retry);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_books);
        }
    }

    //It handles pagination search error
    private void handlePaginationError(SearchResult searchResult) {
        //As in the more result search emptyView was added to the footerView of the listView, there is no need to insert it again
        //so change only the error message of the textView
        //And if required visible the button view

        //Visible the emptyView
        emptyTextView.setVisibility(View.VISIBLE);


        //This method will be invoked for only two reasons
        //If the user is not connected to the internet then show the retry Button to retry the search
        //else there are no books have been found matching the query, so set text no_books to the emptyTextView
        if (!searchResult.isConnected) {
            loadDataButton.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_connection);
            loadDataButton.setText(R.string.retry);
        } else {
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_books);
        }
    }

    //Upadates the UI of the screen
    private void updateUI(SearchResult searchResult) {

        //Store the current scroll position of the listView
        Parcelable listState = bookListView.onSaveInstanceState();

        //if adapter is empty (in case of new search and device rotation) and searchResult has bookss
        //then add all the searchResult book list to the adapter
        //else if latestResult has books then add this to the adapter
        if (listAdapter.getCount() == 0 && searchResult.getBooks().size() > 1)
            listAdapter.addAll(searchResult.getBooks());
        else if (listAdapter.getCount() != 0 && searchResult.latestResult.getBooks().size() > 1)
            listAdapter.addAll(searchResult.latestResult.getBooks());

        //if the parentViewGroup has emptyView and searchResult has content
        //then remove the emptyView from the parentViewGroup and add it to the footer view of the ListView
        if (parentViewGroup.indexOfChild(emptyView) != -1 && searchResult.getBooks().size() > 1) {
            parentViewGroup.removeView(emptyView);
            addEmptyViewToListView();
        }

        //if latesResult contains more then 9 books then page should be paginated
        //so set more books string to the loadDataButton and make it visible
        if (searchResult.latestResult.getBooks().size() > 9) {
            loadDataButton.setText(R.string.more_books);
            emptyView.setVisibility(View.VISIBLE);
            loadDataButton.setVisibility(View.VISIBLE);
        }

        //if latestResult doesn't contains any books
        //then there should be some error while Loading, so handle it
        if (searchResult.latestResult.getBooks().size() == 0)
            handleError(searchResult.latestResult);

        //Restore the listView to the previous position
        bookListView.onRestoreInstanceState(listState);
    }

    //Add emptyView to the footer of the ListView
    private void addEmptyViewToListView() {
        ViewGroup.LayoutParams layoutParams = new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
        emptyView.setLayoutParams(layoutParams);
        bookListView.addFooterView(emptyView);
    }

    class LoaderCallbacks implements LoaderManager.LoaderCallbacks<SearchResult> {
        @NonNull
        @Override
        public Loader<SearchResult> onCreateLoader(int id, @Nullable Bundle args) {
            return new SearchBookLoader(isSearchedByTheUser, context, mSearchResult, query.toString());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<SearchResult> loader, SearchResult data) {
//                isSearchedByTheUser.value = false;
            loadingBar.setVisibility(View.GONE);

            //If the searchResult is null and data comes then point searcResult to that
            //It happens in case of configuration change like rotation of device
            if (mSearchResult == null)
                mSearchResult = data;
            if (query.length() < 1)
                query.append(data.query);
            updateUI(data);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<SearchResult> loader) {
            mSearchResult = null;
        }
    }
}


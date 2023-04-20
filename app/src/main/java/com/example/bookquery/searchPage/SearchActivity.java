package com.example.bookquery.searchPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import com.example.bookquery.QueryUtils;
import com.example.bookquery.R;
import com.example.bookquery.bookInfo.BookInfoActivity;

public class SearchActivity extends AppCompatActivity {
    RelativeLayout parentRelativeView;
    SearchView searchView;
    SearchManager mSearchManager;
    LinearLayout emptyView;
    LoaderManager mLoaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        emptyView = (LinearLayout) getLayoutInflater().inflate(R.layout.empty_view, null);
        searchView = findViewById(R.id.searchView);
        parentRelativeView = findViewById(R.id.search_parentView);
        mLoaderManager = LoaderManager.getInstance(this);
        ListView bookList = findViewById(R.id.book_list);
        mSearchManager = new SearchManager(parentRelativeView, emptyView, mLoaderManager, this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                //search for the given query
                //and clear the focus from the searchView
                searchView.clearFocus();
                mSearchManager.makeNewSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        bookList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShortBookInfo shortBookInfo = (ShortBookInfo) bookList.getAdapter().getItem(position);
                Intent intent = new Intent(SearchActivity.this, BookInfoActivity.class);
                intent.putExtra(QueryUtils.ID, shortBookInfo.getVolumeId());
                startActivity(intent);
            }
        });
    }

}
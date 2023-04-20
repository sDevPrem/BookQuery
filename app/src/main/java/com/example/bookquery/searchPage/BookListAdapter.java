package com.example.bookquery.searchPage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bookquery.QueryUtils;
import com.example.bookquery.R;

import java.util.ArrayList;
import java.util.List;

public class BookListAdapter extends ArrayAdapter<ShortBookInfo> {
    Context mContext;
    ArrayList<ShortBookInfo> mShortBookInfoList;

    public BookListAdapter(@NonNull Context context, int resource, @NonNull List<ShortBookInfo> objects) {
        super(context, resource, objects);
        mContext = context;
        mShortBookInfoList = (ArrayList<ShortBookInfo>) objects;
    }

    public List<ShortBookInfo> getItemList() {
        return mShortBookInfoList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ShortBookInfo currentShortBookInfo = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        setValueToView(currentShortBookInfo, view, position);
        return view;
    }


    private void setValueToView(ShortBookInfo shortBookInfo, View view, int position) {

        TextView title = view.findViewById(R.id.book_title);
        TextView bookAuthor = view.findViewById(R.id.authors);
        TextView avgRating = view.findViewById(R.id.avg_ratings);
        TextView description = view.findViewById(R.id.description);
        ImageView img = view.findViewById(R.id.book_img);
        img.setImageResource(R.drawable.ic_baseline_book_24);


        title.setText(shortBookInfo.getTitle());
        avgRating.setText(shortBookInfo.getAvgRating());

        bookAuthor.setText(QueryUtils.stringArrayToString(shortBookInfo.getAuthors()));

        if (shortBookInfo.getDescription().equals(""))
            description.setVisibility(View.GONE);
        else
            description.setText(shortBookInfo.getDescription());

        setImage(position, shortBookInfo, img);

    }

    private void setImage(int position, ShortBookInfo bookInfo, ImageView img) {
        //if object doesn't have thumbnail image then get the thumbnail of the book
        //else set the thumbnail to the thumbnail imageView
        if (bookInfo.getThumbBitmap() == null) {
            new QueryUtils.SetImage(bookInfo, img, getContext()).execute(bookInfo.getThumbUrl());
        } else if (bookInfo.getThumbBitmap() != null) {
            img.setImageBitmap(bookInfo.getThumbBitmap());
        }
    }


}


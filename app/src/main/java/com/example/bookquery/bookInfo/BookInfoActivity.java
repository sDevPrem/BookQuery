package com.example.bookquery.bookInfo;

import android.os.Bundle;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.bookquery.QueryUtils;
import com.example.bookquery.R;

public class BookInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<FullBookInfo> {

    ViewGroup parentLayout;
    ViewGroup bookInfoLayout, emptyLayout;
    TextView emptyText;
    ProgressBar progressBar;
    Button emptyButton;
    String url = "https://content-books.googleapis.com/books/v1/volumes/5iTebBW-w7QC";
    String description = "<p>Apple reported record sales in the holiday quarter, beating estimates as it benefited from high iPhone demand in China and withstood constraints caused by supply chain disruptions and the Omicron variant.</p> <p>Apple’s CEO, Tim Cook, had warned in October that chip shortages were affecting the manufacturing of most Apple products and could lead to over $6bn in lost sales. But on Thursday, the company celebrated a successful quarter in a call with investors.</p> <p>“Despite the uncertainty of the world, there is one thing of which I am certain: Apple will continue to improve every day and in every way to deliver on the promise of technology at its best,” he said.</p> <p>Reporting its first quarter earnings of 2022, the company said that it made an all-time record revenue of<strong> </strong>$123.9bn, 11% up from last year and higher than analysts’ average estimate of $118.7bn</p> <aside class=\\\"element element-rich-link element--thumbnail\\\"> <p> <span>Related: </span><a href=\\\"https://www.theguardian.com/technology/2022/jan/20/apple-airtags-stalking-complaints-technology\\\">‘I was just really scared’: Apple AirTags lead to stalking complaints</a> </p> </aside>  <p>Executives attributed the strong quarter to the success of its products, reporting record holiday iPhone sales in late 2021. It also launched several new products in the fall, including new models of the Apple Watch, iPad, MacBook Pro computer and AirPods.</p> <p>“The very strong customer response to our recent launch of new products and services drove double-digit growth in revenue and earnings, and helped set an all-time high for our installed base of active devices,” said Luca Maestri, Apple’s CFO.</p> <p>Although analysts had anticipated a “blockbuster” quarter with record revenues, high expectations were tampered by ongoing supply chain problems. </p> <p>But the successful quarter shows Apple is more resistant than other tech companies to those constraints, said Tom Johnson, chief digital officer at media agency Mindshare Worldwide.</p> <p>“Apple’s cult following will always buy the latest version of its flagship device,” he said. “Chip shortages were never going to hold back its growth long term.”</p> <p>Still,<strong> </strong>Apple faces setbacks in the form of ongoing Covid-19 concerns. The surge of the Omicron variant forced the company to close down a number of Apple stores as well as corporate offices.</p> <p>Maestri offered forward-looking statements on a call with investors on Thursday, indicating that executives do not anticipate Covid-related impacts to worsen in the coming quarter.</p> <p>“We expect to achieve solid year over year revenue growth and set a March quarter revenue record despite significant supply constraints,” he said.</p> <p>Antitrust legislation is also spelling trouble for the company, as CEO Tim Cook continues to aggressively <a href=\\\"https://www.businessinsider.com/tim-cook-nancy-pelosi-apple-lobby-antitrust-bills-legislation-nyt-2021-6\\\">lobby against</a> a number of new antitrust bills. </p> <p>Under one such bill, introduced by Senator Amy Klobuchar, competing app stores would be allowed and consumers could choose which stores to use. Currently, Apple dominates the marketplace for how its users download new apps.</p> <p>Cook has argued such changes would turn the app store ecosystem into the “wild west”, allowing unscrupulous companies to flood users’ phones with malware.</p> <p>Apple’s services sector, which includes the App Store, saw large growth in the last quarter, up 24% from one year ago to $19.5bn.</p> <p>Apple’s successful quarter comes on the heels of a record earnings report from Tesla on Thursday, both indicating the tech sector may be on the mend after a series of weak quarters.</p> <p>Analysts will look to earnings reports from other tech giants including Google parent company Alphabet and Amazon to see if that success will continue.</p> <p>“Apple’s blowout results are just what the tech sector needed to get out of its recent funk,” said Jesse Cohen, senior analyst at <a href=\\\"http://investing.com/\\\">Investing.com</a>. “The iPhone giant’s big beat could be the next catalyst to spark a rally in the tech space.”</p> <p>Reuters contributed to this report.</p>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        url = QueryUtils.makeBookInfoUrl(getIntent().getStringExtra(QueryUtils.ID));
        parentLayout = findViewById(R.id.bookInfo_parentLayout);
        bookInfoLayout = findViewById(R.id.fullBookInfoLayout);
        bookInfoLayout.setVisibility(View.GONE);
        emptyLayout = (ViewGroup) getLayoutInflater().inflate(R.layout.empty_view, parentLayout, false);
        emptyButton = emptyLayout.findViewById(R.id.loadData_button);
        progressBar = emptyLayout.findViewById(R.id.loading_spinner);
        emptyText = emptyLayout.findViewById(R.id.empty_TextView);

        addEmptyLayout(parentLayout, emptyLayout);

        emptyButton.setVisibility(View.GONE);
        emptyText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        LoaderManager.getInstance(BookInfoActivity.this).initLoader(1, savedInstanceState, BookInfoActivity.this);

        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoaderManager.getInstance(BookInfoActivity.this).restartLoader(1, savedInstanceState, BookInfoActivity.this);
                progressBar.setVisibility(View.VISIBLE);
                emptyButton.setVisibility(View.GONE);
                emptyText.setVisibility(View.GONE);
            }
        });
    }

    //Adds emptyView to the RelativeLayout
    private void addEmptyLayout(@NonNull ViewGroup viewGroup, @NonNull View emptyView) {
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        parentLayout.setLayoutParams(flp);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        emptyView.setLayoutParams(rlp);
        viewGroup.addView(emptyView);
    }

    private void updateData(FullBookInfo data) {
        progressBar.setVisibility(View.GONE);
        //if there is no data show the error
        // else updates the UI according to the given data
        if (data == null) {
            emptyText.setText(R.string.FullBookInfoError);
            emptyText.setVisibility(View.VISIBLE);
            emptyButton.setVisibility(View.VISIBLE);
        } else updateUI(data);
    }

    //updates the UI of the screen
    private void updateUI(@NonNull FullBookInfo bookInfo) {
        emptyLayout.setVisibility(View.GONE);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentLayout.setLayoutParams(lp);

        ((TextView) findViewById(R.id.content_title)).setText(bookInfo.title);

        Spanned htmlString = HtmlCompat.fromHtml(bookInfo.description, HtmlCompat.FROM_HTML_MODE_LEGACY);
        ((TextView) findViewById(R.id.content_description)).setText(htmlString);
        ((TextView) findViewById(R.id.content_publisher)).setText(bookInfo.publisher);
        ((TextView) findViewById(R.id.content_publishedDate)).setText(bookInfo.publishedDate.split("-")[0]);
        ((TextView) findViewById(R.id.content_language)).setText(bookInfo.language);
        ((TextView) findViewById(R.id.content_pageCount)).setText(String.valueOf(bookInfo.pageCount));
        ((TextView) findViewById(R.id.content_authors)).setText(QueryUtils.stringArrayToString(bookInfo.authors));

        TextView categories = (TextView) findViewById(R.id.content_Categories);
        if (bookInfo.categories.length == 0)
            categories.setText(R.string.not_categorised);
        else
            categories.setText(QueryUtils.stringArrayToString(bookInfo.categories));

        TextView ratingsCount = (TextView) findViewById(R.id.content_ratingCount);
        TextView avgRating = (TextView) findViewById(R.id.content_avgRating);
        if (bookInfo.ratingsCount == 0) {
            avgRating.setVisibility(View.GONE);
            ratingsCount.setText(R.string.no_rating);
        } else {
            avgRating.setText(String.valueOf(bookInfo.avgRating));
            ratingsCount.setText(String.valueOf(bookInfo.ratingsCount));
        }
        if (bookInfo.getThumbBitmap() == null)
            new QueryUtils.SetImage(bookInfo, ((ImageView) findViewById(R.id.book_img)), this).execute(bookInfo.getThumbUrl());
        else
            ((ImageView) findViewById(R.id.book_img)).setImageBitmap(bookInfo.getThumbBitmap());
        emptyLayout.setVisibility(View.GONE);
        bookInfoLayout.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<FullBookInfo> onCreateLoader(int id, @Nullable Bundle args) {
        return new BookInfoLoader(BookInfoActivity.this, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<FullBookInfo> loader, FullBookInfo data) {
        updateData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<FullBookInfo> loader) {

    }
}

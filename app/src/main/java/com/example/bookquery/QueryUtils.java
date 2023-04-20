package com.example.bookquery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.bookquery.bookInfo.FullBookInfo;
import com.example.bookquery.searchPage.SearchResult;
import com.example.bookquery.searchPage.ShortBookInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


//Request the api for the queried mShortBookInfo
//and extracting shortBookInfoList from the json
final public class QueryUtils {

    public static final String ID = "ID";

    static ArrayList<ShortBookInfo> shortBookInfoList;
    static int responseCode;

    //Search for the books according to the given url
    public static SearchResult searchBooks(String url) {
        try {
            return extractBooksFromJson(getJsonResponse(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SearchResult(new ArrayList<>(), true, responseCode);
    }

    //Initiates the search from the given url and returns the JSON that is returned by the host
    private static String getJsonResponse(String urlString) throws IOException {
        URL url;
        HttpURLConnection httpConnection = null;
        InputStream inputStream = null;
        String jsonResponse = "";
        try {
            url = new URL(urlString);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setReadTimeout(1500);
            httpConnection.setConnectTimeout(1500);
            httpConnection.connect();
            responseCode = httpConnection.getResponseCode();
            if (httpConnection.getResponseCode() == 200) {
                inputStream = httpConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally {
            if (httpConnection != null)
                httpConnection.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        return jsonResponse;
    }

    //convert the InputStream to the JSON string
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder jsonResponse = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = bf.readLine();
        while (line != null) {
            jsonResponse.append(line);
            line = bf.readLine();
        }
        return jsonResponse.toString();
    }

    //extracts and returns a List of Books from the JSON string
    private static SearchResult extractBooksFromJson(String JSONResponse) {
        JSONObject response = null;

        if (JSONResponse == null || JSONResponse.equals("") || JSONResponse.length() < 1) {
            return new SearchResult(new ArrayList<ShortBookInfo>(), true, responseCode);
        }
        try {
            response = new JSONObject(JSONResponse);

            if (response.getInt("totalItems") == 0) {
                return new SearchResult(new ArrayList<ShortBookInfo>(), true, responseCode);
            }
            JSONArray items = response.getJSONArray("items");
            shortBookInfoList = new ArrayList<>(items.length());
            for (int i = 0; i < items.length(); i++) {
                JSONObject volume = items.getJSONObject(i);
                shortBookInfoList.add(extractShortBookInfo(volume));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new SearchResult(shortBookInfoList, true, responseCode);
    }

    //Returns an object of ShortBookInfo which is created from the given volume JSONObject
    private static ShortBookInfo extractShortBookInfo(JSONObject volume) throws JSONException {
        String title, description, avg_Ratings, smallThumbUrl, volumeId;
        String[] authors;
        JSONObject volumeInfo = volume.getJSONObject("volumeInfo");
        volumeId = volume.getString("id");
        if (volumeInfo.has("authors")) {
            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
            authors = new String[authorsArray.length()];
            for (int i = 0; i < authorsArray.length(); i++) {
                authors[i] = authorsArray.getString(i);
            }
        } else {
            authors = new String[]{
                    "",
            };
        }
        title = volumeInfo.getString("title");
        if (volumeInfo.has("description"))
            description = volumeInfo.getString("description");
        else description = "";
        if (volumeInfo.has("averageRating"))
            avg_Ratings = String.valueOf(volumeInfo.getDouble("averageRating"));
        else avg_Ratings = "0.0";
        if (volumeInfo.has("imageLinks"))
            smallThumbUrl = volumeInfo.getJSONObject("imageLinks").getString("smallThumbnail");
        else smallThumbUrl = "";
        return new ShortBookInfo(title, avg_Ratings, description, volumeId, authors, smallThumbUrl);
    }

    //make url string according to the given query and startIndex
    public static String makeSearchURL(String query, int startIndex) {
        if (query == null || query.length() == 0)
            return "";
        String url = "";
        URL urlObject = null;
        try {
            url = "https://books.googleapis.com/books/v1/volumes?q=" + URLEncoder.encode(query, "UTF-8")
                    + "&maxResults=10"
                    + "&startIndex=" + startIndex
                    + "&projection=lite";
            urlObject = new URL(url);
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            Log.e("URLError", e.toString());
            e.printStackTrace();
        }
        return url;
    }

    //Make url for obtaining full info about a book from the given volume id of the book
    static public String makeBookInfoUrl(String id) {
        return "https://content-books.googleapis.com/books/v1/volumes/" + id;
    }

    //extract the full info about a book from the JSON response
    public static FullBookInfo extractFullBookInfo(String jsonResponse) throws JSONException {
        JSONObject response = null;
        String title, publishedDate, description, language, id, publisher, thumbUrl;
        String[] authors, categories;
        int pageCount, ratingsCount;
        double avgRating;
        response = new JSONObject(jsonResponse);
        id = response.getString("id");
        JSONObject volumeInfo = response.getJSONObject("volumeInfo");

        if (volumeInfo.has("publisher"))
            publisher = volumeInfo.getString("publisher");
        else
            publisher = "";
        if (volumeInfo.has("title"))
            title = volumeInfo.getString("title");
        else
            title = "";
        if (volumeInfo.has("publishedDate"))
            publishedDate = volumeInfo.getString("publishedDate");
        else
            publishedDate = "";
        if (volumeInfo.has("description"))
            description = volumeInfo.getString("description");
        else
            description = "";
        if (volumeInfo.has("language"))
            language = volumeInfo.getString("language");
        else
            language = "";
        if (volumeInfo.has("pageCount"))
            pageCount = volumeInfo.getInt("pageCount");
        else
            pageCount = 0;
        if (volumeInfo.has("ratingsCount"))
            ratingsCount = volumeInfo.getInt("ratingsCount");
        else
            ratingsCount = 0;
        if (volumeInfo.has("averageRating"))
            avgRating = volumeInfo.getDouble("averageRating");
        else
            avgRating = 0;
        if (volumeInfo.has("authors")) {
            JSONArray jsonArray = volumeInfo.getJSONArray("authors");
            authors = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                authors[i] = jsonArray.getString(i);
            }
        } else
            authors = new String[0];
        if (volumeInfo.has("categories")) {
            JSONArray jsonArray = volumeInfo.getJSONArray("categories");
            categories = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                categories[i] = jsonArray.getString(i);
            }
        } else
            categories = new String[0];
        if (volumeInfo.has("imageLinks")) {
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            if (imageLinks.has("thumbnail")) {
                thumbUrl = imageLinks.getString("thumbnail");
            } else
                thumbUrl = "";
        } else
            thumbUrl = "";
        return new FullBookInfo(title, avgRating, description, id, publisher, publishedDate, language, authors, categories, pageCount, ratingsCount, thumbUrl);
    }

    //request the api for full book info
    public static FullBookInfo retrieveFullBookInfo(String url) {
        try {
            return extractFullBookInfo(getJsonResponse(url));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Get the thumbnail of the book and set the thumbnail to the corresponding imageView of the BookInfo object
    //also store it in the object for later use
    static public class SetImage extends AsyncTask<SetImage.BookWithImage, Void, SetImage.ImageViewWithBitmap> {

        static public class BookWithImage {
            private final BookInfo mBookInfo;
            private final ImageView imageView;

            public BookWithImage(BookInfo bookInfo, ImageView imageView) {
                mBookInfo = bookInfo;
                this.imageView = imageView;
            }
        }

        private static class ImageViewWithBitmap {
            private final ImageView mImageView;
            private final Bitmap mBitmap;

            public ImageViewWithBitmap(ImageView imageView, Bitmap bitmap) {
                mImageView = imageView;
                mBitmap = bitmap;
            }

        }

        @Override
        protected ImageViewWithBitmap doInBackground(BookWithImage... books) {
            String urlDisplay = books[0].mBookInfo.getThumbUrl();
            if (urlDisplay.length() == 0)
                return null;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                Log.e("Error", e.getMessage() + " " + urlDisplay);
                e.printStackTrace();
            }
            books[0].mBookInfo.setThumbBitmap(mIcon11);
            return new ImageViewWithBitmap(books[0].imageView, mIcon11);
        }

        @Override
        protected void onPostExecute(ImageViewWithBitmap item) {
            if (item == null || item.mBitmap == null)
                return;
            item.mImageView.setImageBitmap(item.mBitmap);
        }

        public Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    //Gives the user connection status whether the user is connected to the internet
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //separates the item of the array with ',' and append 'and' before the last item
    public static String stringArrayToString(String[] s) {
        if (s == null || s.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        if (s.length == 1)
            return s[0];
        sb.append(s[0]);
        for (int i = 1; i < s.length - 1; i++) {
            sb.append(", ").append(s[i]);
        }
        sb.append(" and ").append(s[s.length - 1]);
        return sb.toString();
    }

}

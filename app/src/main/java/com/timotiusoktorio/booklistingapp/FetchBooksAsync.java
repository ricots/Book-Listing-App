package com.timotiusoktorio.booklistingapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.timotiusoktorio.booklistingapp.adapter.BooksAdapter;
import com.timotiusoktorio.booklistingapp.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_AUTHORS;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_ID;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_IMAGE_LINKS;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_ITEMS;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_THUMBNAIL;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_TITLE;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_JSON_VOLUME_INFO;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_SEARCH_PARAM_MAX_RESULTS;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_SEARCH_PARAM_QUERY;
import static com.timotiusoktorio.booklistingapp.Constants.BOOKS_SEARCH_URL;

/**
 * Created by Timotius on 2016-07-20.
 */

public class FetchBooksAsync extends AsyncTask<String, Void, List<Book>> {

    private static final String LOG_TAG = FetchBooksAsync.class.getSimpleName();
    private static final int MAX_RESULTS = 10;
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    private BooksAdapter mAdapter;

    public FetchBooksAsync(BooksAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mAdapter.clear();
    }

    @Override
    protected List<Book> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        List<Book> books = null;

        try {
            Uri uri = Uri.parse(BOOKS_SEARCH_URL).buildUpon()
                    .appendQueryParameter(BOOKS_SEARCH_PARAM_QUERY, params[0])
                    .appendQueryParameter(BOOKS_SEARCH_PARAM_MAX_RESULTS, String.valueOf(MAX_RESULTS))
                    .build();

            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) builder.append(line).append("\n");

                String jsonString = builder.toString();
                if (!TextUtils.isEmpty(jsonString)) books = extractBooksFromJSONString(jsonString);
            } else {
                Log.e(LOG_TAG, "Error response code: " + responseCode);
            }
        }
        catch (IOException | JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        finally {
            try {
                if (urlConnection != null) urlConnection.disconnect();
                if (inputStream != null) inputStream.close();
                if (reader != null) reader.close();
            }
            catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
        }

        return books;
    }

    @Override
    protected void onPostExecute(List<Book> books) {
        super.onPostExecute(books);
        if (books != null && books.size() > 0) mAdapter.addAll(books);
    }

    private List<Book> extractBooksFromJSONString(String jsonString) throws JSONException {
        List<Book> books = new ArrayList<>();
        JSONObject root = new JSONObject(jsonString);
        JSONArray items = root.getJSONArray(BOOKS_JSON_ITEMS);

        for (int i = 0; i < items.length(); i++) {
            JSONObject bookObject = items.getJSONObject(i);
            String id = bookObject.getString(BOOKS_JSON_ID);

            JSONObject volumeInfo = bookObject.getJSONObject(BOOKS_JSON_VOLUME_INFO);
            String title = volumeInfo.getString(BOOKS_JSON_TITLE);

            String[] authors = new String[] {};
            // Some books surprisingly have no authors. Therefore a null-check is necessary.
            JSONArray authorsArray = volumeInfo.optJSONArray(BOOKS_JSON_AUTHORS);
            if (authorsArray != null) {
                authors = new String[authorsArray.length()];
                for (int j = 0; j < authorsArray.length(); j++) {
                    authors[j] = authorsArray.getString(j);
                }
            }

            JSONObject imageLinks = volumeInfo.optJSONObject(BOOKS_JSON_IMAGE_LINKS);
            String thumbnail = imageLinks.optString(BOOKS_JSON_THUMBNAIL);

            Book book = new Book(id, title, authors, thumbnail);
            books.add(book);
        }

        return books;
    }

}
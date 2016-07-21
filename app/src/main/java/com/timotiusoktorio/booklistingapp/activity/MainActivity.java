package com.timotiusoktorio.booklistingapp.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.timotiusoktorio.booklistingapp.FetchBooksAsync;
import com.timotiusoktorio.booklistingapp.R;
import com.timotiusoktorio.booklistingapp.Utility;
import com.timotiusoktorio.booklistingapp.adapter.BooksAdapter;
import com.timotiusoktorio.booklistingapp.model.Book;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BooksAdapter mAdapter;
    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the adapter with empty data as it'll be populated later in FetchBooksAsync.
        mAdapter = new BooksAdapter(this, new ArrayList<Book>());

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
        listView.setEmptyView(findViewById(android.R.id.empty));

        Button searchBooksButton = (Button) findViewById(R.id.search_books_button);
        searchBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find a reference of the SearchView EditText and clear the text.
                EditText editText = (EditText) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                editText.setText("");

                // Expand and focus the search bar.
                if (mSearchMenuItem.isActionViewExpanded()) mSearchMenuItem.collapseActionView();
                mSearchMenuItem.expandActionView();
                mSearchView.requestFocus();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Find a reference of the SearchView from the search menu item and set it up.
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        mSearchView.setQueryHint(getString(R.string.search_view_query_hint));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // This method gets invoked when the user press the keyboard search button.
                // Execute the async task to fetch the books data with the given query.
                if (Utility.isNetworkAvailable(MainActivity.this)) new FetchBooksAsync(mAdapter).execute(query);
                else Toast.makeText(MainActivity.this, R.string.msg_network_unavailable, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

}
package com.timotiusoktorio.booklistingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.timotiusoktorio.booklistingapp.R;
import com.timotiusoktorio.booklistingapp.model.Book;

import java.util.List;

/**
 * Created by Timotius on 2016-07-20.
 */

public class BooksAdapter extends ArrayAdapter<Book> {

    private Context mContext;
    private List<Book> mBooks;

    public BooksAdapter(Context context, List<Book> books) {
        super(context, R.layout.list_item_book, books);
        mContext = context;
        mBooks = books;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern for faster and more efficient View recycling.
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_book, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bookTitleTextView = (TextView) convertView.findViewById(R.id.book_title_text_view);
            viewHolder.bookAuthorTextView = (TextView) convertView.findViewById(R.id.book_author_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Book book = mBooks.get(position);
        viewHolder.bookTitleTextView.setText(book.getTitle());

        String[] authors = book.getAuthors();
        // Some books have no authors. If the book has no authors, display the no authors text.
        if (authors.length == 0) {
            viewHolder.bookAuthorTextView.setText(mContext.getString(R.string.text_view_book_author_empty));
        } else {
            // Build the authors String from the String array.
            // Author TextView format should look like: Author A, Author B, Author C.
            StringBuilder builder = new StringBuilder();
            for (String author : authors) {
                builder.append(author);
                if (!author.equals(authors[authors.length - 1]))
                    builder.append(", ");
            }
            viewHolder.bookAuthorTextView.setText(builder.toString());
        }
        return convertView;
    }

    static class ViewHolder {

        TextView bookTitleTextView;
        TextView bookAuthorTextView;

    }

}
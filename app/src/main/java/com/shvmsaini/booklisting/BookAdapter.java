package com.shvmsaini.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(@NonNull Context context, List<Book> books) {
        super(context,0,books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView ==null){
            listView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item,parent,false);
        }
        Book currentBook = getItem(position);
        ((TextView)(listView.findViewById(R.id.title))).setText(currentBook.getmTitle());
        ((TextView)(listView.findViewById(R.id.author))).setText(currentBook.getmAuthor());
        ((TextView)(listView.findViewById(R.id.price))).setText(currentBook.getmPrice());
        ((ImageView)(listView.findViewById(R.id.thumbnail))).setImageBitmap(currentBook.getImageBitmap());
        return listView;
    }

    @Override
    public int getPosition(@Nullable Book item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }
}

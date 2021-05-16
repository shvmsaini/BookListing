package com.shvmsaini.booklisting;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {
    public ImageView bookThumbnail;
    public TextView bookTitle;
    public TextView bookAuthor;
    public TextView bookPrice;
    public Book book1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        int position = getIntent().getIntExtra("int",0);
        Book book = BookActivity.bookAdapter.getItem(position);
        bookThumbnail = findViewById(R.id.book_thumbnail);
        bookThumbnail.setImageBitmap(book.getImageBitmap());
        bookTitle = findViewById(R.id.book_title);
        bookTitle.setText(book.getmTitle());
        ((TextView)findViewById(R.id.book_author)).setText(book.getmAuthor());
        String price = book.getmPrice();
        bookPrice = findViewById(R.id.book_price);
        bookPrice.setText(price);
        if(price.equals("NOT_FOR_SALE")){
            bookPrice.setTextColor(Color.RED);
        }
        else bookPrice.setTextColor(Color.GREEN);
        if(bookTitle.getText().length()>100){
            bookTitle.setTextSize(16);
        }
        String str = getIntent().getStringExtra("string");
        Log.d("INSIDE",str);

        new Thread(() -> {
            try {
                String response = QueryUtility.ReadFromStream(QueryUtility.MakeHTTPRequest(book.getmSelfLink()));
                book1 = QueryUtility.extractBookDetailFromJSON(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.book_description)).setText(Html.fromHtml(book1.getmDescription()));
                Button buyButton = findViewById(R.id.buy_button);
                String url = book1.getmBuyLink();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                if(url.equals("http://")){
                    buyButton.setEnabled(false);
                    buyButton.setText(R.string.not_for_sale);
                    buyButton.setTextColor(Color.RED);

                }else{
                    Uri uri = Uri.parse(url);

                    buyButton.setOnClickListener(v -> {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                }

            });

        }).start();

    }
}

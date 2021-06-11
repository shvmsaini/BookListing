package com.shvmsaini.booklisting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

public class BookDetailActivity extends AppCompatActivity {
    public ImageView bookThumbnail;
    public TextView bookTitle;
    public TextView bookAuthor;
    public TextView bookPrice;
    public Book bookDetail;
    public TextView bookDescription;
    public TextView publishingDate;
    public TextView pageCount;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        int position = getIntent().getIntExtra("int",0);
        Book book = BookActivity.bookAdapter.getItem(position);
        bookThumbnail = findViewById(R.id.book_thumbnail);
        bookThumbnail.setImageBitmap(book.getImageBitmap());
        bookTitle = findViewById(R.id.book_title);
        bookTitle.setText(book.getmTitle());
        bookDescription = findViewById(R.id.book_description);
        ((TextView)findViewById(R.id.book_author)).setText(book.getmAuthor());
        String price = book.getmPrice();
        bookPrice = findViewById(R.id.book_price);
        bookPrice.setText(price);
        if(price.equals("NOT FOR SALE")){
            bookPrice.setTextColor(Color.RED);
        }
        else bookPrice.setTextColor(Color.GREEN);
        if(bookTitle.getText().length()>100){
            bookTitle.setTextSize(16);
        }
        String str = getIntent().getStringExtra("string");

        new Thread(() -> {

            try {
                String response = QueryUtility.ReadFromStream(QueryUtility.MakeHTTPRequest(book.getmSelfLink()));
                bookDetail = QueryUtility.extractBookDetailFromJSON(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if(book.getImageBitmap()==null){
                    ((ImageView)(findViewById(R.id.book_thumbnail))).setImageResource(R.mipmap.book_not_found);
                }
                String description = bookDetail.getmDescription();
                if(description.length()==0){
                   bookDescription.setText(R.string.no_description);
                }else{
                    bookDescription.setText(Html.fromHtml(description));
                }
                Button buyButton = findViewById(R.id.buy_button);

                String url = bookDetail.getmBuyLink();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                if(url.equals("http://")){
                    buyButton.setEnabled(false);
                    buyButton.setText(R.string.not_for_sale);


                }else{
                    Uri uri = Uri.parse(url);

                    buyButton.setOnClickListener(v -> {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                }
                Button downloadButton = findViewById(R.id.download);
                String pdfLink = bookDetail.getmPdfLink();
                if (!pdfLink.startsWith("http://") && !pdfLink.startsWith("https://"))
                    pdfLink = "http://" + pdfLink;
                String finalPdfLink = pdfLink;
                if(pdfLink.equals("http://")){
                    downloadButton.setEnabled(false);
                    downloadButton.setText(R.string.not_available);
                }else{
                    downloadButton.setVisibility(View.VISIBLE);
                    downloadButton.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(finalPdfLink);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                }
                publishingDate = findViewById(R.id.publishing_date);
                try{
                    publishingDate.setText(getString(R.string.published_in) + " " + bookDetail.getmPublishingDate().substring(0,4));
                }catch ( Exception e){
                   e.printStackTrace();
                }
                pageCount = findViewById(R.id.page_count);
                pageCount.setText(getString(R.string.page_count)+ " " + bookDetail.getmPageCount());

            });

            }).start();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

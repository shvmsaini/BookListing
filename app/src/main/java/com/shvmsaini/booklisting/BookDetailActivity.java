package com.shvmsaini.booklisting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shvmsaini.booklisting.databinding.BookDetailBinding;

import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;

public class BookDetailActivity extends AppCompatActivity {

    public BookDetailBinding bookDetailBinding;
    public Book bookDetail;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setting up view binding
        bookDetailBinding = BookDetailBinding.inflate(getLayoutInflater());

        // action bar back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        int position = getIntent().getIntExtra("int",0);
        Book book = BookActivity.bookAdapter.getItem(position);

        bookDetailBinding.bookThumbnail.setImageBitmap(book.getImageBitmap());
        bookDetailBinding.bookTitle.setText(book.getmTitle());
        bookDetailBinding.bookAuthor.setText(book.getmAuthor());

        String price = book.getmPrice();
        bookDetailBinding.bookPrice.setText(price);
        if(price.equals("NOT FOR SALE")){
            bookDetailBinding.bookPrice.setTextColor(Color.RED);
        }
        else bookDetailBinding.bookPrice.setTextColor(Color.GREEN);
        if(bookDetailBinding.bookTitle.getText().length()>100){
            bookDetailBinding.bookTitle.setTextSize(16);
        }

        String str = getIntent().getStringExtra("string");

        Thread detailGetter = new Thread(() -> {
            try {
                String response = QueryUtility.ReadFromStream(QueryUtility.MakeHTTPRequest(book.getmSelfLink()));
                bookDetail = QueryUtility.extractBookDetailFromJSON(response);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if(book.getImageBitmap()==null){
                    bookDetailBinding.bookThumbnail.setImageResource(R.mipmap.book_not_found);
                }
                String description = bookDetail.getmDescription();
                if(description.length()==0){
                    bookDetailBinding.bookDescription.setText(R.string.no_description);
                }else{
                    bookDetailBinding.bookDescription.setText(Html.fromHtml(description));
                }

                String url = bookDetail.getmBuyLink();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                if(url.equals("http://")){
                    bookDetailBinding.buyButton.setEnabled(false);
                    bookDetailBinding.buyButton.setText(R.string.not_for_sale);


                }else{
                    Uri uri = Uri.parse(url);

                    bookDetailBinding.buyButton.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                }

                String pdfLink = bookDetail.getmPdfLink();
                if (!pdfLink.startsWith("http://") && !pdfLink.startsWith("https://"))
                    pdfLink = "http://" + pdfLink;
                String finalPdfLink = pdfLink;
                if(pdfLink.equals("http://")){
                    bookDetailBinding.download.setEnabled(false);
                    bookDetailBinding.download.setText(R.string.not_available);
                }else{
                    bookDetailBinding.download.setVisibility(View.VISIBLE);
                    bookDetailBinding.download.setOnClickListener(v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse(finalPdfLink);
                        intent.setData(uri);
                        startActivity(intent);
                    });
                }
                try{
                    bookDetailBinding.publishingDate.setText(getString(R.string.published_in) + " " + bookDetail.getmPublishingDate().substring(0,4));
                }catch ( Exception e){
                    e.printStackTrace();
                }
                bookDetailBinding.pageCount.setText(getString(R.string.page_count)+ " " + bookDetail.getmPageCount());

            });

        });
        detailGetter.start();
        try {
            detailGetter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(bookDetailBinding.getRoot());

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

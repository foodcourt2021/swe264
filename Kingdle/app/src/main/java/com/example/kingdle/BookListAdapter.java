package com.example.kingdle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private List<Book> bookList;
    BookListAdapter(List<Book> list) {
        this.bookList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBook;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvRating;
        TextView tvDescription;
        TextView tvIsbn;

        ViewHolder(View bookRow) {
            super(bookRow);
            ivBook = bookRow.findViewById(R.id.ivBook);
            tvTitle = bookRow.findViewById(R.id.tvTitle);
            tvAuthor = bookRow.findViewById(R.id.tvAuthor);
            tvRating = bookRow.findViewById(R.id.tvRating);
            tvDescription = bookRow.findViewById(R.id.tvDescription);
            tvIsbn =  bookRow.findViewById(R.id.tvIsbn);
        }
    }

    @NonNull
    @Override
    public BookListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = bookList.get(position);
        //Picasso.get().load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath()).into(holder.ivMovie);
        holder.tvTitle.setText(book.get_title());
        holder.tvAuthor.setText(book.get_author());
        holder.tvDescription.setText(book.get_description());
        holder.tvIsbn.setText(book.get_isbn());
        holder.tvRating.setText(Float.toString(book.get_rating()));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

}

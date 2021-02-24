package com.example.kingdle.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kingdle.R;
import com.squareup.picasso.Picasso;

import java.util.*;

import javax.sql.StatementEvent;

public class SearchBookAdapter extends RecyclerView.Adapter<SearchBookAdapter.ViewHolder>{
    private List<HashMap<String, List<String>>> mData;

    public SearchBookAdapter(List<HashMap<String, List<String>>> mData) {
        this.mData = mData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView tvImage;
        TextView tvInfo;
        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvImage = itemView.findViewById(R.id.ivBook);
            tvInfo = itemView.findViewById(R.id.tvInfo);
        }
    }
    @Override
    public SearchBookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.response_row, parent, false);
        return new SearchBookAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchBookAdapter.ViewHolder holder, int position) {
        HashMap<String, List<String>> map = mData.get(position);
        holder.tvTitle.setText(map.get("Title").get(0));
        if(map.get("Image") != null && map.get("Image").size() > 0) {
            Picasso.get().load(map.get("Image").get(0)).into(holder.tvImage);
        }
//        Picasso.get().load("https://www.gstatic.com/webp/gallery/4.sm.jpg").into(holder.tvImage);
        StringBuilder des = new StringBuilder();
        des.append("\n");
        for(String key: map.keySet()){
            if(key.equals("Title") || key.equals("Image")) continue;
            des.append(key + ": ");
            if(map.get(key) == null || map.get(key).size() == 0){
                des.append("null, ");
            }
            else{
                for(String val: map.get(key)){
                    des.append(val + ", ");
                }
            }
            des.deleteCharAt(des.length() - 2);
            des.append("\n\n");
        }
        holder.tvInfo.setText(des.toString());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}

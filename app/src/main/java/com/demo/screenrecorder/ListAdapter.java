package com.demo.screenrecorder;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.screenrecorder.helper.MediaBean;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.List;

/**
 * @author: yanx
 * @time: 2020/11/16
 * @describe: com.demo.screenrecorder
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    private List<MediaBean> data;

    public ListAdapter(List<MediaBean> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext())
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .frame(4000000)
                                .centerCrop()

                )
                .load(data.get(position).getPath())
                .into(holder.imageView);
        holder.textView.setText(data.get(position).getMediaName()+"\n"+data.get(position).getLength()+"|"+data.get(position).getVideoTime()
        +"\n"+data.get(position).getCreateData());
        holder.itemView.setOnClickListener(v -> {
            holder.itemView.getContext().startActivity(new Intent( holder.itemView.getContext(),PlayVideoActivity.class)
            .putExtra("data",data.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            textView = itemView.findViewById(R.id.text);
        }
    }
}

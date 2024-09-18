package com.example.finalnews.newsmaterial;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.finalnews.R;
import com.example.finalnews.newsmaterial.model.Datum;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    Context context;
    ArrayList<Datum> datumArrayList;
    private int lastPosition = -1;

    public Adapter(Context context, ArrayList<Datum> modelArrayList) {
        this.context = context;
        this.datumArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);

       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.newstitle.setText(datumArrayList.get(position).getTitle());
        holder.newsdescription.setText(datumArrayList.get(position).getDescription());
        holder.publishedat.setText(datumArrayList.get(position).getPublished_at());
        holder.source.setText(datumArrayList.get(position).getSource());
        Glide.with(context).load(datumArrayList.get(position).getImage_url()).into(holder.imageView);


        // Here you apply the animation when the view is bound
        setAnimation(holder.itemView, position);



       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent in = new Intent(context,ReadNewsActivity.class);
               in.putExtra("URL",datumArrayList.get(position).getUrl());
               context.startActivity(in);
           }
       });

    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return datumArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView newstitle,newsdescription,publishedat,source;
        ImageView imageView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newstitle = itemView.findViewById(R.id.mainHealines_id);
            newsdescription = itemView.findViewById(R.id.newsdescription_id);
            imageView = itemView.findViewById(R.id.news_image);
            cardView = itemView.findViewById(R.id.card_view);
            publishedat = itemView.findViewById(R.id.published_at);
            source = itemView.findViewById(R.id.source);

        }
    }
}

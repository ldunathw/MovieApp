package com.example.MovieApp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.MovieApp.Activities.MainActivity;
import com.example.MovieApp.Activities.ProfileActivity;
import com.example.MovieApp.Database.FilmWatchlistDAO;
import com.example.MovieApp.Domains.SliderItems;
import com.example.MovieApp.R;

import java.util.List;

public class SlidersAdapter extends RecyclerView.Adapter<SlidersAdapter.SliderViewholder> {
    private List<SliderItems> sliderItems;
    private ViewPager2 viewPager2;
    private String phoneNumber;
    private Context context;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItems.addAll(sliderItems);
            notifyDataSetChanged();
        }
    };

    public SlidersAdapter(List<SliderItems> sliderItems, ViewPager2 viewPager2, String phoneNumber) {
        this.sliderItems = sliderItems;
        this.viewPager2 = viewPager2;
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    @Override
    public SlidersAdapter.SliderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SliderViewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_viewholder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SlidersAdapter.SliderViewholder holder, int position) {
        holder.setImage(sliderItems.get(position));
        if (position == sliderItems.size() - 2) {
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class SliderViewholder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTxt, genreTxt, ageTxt, yearTxt, timeTxt;
        private AppCompatButton buttonAddToWatchlist;

        public SliderViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            genreTxt = itemView.findViewById(R.id.genreTxt);
            ageTxt = itemView.findViewById(R.id.ageTxt);
            yearTxt = itemView.findViewById(R.id.yearTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            buttonAddToWatchlist = itemView.findViewById(R.id.buttonAddToWatchlist);
        }

        void setImage(SliderItems sliderItems) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(context)
                    .load(sliderItems.getImage())
                    .apply(requestOptions)
                    .into(imageView);

            nameTxt.setText(sliderItems.getName());
            genreTxt.setText(sliderItems.getGenre());
            ageTxt.setText(sliderItems.getAge());
            yearTxt.setText("" + sliderItems.getYear());
            timeTxt.setText(sliderItems.getTime());
            buttonAddToWatchlist.setOnClickListener(v -> {
                FilmWatchlistDAO watchListDAO = new FilmWatchlistDAO(context);
                if(!watchListDAO.isFilmWatchlistExists(phoneNumber, sliderItems.getName())){
                    long check = watchListDAO.addFilmWatchlist(phoneNumber, sliderItems.getName());
                    if(check> 0){
                        Toast.makeText(context, "Add to watch list success", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(context, "Add to watch list false", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context, "This film is exist in watch list", Toast.LENGTH_LONG).show();
                }
                watchListDAO.close();
            });
        }
    }
}

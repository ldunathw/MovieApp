package com.example.MovieApp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.MovieApp.Database.FilmWatchlistDAO;
import com.example.MovieApp.Domains.SliderItems;
import com.example.MovieApp.R;

import java.util.List;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.WatchListViewHolder> {
    private List<SliderItems> sliderItems;
    private String phoneNumber;
    private Context context;

    public WatchListAdapter(List<SliderItems> sliderItems, String phoneNumber) {
        this.sliderItems = sliderItems;
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    @Override
    public WatchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.slider_viewholder, parent, false);
        return new WatchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchListViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    public class WatchListViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTxt, genreTxt, ageTxt, yearTxt, timeTxt;
        private AppCompatButton buttonAddToWatchlist;

        public WatchListViewHolder(@NonNull View itemView) {
            super(itemView);
            // Set the height of itemView to 200 and add bottom margin
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLayoutParams.height = 500;
                marginLayoutParams.bottomMargin = 32; // Set bottom margin in pixels
                itemView.setLayoutParams(marginLayoutParams);
            } else {
                layoutParams.height = 500;
                itemView.setLayoutParams(layoutParams);
            }
            imageView = itemView.findViewById(R.id.imageSlide);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            genreTxt = itemView.findViewById(R.id.genreTxt);
            ageTxt = itemView.findViewById(R.id.ageTxt);
            yearTxt = itemView.findViewById(R.id.yearTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            buttonAddToWatchlist = itemView.findViewById(R.id.buttonAddToWatchlist);
        }

        void setImage(SliderItems sliderItem) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(60));
            Glide.with(context)
                    .load(sliderItem.getImage())
                    .apply(requestOptions)
                    .into(imageView);

            nameTxt.setText(sliderItem.getName());
            genreTxt.setText(sliderItem.getGenre());
            ageTxt.setText(sliderItem.getAge());
            yearTxt.setText("" + sliderItem.getYear());
            timeTxt.setText(sliderItem.getTime());

            buttonAddToWatchlist.setVisibility(View.GONE);
        }
    }
}

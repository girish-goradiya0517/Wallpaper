package com.dsquare.wallzee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Slider;
import com.dsquare.wallzee.R;
import com.github.islamkhsh.CardSliderAdapter;

import java.util.ArrayList;

public class SliderAdapter extends CardSliderAdapter<SliderAdapter.SliderAdapterHolder> {

    private ArrayList<Slider> sliders;

    OnSliderClickListner clickListner;
    Context context;

    public SliderAdapter(ArrayList<Slider> movies, Context context, OnSliderClickListner clickListner) {
        this.sliders = movies;
        this.clickListner = clickListner;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return sliders.size();
    }

    @Override
    public SliderAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new SliderAdapterHolder(view);
    }

    @Override
    public void bindVH(@NonNull SliderAdapterHolder sliderAdapterHolder, int i) {
        sliders.forEach(slider -> {
            Log.d("SliderAdapter", "bindVH: SliderImage" + slider.getSliderImage() + "and SliderID" + slider.getId());
        });
        Slider item = sliders.get(i);
        String url = Config.WEBSITE_URL + "/images/slider/" + item.getSliderImage();
        Glide.with(sliderAdapterHolder.itemView).load(url)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(sliderAdapterHolder.sliderImage);
        sliderAdapterHolder.sliderImage.setOnClickListener(v -> {
            clickListner.onSliderClick(item, i);
        });

    }

    public void resetListData() {
        this.sliders = new ArrayList<>();
        notifyDataSetChanged();
    }

    class SliderAdapterHolder extends RecyclerView.ViewHolder {

        ImageView sliderImage;

        public SliderAdapterHolder(View view) {
            super(view);
            sliderImage = view.findViewById(R.id.sliderImage);
        }
    }

    public interface OnSliderClickListner {
        public void onSliderClick(Slider slider, int position);
    }
}
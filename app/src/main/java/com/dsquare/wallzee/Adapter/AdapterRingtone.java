package com.dsquare.wallzee.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsquare.wallzee.Activity.PlayActivity;
import com.dsquare.wallzee.Activity.RingtoneActivity;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.Model.RingtoneType;
import com.dsquare.wallzee.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class AdapterRingtone extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Ringtone> items;

    MediaCallBack callBack;

    private Context context;
    private OnItemClickListener mOnItemClickListener;

    static MediaPlayer mediaPlayer;

    ArrayList<Integer> bg;

    private boolean isPlaying = false;
    int currentlyPlayingPosition = -1; // Variable to keep track of the currently playing position

    public interface OnItemClickListener {
        void onItemClick(View view, Ringtone obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRingtone(Context context, List<Ringtone> items,MediaCallBack callBack) {
        this.items = items;
        this.callBack = callBack;
        this.context = context;
        initBackground();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView category_name;

        public ImageView playIcon;
        public ProgressBar progressBar;
        public RelativeLayout lyt_parent;
        public LinearLayout llBackground;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.tvCategory);
            playIcon = v.findViewById(R.id.ivCategory);
            progressBar = v.findViewById(R.id.progressBar);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            llBackground = v.findViewById(R.id.llBackground);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ringtone, parent, false);
        return new OriginalViewHolder(v);
    }

    private void initBackground() {
        bg = new ArrayList<>();
        bg.add(R.drawable.one);
        bg.add(R.drawable.two);
        bg.add(R.drawable.three);
        bg.add(R.drawable.foure);
        bg.add(R.drawable.five);
        bg.add(R.drawable.six);
        bg.add(R.drawable.seven);
        bg.add(R.drawable.eight);
        bg.add(R.drawable.nine);
        bg.add(R.drawable.ten);
        bg.add(R.drawable.eleven);
        bg.add(R.drawable.twelve);
        bg.add(R.drawable.thirteen);
        bg.add(R.drawable.fourteen);
        bg.add(R.drawable.fifthteen);
        bg.add(R.drawable.sixteen);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Ringtone c = items.get(position);
        final OriginalViewHolder vItem = (OriginalViewHolder) holder;
        vItem.category_name.setText(c.ringtone_name);

        int drawableIndex = position % bg.size();
        int drawableResId = bg.get(drawableIndex);
        vItem.llBackground.setBackground(context.getDrawable(drawableResId));

        vItem.llBackground.setOnClickListener(view -> {
            vItem.progressBar.setVisibility(View.GONE);
            vItem.playIcon.setImageResource(R.drawable.round_play_circle_outline_24);
            stopMediaPlayer();
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("url", c.ringtone_url);
            intent.putExtra("name", c.ringtone_name);
            context.startActivity(intent);
        });

        MediaPlayer.OnCompletionListener mCompletionListener = mp -> {
            mp.release();
            mediaPlayer = null;
            currentlyPlayingPosition = -1; // Reset currentlyPlayingPosition
            notifyDataSetChanged(); // Notify the adapter to update the UI
        };
        vItem.playIcon.setOnClickListener(view -> {
            currentlyPlayingPosition = holder.getAdapterPosition();
            if (holder.getAdapterPosition() == currentlyPlayingPosition) {
                vItem.progressBar.setVisibility(View.VISIBLE);
                vItem.playIcon.setImageResource(R.drawable.pause_icon);
                c.type = RingtoneType.CURRENT;
//                Toast.makeText(context, "Current Play Name : -"+c.ringtone_name, Toast.LENGTH_SHORT).show();
            } else {
                vItem.progressBar.setVisibility(View.GONE);
                vItem.playIcon.setImageResource(R.drawable.round_play_circle_outline_24);
                c.type = RingtoneType.OTHERS;
//                Toast.makeText(context, "Current Pause Name : -"+c.ringtone_name, Toast.LENGTH_SHORT).show();
            }

            if (mediaPlayer != null && currentlyPlayingPosition != holder.getAdapterPosition()) {
                // Stop the previously playing audio if it exists and is not the current item
                mediaPlayer.release();
                mediaPlayer = null;
                currentlyPlayingPosition = -1;
                vItem.playIcon.setImageResource(R.drawable.round_play_circle_outline_24);
            }

            if (mediaPlayer == null) {
                // Start playing the audio for the clicked item
                vItem.progressBar.setVisibility(View.VISIBLE);
                vItem.playIcon.setImageResource(R.drawable.pause_icon);
                mediaPlayer = MediaPlayer.create(holder.itemView.getContext(), Uri.parse(Config.WEBSITE_URL + "ringtone/" + c.ringtone_url));

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(mCompletionListener);
                    currentlyPlayingPosition = holder.getAdapterPosition(); // Set currentlyPlayingPosition
                }


            } else {
                // Pause or stop the audio if it's the currently playing item
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    vItem.playIcon.setImageResource(R.drawable.round_play_circle_outline_24);
                    stopMediaPlayer();
                    vItem.progressBar.setVisibility(View.GONE);
                } else {
                    mediaPlayer.start();
                    vItem.playIcon.setImageResource(R.drawable.pause_icon);
                }
            }
            callBack.onChangeMedia(mediaPlayer);
        });


        if (holder.getAdapterPosition() == currentlyPlayingPosition) {
            vItem.progressBar.setVisibility(View.VISIBLE);
            vItem.playIcon.setImageResource(R.drawable.pause_icon);
        } else {
            vItem.progressBar.setVisibility(View.GONE);
            vItem.playIcon.setImageResource(R.drawable.round_play_circle_outline_24);
        }


//        if (isPlaying) {
//                    // Stop the media player
//                    mediaPlayer.pause();
//                    vItem.progressBar.setVisibility(View.GONE);
//                    vItem.playIcon.setImageResource(R.drawable.round_play_circle_outline_24);
//                } else {
//                    vItem.progressBar.setVisibility(View.VISIBLE);
//                    vItem.playIcon.setImageResource(R.drawable.pause_icon);
//                    try {
//                        // Set data source only if the media player is not playing
//                        mediaPlayer.reset(); // Reset the media player to its uninitialized state
//                        mediaPlayer.setDataSource(Config.WEBSITE_URL+ "ringtone/"+c.ringtone_url);
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
//                        isPlaying = true;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }


        vItem.lyt_parent.setOnClickListener(view -> {

            Intent intent = new Intent(context, RingtoneActivity.class);
            intent.putExtra("name", c.ringtone_name);
            intent.putExtra("url", c.ringtone_url);
            context.startActivity(intent);


            // if (mOnItemClickListener != null) {
            //      mOnItemClickListener.onItemClick(view, c, position);
            //  }
        });
    }

    // Method to stop the currently playing audio
    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.pause();
            mediaPlayer.release();
            mediaPlayer = null;
            currentlyPlayingPosition = -1;
        }
    }
    private int getBackgroundDrawable(int position) {
        // Calculate the index of the background drawable to use
        int bgIndex = position % 16; // Assuming you have 16 drawables
        // Return the resource ID of the background drawable
        return context.getResources().getIdentifier("background_" + (bgIndex + 1), "drawable", context.getPackageName());
    }

    public void setListData(List<Ringtone> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface MediaCallBack{
     public void onChangeMedia(MediaPlayer player);
    }
}
package com.dsquare.wallzee.Adapter;

import static com.dsquare.wallzee.Config.LOAD_MORE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dsquare.wallzee.Activity.WallpaperDetailsActivity;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Wallpaper;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Utils.AdsManager;
import com.dsquare.wallzee.Utils.AdsPref;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.ymg.ads.sdk.ui.SmallNativeAdView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdapterRecent extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    String TAG = AdapterRecent.class.getSimpleName();
    private final int VIEW_AD = 2;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Wallpaper> items;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private Wallpaper pos;
    private CharSequence charSequence = null;

    private AdsPref adsPref;
    private AdsManager adsManager;

    boolean scrolling = false;

    public interface OnItemClickListener {
        void onItemClick(View view, Wallpaper obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterRecent(Context context, RecyclerView view, List<Wallpaper> items) {
        this.items = items;
        this.context = context;
        adsPref = new AdsPref(context);
        adsManager = new AdsManager((Activity) context);
        lastItemViewDetector(view);
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    scrolling = true;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrolling = false;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        public TextView tvCategory;
        public ImageView playIcon;
        public ImageView ivCategory;

        public OriginalViewHolder(View v) {
            super(v);
            playIcon = v.findViewById(R.id.playIcon);
            cardView = v.findViewById(R.id.cardView);
            ivCategory = v.findViewById(R.id.ivWallpaper);
            tvCategory = v.findViewById(R.id.tvWallpaper);
        }

    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

        SmallNativeAdView mediumNativeAdView;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            //admob native ad

            mediumNativeAdView = itemView.findViewById(R.id.mediumNativeAdView);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.load_more);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wallpaper, parent, false);
            vh = new OriginalViewHolder(v);

        } else if (viewType == VIEW_AD) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ads, parent, false);
            vh = new AdViewHolder(v);

        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OriginalViewHolder) {
            final Wallpaper p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;
            if (p.getType().contains("gif")) {
                vItem.playIcon.setVisibility(View.VISIBLE);
                vItem.tvCategory.setText(p.getImage_name());
                Log.d("TEST", "onBindViewHolder: this is gif" + (p.getType()));
                Glide.with(context)
                        .asGif()
                        .load(Config.WEBSITE_URL + "images/thumbs/" + p.getImage())
                        .placeholder(R.drawable.placeholder)
                        .into(vItem.ivCategory);
            } else if (p.getType().contains("mp4")) {
                vItem.playIcon.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(Config.WEBSITE_URL + "images/thumbs/" + p.getImage())
                        .placeholder(R.drawable.placeholder)
                        .into(vItem.ivCategory);
            } else {
                vItem.playIcon.setVisibility(View.GONE);
                Log.d("TEST", "onBindViewHolder: this is photo or png" + (p.getImage()));
                vItem.tvCategory.setText(p.getImage_name());
                Glide.with(context)
                        .load(Config.WEBSITE_URL + "images/thumbs/" + p.getImage())
                        .placeholder(R.drawable.placeholder)
                        .into(vItem.ivCategory);
            }

            vItem.cardView.setOnClickListener(view -> {
                Wallpaper item = items.get(position);
                final Intent intshow = new Intent(context, WallpaperDetailsActivity.class);
                Log.d("AdapterRecent", "onBindViewHolder: " + item.type);
                if (item.type.contains("mp4")) {
                    Log.d("TAG", "Click item: video item called");
                    intshow.putExtra("liveItem", (Serializable) item);
                    intshow.putExtra("TYPE", "live");
                } else if (item.type.contains("gif")) {
                    Log.d("TAG", "Click item: gif item called");
                    intshow.putExtra("liveItem", (Serializable) item);
                    intshow.putExtra("TYPE", "gif");
                } else {
                    Log.d("TAG", "Click item: Wallpapers item called");
                    intshow.putExtra("POSITION", position);
                    intshow.putExtra("array", (Serializable) items);
                    intshow.putExtra("TYPE", "wallpaper");
                }
                context.startActivity(intshow);
            });
        } else if (holder instanceof AdViewHolder) {

            final AdViewHolder vItem = (AdViewHolder) holder;

            adsManager.loadNativeAdView(vItem.mediumNativeAdView, true, "default");

            //vItem.bindNativeAd(context);


        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

        if (getItemViewType(position) == VIEW_PROG || getItemViewType(position) == VIEW_AD) {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        } else {
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(false);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

    @Override
    public int getItemViewType(int position) {
        Wallpaper channel = items.get(position);
        if (channel != null) {
            if (channel.image_name == null || channel.image.equals("")) {
                return VIEW_AD;
            }
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Wallpaper> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void insertDataWithNativeAd(List<Wallpaper> items) {
        setLoaded();
        int positionStart = getItemCount();

        if (items.size() >= adsPref.getNativeAdIndex()) {
            items.add(adsPref.getNativeAdIndex(), new Wallpaper());
        }

        // Filter out duplicate items
        ArrayList<Wallpaper> newItems = new ArrayList<>();
        for (Wallpaper wallpaper : items) {
            boolean isDuplicate = false;
            for (Wallpaper subWallPaper : this.items) {
                if (wallpaper.getId().equals(subWallPaper.getId())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                newItems.add(wallpaper);
            }
        }
        this.items.addAll(newItems);
        Log.d("itemSize","SIZE :"+this.items.size());
        for (Wallpaper post :  this.items) {
            Log.d("item", "TITLE: " + post.getId());
        }

        notifyItemRangeInserted(positionStart, newItems.size());
    }


    private void lastItemViewDetector(RecyclerView recyclerView) {

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        int[] lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null);
                        int lastPos = getLastVisibleItem(lastVisibleItemPositions);

                        if (!loading && lastPos >= getItemCount() - 1 && onLoadMoreListener != null) {
                            loading = true;
                            int currentPage = getItemCount() / LOAD_MORE;
                            onLoadMoreListener.onLoadMore(currentPage);
                        }
                    }
                }
            });
        }


    }


    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    public List<Wallpaper> getList(){
        return items;
    }

    private int getLastVisibleItem(int[] into) {
        int last_idx = into[0];
        for (int i : into) {
            if (last_idx < i) last_idx = i;
        }
        return last_idx;
    }

}
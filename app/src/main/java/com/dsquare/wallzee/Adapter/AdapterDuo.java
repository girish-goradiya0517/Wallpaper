package com.dsquare.wallzee.Adapter;

import static com.dsquare.wallzee.Config.LOAD_MORE;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Duo;
import com.dsquare.wallzee.Model.Wallpaper;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Utils.Constant;

import java.io.IOException;
import java.util.List;

public class AdapterDuo extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_AD = 2;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private List<Duo> items;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private Duo pos;
    private CharSequence charSequence = null;


    boolean scrolling = false;

    public interface OnItemClickListener {
        void onItemClick(View view, Duo obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterDuo(Context context, RecyclerView view, List<Duo> items) {
        this.items = items;
        this.context = context;

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
        public TextView applyWallpaper;
        public ImageView ivWallpaper1;
        public ImageView ivWallpaper2;

        private ViewFlipper viewFlipper;
        private Animation slideUpAnimation;
        private Animation fadeInAnimation;

        private Handler handler = new Handler();
        private Handler handlerAgain = new Handler();
        private Handler handlerLock = new Handler();


        public OriginalViewHolder(View v) {
            super(v);

            ivWallpaper1 = v.findViewById(R.id.img1);
            ivWallpaper2 = v.findViewById(R.id.img2);
            applyWallpaper = v.findViewById(R.id.setWallpaper);


            viewFlipper = v.findViewById(R.id.viewFlipper);

        }

    }

    public class AdViewHolder extends RecyclerView.ViewHolder {

//        SmallNativeAdView mediumNativeAdView;

        public AdViewHolder(@NonNull View itemView) {
            super(itemView);
            //admob native ad

            // = itemView.findViewById(R.id.mediumNativeAdView);

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

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doubles, parent, false);
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

            final Duo p = items.get(position);
            final OriginalViewHolder vItem = (OriginalViewHolder) holder;
            String url = Config.WEBSITE_URL + "images/double/";
            Log.d("WALL1-URL", "WALLP1: " + url + p.getWall1());
            Log.d("WALL2-URL", "WALLP2 " + url + p.getWall2());
            Glide.with(context)
                    .load(url + p.getWall2())
                    .placeholder(R.drawable.placeholder)
                    .into(vItem.ivWallpaper1);

            Glide.with(context)
                    .load(url + p.getWall1())
                    .placeholder(R.drawable.placeholder)
                    .into(vItem.ivWallpaper2);

            showAnimation(vItem);

            vItem.applyWallpaper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "Please Wait...", Toast.LENGTH_SHORT).show();
                    setWallpaper(p.getWall2());
                    setWallpaperLock(p.getWall1());
                }
            });

        } else if (holder instanceof AdViewHolder) {

            final AdViewHolder vItem = (AdViewHolder) holder;


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

    private void showAnimation(OriginalViewHolder holder) {
        holder.slideUpAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
        holder.fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);

        holder.viewFlipper.setInAnimation(holder.slideUpAnimation);
        holder.viewFlipper.setOutAnimation(holder.fadeInAnimation);

        holder.viewFlipper.setFlipInterval(500);
        holder.viewFlipper.showNext();
        // Set a delay to hide the ViewFlipper after 5 seconds
        holder.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                holder.viewFlipper.setVisibility(View.GONE);
                //mainImageView.setVisibility(View.VISIBLE);
                showAnimatationAgain(holder);
            }
        }, 500);
    }

    private void showAnimatationAgain(OriginalViewHolder holder) {
        holder.handlerAgain.postDelayed(new Runnable() {
            @Override
            public void run() {
                showLockAgain(holder);
            }
        }, 4000);
    }

    private void showLockAgain(OriginalViewHolder holder) {
        holder.viewFlipper.setVisibility(View.VISIBLE);
        holder.handlerLock.postDelayed(new Runnable() {
            @Override
            public void run() {
                showAnimation(holder);
            }
        }, 2000);
    }

    private void setWallpaper(String img1) {
        Glide.with(context)
                .asBitmap()
                .load(Config.WEBSITE_URL + "/images/double/" + img1)
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Home Wallpaper was set successfully", Toast.LENGTH_SHORT).show();
                                }
                            }, Constant.DELAY_SET_WALLPAPER);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {


                    }
                });
    }


    private void setWallpaperLock(String img2) {
        Glide.with(context)
                .asBitmap()
                .load(Config.WEBSITE_URL + "/images/double/" + img2)
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Lock Wallpaper was set successfully", Toast.LENGTH_SHORT).show();
                                }
                            }, Constant.DELAY_SET_WALLPAPER);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {


                    }
                });
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
        Duo channel = items.get(position);
        if (channel != null) {
            if (channel.wall1 == null || channel.wall2.equals("")) {
                return VIEW_AD;
            }
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Duo> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public List<Duo> getList() {
        return items;
    }

//    public void insertDataWithNativeAd(List<Wallpaper> items) {
//        setLoaded();
//        int positionStart = getItemCount();
//        for (Wallpaper post : items) {
//            Log.d("item", "TITLE: " + post.wallpaper_image);
//        }
//        if (items.size() >= adsPref.getNativeAdIndex()) {
//            if (2 == 4) {
//                items.add(4, new Channel());
//            } else if (3 == 3) {
//                items.add(adsPref.getNativeAdIndex(), new Channel());
//            } else {
//                items.add(adsPref.getNativeAdIndex(), new Channel());
//            }
//        }
//        int itemCount = items.size();
//        this.items.addAll(items);
//        notifyItemRangeInserted(positionStart, itemCount);
//    }

    private void lastItemViewDetector(RecyclerView recyclerView) {

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = getLastVisibleItem(layoutManager.findLastVisibleItemPositions(null));
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        int current_page = getItemCount() / (LOAD_MORE);
                        onLoadMoreListener.onLoadMore(current_page);
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


    private int getLastVisibleItem(int[] into) {
        int last_idx = into[0];
        for (int i : into) {
            if (last_idx < i) last_idx = i;
        }
        return last_idx;
    }

}
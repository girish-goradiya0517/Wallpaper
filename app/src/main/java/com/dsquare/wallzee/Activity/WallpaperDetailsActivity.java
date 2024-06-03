package com.dsquare.wallzee.Activity;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Datebase.DBHelper;
import com.dsquare.wallzee.Model.Wallpaper;
import com.dsquare.wallzee.Model.responseReport;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.ClickableViewPager;
import com.dsquare.wallzee.Utils.Constant;
import com.dsquare.wallzee.Utils.DocumentUtils;
import com.dsquare.wallzee.Utils.Methods;
import com.dsquare.wallzee.Utils.PrefManager;
import com.dsquare.wallzee.Utils.ReportDialogue;
import com.dsquare.wallzee.Utils.VideoLiveWallpaperService;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import alirezat775.lib.downloader.Downloader;
import alirezat775.lib.downloader.core.OnDownloadListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperDetailsActivity extends AppCompatActivity implements ReportDialogue.ReportDialogelistner {

    Boolean isOnce = true;
    ReportDialogue reportDialoge;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    LinearLayout lytAction;
    Handler handler;
    ImageView itemGif;

    ExoPlayer player;
    VideoView detailPlayer;
    private int currentPosition = 0;
    private static int TOTAL_TEXT;
    ClickableViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;
    ArrayList<Wallpaper> arrayList;
    RelativeLayout relativeLayoutLoadMore;
    RelativeLayout rootLayout;
    File filePath;
    AlertDialog alertDialog1;
    AlertDialog alertDialog;
    Methods methodsAll;
    //DBHelper dbHelper;
    ImageButton tvApply;
    TextView tvWallpaperViews;
    TextView tvWallpaperDownloads;
    TextView tvWallpaperSets;
    TextView tvUserName;
    ImageView ivUser;
    TextView tvWallpaperSize;
    TextView tvWallpaperResolution;

    ImageView ivImageView;
    ImageView imageView;
    ImageView btnFavorite;
    ImageButton btnShare;
    ImageButton btnDownload;
    ImageButton btnInfo;
    PrefManager prefManager;
    LinearLayout userProfileLayout;
    private final String TAG = WallpaperDetailsActivity.class.getSimpleName();
    CharSequence[] values = {" Set Wallpaper ", " Crop Wallpaper "};
    CharSequence[] names = {" Home Screen ", " Lock Screen ", " Both Screen "};
    private int STORAGE_PERMISSION_CODE = 1;
    RelativeLayout shareLayout;

    Wallpaper liveItemMain;
    RelativeLayout downloadLayout;
    LinearLayout reportLayout;

    DBHelper dbHelper;


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = new DBHelper(this);
        handler = new Handler(getMainLooper());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        //dbHelper = new DBHelper(this);
        methodsAll = new Methods(this);
        prefManager = new PrefManager(this);
        reportDialoge = new ReportDialogue(this, this, this);
        lytAction = findViewById(R.id.lyt_action);
        viewPager = findViewById(R.id.viewPager);
        tvApply = findViewById(R.id.tvApply);
        btnFavorite = findViewById(R.id.btn_favorite);
        btnShare = findViewById(R.id.btnShare);
        btnDownload = findViewById(R.id.btnDownload);
        btnInfo = findViewById(R.id.btnInfo);
        relativeLayoutLoadMore = findViewById(R.id.relativeLayoutLoadMore);
        rootLayout = findViewById(R.id.rootLayout);

        tvWallpaperViews = findViewById(R.id.tvWallpaperViews);
        tvWallpaperDownloads = findViewById(R.id.tvWallpaperDownloads);
        tvWallpaperSets = findViewById(R.id.tvWallpaperSets);
        tvWallpaperSize = findViewById(R.id.tvWallpaperSize);
        tvWallpaperResolution = findViewById(R.id.tvWallpaperResolution);

        reportLayout = findViewById(R.id.reportLayout);


        tvUserName = findViewById(R.id.tvUserName);
        ivUser = findViewById(R.id.ivUser);
        userProfileLayout = findViewById(R.id.userProfileLayout);
        imageView = findViewById(R.id.iv_full);
        ivImageView = findViewById(R.id.ivImageView);
        shareLayout = findViewById(R.id.shareLayout);
        downloadLayout = findViewById(R.id.downloadLayout);
        String type = getIntent().getStringExtra("TYPE");
        Intent i = getIntent();
        arrayList = (ArrayList<Wallpaper>) i.getSerializableExtra("array");
        if (type != null) {
            if (type.equals("live")) {
                liveItemMain = (Wallpaper) i.getSerializableExtra("liveItem");
                String url = Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage();
                relativeLayoutLoadMore.setVisibility(View.VISIBLE);
                ivImageView.setVisibility(View.VISIBLE);
                Glide.with(this).asBitmap().load(url).into(ivImageView);
                Log.d(TAG, "onCreate: item Id :-" + liveItemMain.toString());
                detailPlayer = findViewById(R.id.detailPlayer);
                detailPlayer.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                detailPlayer.setOnClickListener(listener);
                new LoadVideoTask().execute(url);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                android.widget.RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) detailPlayer.getLayoutParams();
                params.width = metrics.widthPixels;
                params.height = metrics.heightPixels;
                params.leftMargin = 0;
                detailPlayer.setScaleY(1.5f);
                detailPlayer.setLayoutParams(params);
                detailPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.d(TAG, "ErrorListener: what " + what + "and extra" + extra);
                    return false;
                });
                detailPlayer.setOnPreparedListener(mediaPlayer -> {
                    ivImageView.setVisibility(View.GONE);
                    relativeLayoutLoadMore.setVisibility(View.GONE);
                    mediaPlayer.start();
//                    float videoRatio = (float) mediaPlayer.getVideoWidth() / mediaPlayer.getVideoHeight();
//                    float screenRatio = (float) detailPlayer.getWidth() / detailPlayer.getHeight();
//                    float scale = videoRatio / screenRatio;
//                    if (scale >= 1) {
//                        detailPlayer.setScaleX(scale);
//                    } else {
//                        detailPlayer.setScaleY(1f / scale);
//                    }
                });
                detailPlayer.setOnCompletionListener(mediaPlayer -> {
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                });
                viewPager.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                favToggle(liveItemMain);
                loadBottomSheetData();
            } else if (type.equals("gif")) {
                liveItemMain = (Wallpaper) i.getSerializableExtra("liveItem");
                String url = Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage();
                Log.d(TAG, "onCreate: item :" + liveItemMain.toString());
                itemGif = findViewById(R.id.iv_full);
                itemGif.setOnClickListener(listener);
                Log.d(TAG, "onCreate: item is gif  ->" + liveItemMain.getType());
                imageView.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(itemGif);
                favToggle(liveItemMain);
                loadBottomSheetData();
            } else {
//                Toast.makeText(this, "this is upload", Toast.LENGTH_SHORT).show();
                viewPager.setVisibility(View.VISIBLE);
                customPagerAdapter = new CustomPagerAdapter(WallpaperDetailsActivity.this);
                currentPosition = i.getIntExtra("POSITION", 0);
                arrayList = (ArrayList<Wallpaper>) i.getSerializableExtra("array");
                TOTAL_TEXT = (arrayList.size() - 1);
                viewPager.setAdapter(customPagerAdapter);
                viewPager.setCurrentItem(currentPosition, true);
                viewPager.getAdapter().notifyDataSetChanged();
                viewPager.setOffscreenPageLimit(0);
            }
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPosition = position;
                changePreviewText(position);
                favToggle(arrayList.get(currentPosition));
                Log.d(TAG, "onPageScrolled: image id " + arrayList.get(position).image_id);
                loadBottomSheetData();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("scroll", "PageScrollStateChanged");
            }
        });

        //apply wallpaper
        tvApply.setOnClickListener(v -> {
            applyFunction();
        });

        //favorite wallpaper
        btnFavorite.setOnClickListener(v -> {
            if (liveItemMain != null) {
                if (dbHelper.isFavoritesExist(liveItemMain.image_id)) {
                    dbHelper.deleteFavorites(liveItemMain);
                    Toast.makeText(WallpaperDetailsActivity.this, getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addOneFavorite(liveItemMain);
                    Toast.makeText(WallpaperDetailsActivity.this, getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                }
                favToggle(liveItemMain);
            } else {
                if (dbHelper.isFavoritesExist(arrayList.get(currentPosition).image_id)) {
                    dbHelper.deleteFavorites(arrayList.get(currentPosition));
                    Toast.makeText(WallpaperDetailsActivity.this, getResources().getString(R.string.favorite_removed), Toast.LENGTH_SHORT).show();
                } else {
                    dbHelper.addOneFavorite(arrayList.get(currentPosition));
                    Toast.makeText(WallpaperDetailsActivity.this, getResources().getString(R.string.favorite_added), Toast.LENGTH_SHORT).show();
                }
                favToggle(arrayList.get(currentPosition));
            }
        });

        //share wallpaper
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWallpaper();
                if (methodsAll.isNetworkAvailable()) {
                    // new MyTask().execute(Constant.URL_SHARE_COUNT + arrayList.get(currentPosition).getImage_id());
                }
            }
        });

        //download wallpaper
        btnDownload.setOnClickListener(v -> {
            Wallpaper current;
            if (liveItemMain != null) {
                current = liveItemMain;
            } else {
                current = arrayList.get(currentPosition);
            }
            if (verifyPermissions()) {
                if (!current.getType().contains("gif") && !current.getType().contains("mp4")) {
                    downloadBitmap();
                } else if (current.getType().contains("gif")) {
                    downloadGIfs(Config.WEBSITE_URL + "/images/wallpaper/" + current.getImage());
                } else {
                    downloadLiveWallpaper(Config.WEBSITE_URL + "/images/wallpaper/" + current.getImage());
                }
//                if (methodsAll.isNetworkAvailable()) {
//                    //new MyTask().execute(Constant.URL_DOWNLOAD_COUNT + arrayList.get(currentPosition).getImage_id());
//                }
            }
        });

        loadBottomSheetData();
        initBottomSheet();
    }

    private void showCustomDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_set_wallpaper, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // Initialize LinearLayouts
        LinearLayout layout1 = dialogView.findViewById(R.id.layout_homeSet);
        LinearLayout layout2 = dialogView.findViewById(R.id.layout_homeLockSet);
        LinearLayout layout3 = dialogView.findViewById(R.id.layout_lockSet);


        layout1.setOnClickListener(v -> {
            dialog.dismiss();
            dialog.hide();
            setWallpaperToHomeScreen();
        });

        layout2.setOnClickListener(v -> {
            dialog.dismiss();
            dialog.hide();
            setWallpaperToBothScreen();
        });

        layout3.setOnClickListener(v -> {
            dialog.dismiss();
            dialog.hide();
            setWallpaperToLockScreen();
        });
        dialog.show();
        dialog.setCancelable(true);
    }


    private void applyFunction() {
        if (liveItemMain != null) {
            if (liveItemMain.type.equals("upload")) {
                Log.d(TAG, "applyFunction: showSetUsOption gif for live -- ");
                showSetUsOption();
            } else {
                Log.d(TAG, "applyFunction: set gif for live -- ");
                setGif(Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage());
            }
        } else {
            if (arrayList.get(currentPosition).type.equals("upload")) {
                Log.d(TAG, "applyFunction: showSetUsOption wallpaper for live -- ");
                showSetUsOption();
            } else {
                Log.d(TAG, "applyFunction: set wallpaper for live -- ");
                setGif(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage());
            }
        }

        if (verifyPermissions()) {
            if (liveItemMain != null) {
                if (liveItemMain.type.equals("upload")) {
                    showSetUsOption();
                } else {
                    setGif(Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage());
                }
            } else {
                if (arrayList.get(currentPosition).type.equals("upload")) {
                    showSetUsOption();
                } else {
                    setGif(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage());
                }
            }
            if (methodsAll.isNetworkAvailable()) {
                //new MyTask().execute(Constant.URL_SET_COUNT + arrayList.get(currentPosition).getId());
            }
        }

    }

    private void favToggle(Wallpaper wallpaper2) {
        if (this.dbHelper.isFavoritesExist(wallpaper2.image_id)) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_fill);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void downloadGIfs(String imageURL) {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .download(imageURL)
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Methods.saveImage(WallpaperDetailsActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "image/gif");

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
                                Toast.makeText(WallpaperDetailsActivity.this, "Download Live Wallpaper Successfully", Toast.LENGTH_SHORT).show();

                            }, 2000);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();

    }

    private void downloadLiveWallpaper(String liveWallpaperUrl) {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .download(liveWallpaperUrl)
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Methods.saveVideo(WallpaperDetailsActivity.this, Methods.getBytesFromFile(resource), Methods.createName(liveWallpaperUrl), Methods.getMimeType(resource.getPath()));
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
                                Toast.makeText(WallpaperDetailsActivity.this, "Download Live Wallpaper Successfully", Toast.LENGTH_SHORT).show();
                            }, 3000);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();
    }

    private void saveLiveWallpaper(File resource, String liveWallpaperUrl) throws IOException {
        Methods.saveVideo(WallpaperDetailsActivity.this, Methods.getBytesFromFile(resource), Methods.createName(liveWallpaperUrl), Methods.getMimeType(resource.getPath()));
    }

    private void handleDownloadError(Exception e) {
        Log.e(TAG, "Download failed:", e);
        relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
        // Display appropriate error message to the user
    }

    private void showDownloadSuccessMessage() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            relativeLayoutLoadMore.setVisibility(View.INVISIBLE);
            Toast.makeText(WallpaperDetailsActivity.this, "Live Wallpaper Downloaded Successfully", Toast.LENGTH_SHORT).show();
        }, 2000);
    }

    public Boolean verifyPermissions() {
        if (Build.VERSION.SDK_INT >= 32) {
            return true;
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "verifyPermissions: permission is Accessed");
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d(TAG, "verifyPermissions: permission is not Accessed");
            return false;
        }
    }


    //load bottom sheet data
    private void loadBottomSheetData() {
        if (methodsAll.isNetworkAvailable()) {
            //new MyTask().execute(Constant.URL_VIEW_COUNT + arrayList.get(currentPosition).getImage_id());
        }
        if (liveItemMain != null) {
            tvWallpaperViews.setText(Methods.withSuffix((long) liveItemMain.view_count));
            tvWallpaperDownloads.setText(Methods.withSuffix((long) liveItemMain.download_count));
            tvWallpaperSets.setText(Methods.withSuffix((long) liveItemMain.set_count));
            tvUserName.setText(liveItemMain.creatorName);
            Glide.with(this)
                    .load(liveItemMain.creatorProfileUrl)
                    .into(ivUser);
            userProfileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WallpaperDetailsActivity.this, ProfileActivity.class);
                    intent.putExtra("userImage", liveItemMain.creatorProfileUrl);
                    intent.putExtra("tvUserName", liveItemMain.creatorName);
                    intent.putExtra("userId", liveItemMain.user_id);
                    startActivity(intent);
                }
            });
            Glide.with(this)
                    .asBitmap()
                    .load(Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //Bitmap resource = ((BitmapDrawable) ivImageView.getDrawable()).getBitmap();
                            int width = resource.getWidth();
                            int height = resource.getHeight();

                            tvWallpaperResolution.setText(width + " x " + height);

                            double kbBitmapSize = resource.getByteCount() / 1024;
                            tvWallpaperSize.setText(kbBitmapSize + "KB");
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            if (liveItemMain.image.contains(".gif")) {
                getDetails("gif");
            } else if (liveItemMain.image.contains(".mp4")) {
                getDetails("mp4");
            } else {
                getDetails("jpg/png");
            }

        } else {
            tvWallpaperViews.setText(Methods.withSuffix((long) arrayList.get(currentPosition).view_count));
            tvWallpaperDownloads.setText(Methods.withSuffix((long) arrayList.get(currentPosition).download_count));
            tvWallpaperSets.setText(Methods.withSuffix((long) arrayList.get(currentPosition).set_count));
            tvUserName.setText(arrayList.get(currentPosition).creatorName);
            Glide.with(this)
                    .load(arrayList.get(currentPosition).creatorProfileUrl)
                    .into(ivUser);
            userProfileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WallpaperDetailsActivity.this, ProfileActivity.class);
                    intent.putExtra("userImage", arrayList.get(currentPosition).creatorProfileUrl);
                    intent.putExtra("tvUserName", arrayList.get(currentPosition).creatorName);
                    intent.putExtra("userId", arrayList.get(currentPosition).user_id);
                    startActivity(intent);
                }
            });
            Glide.with(this)
                    .asBitmap()
                    .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //Bitmap resource = ((BitmapDrawable) ivImageView.getDrawable()).getBitmap();
                            int width = resource.getWidth();
                            int height = resource.getHeight();

                            tvWallpaperResolution.setText(width + " x " + height);

                            double kbBitmapSize = resource.getByteCount() / 1024;
                            tvWallpaperSize.setText(kbBitmapSize + "KB");
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

            if (arrayList.get(currentPosition).image.contains(".gif")) {
                getDetails("gif");
            } else if (arrayList.get(currentPosition).image.contains(".mp4")) {
                getDetails("mp4");
            } else {
                getDetails("jpg/png");
            }
        }


    }

    public Object getDetails(String ff) {
        return ff;
    }

    @Override
    public void onSubmitClick(String data) {
        String userId = "";
        String imageid = "";
        if (liveItemMain != null) {
            userId = liveItemMain.user_id;
            imageid = liveItemMain.getId();
        } else {
            userId = arrayList.get(currentPosition).user_id;
            imageid = arrayList.get(currentPosition).getId();
        }
        Log.d(TAG, "onSubmitClick: request Data -> userID :" + userId + "\n Imageid :" + imageid + "\n Message :" + data);
        if (userId != null && imageid != null && data != null) {
            RestAdapter.createAPI(Config.WEBSITE_URL).reportWallpaper(
                    "YMG-Developers",
                    imageid, userId, data, "1"
            ).enqueue(new Callback<responseReport>() {
                @Override
                public void onResponse(Call<responseReport> call, Response<responseReport> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "onResponse: Response" + response.body());
                        Toast.makeText(WallpaperDetailsActivity.this, "Report Sent SuccessFully", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<responseReport> call, Throwable t) {
                    Toast.makeText(WallpaperDetailsActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "invalid Request", Toast.LENGTH_SHORT).show();
        }
    }



class CustomPagerAdapter extends PagerAdapter {

    Context mContext;

    LayoutInflater mLayoutInflater;

    CustomPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LinearLayout ll = null;
        String url = Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(position).getImage();
        Log.d(TAG, "instantiateItem: print Url" + url);
      
        try{
            View itemView = mLayoutInflater.inflate(R.layout.item_viewpager, container, false);
            ll = itemView.findViewById(R.id.ll_viewpager);
        imageView = itemView.findViewById(R.id.iv_full);
        final Wallpaper p = arrayList.get(position);
        Log.d(TAG, "instantiateItem: item is wallpaper  ->" + arrayList.get(position).getType());
        Glide.with(mContext)
                .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(position).getImage())
                .placeholder(R.drawable.placeholder)
                .into(imageView);

        imageView.setOnClickListener(listener);
        container.addView(ll, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        catch (Exception e){
            Log.d(TAG,"Exception : "+e);
        }
        return ll;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}


    //view pager on swipe
    public void changePreviewText(int position) {
        currentPosition = position;
        Log.d("Main", "Current position: " + position);
    }

    //show bottom layout
    private void initBottomSheet() {

        RelativeLayout relativeLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(relativeLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ((RelativeLayout) findViewById(R.id.lyt_expand)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });


        reportLayout.setOnClickListener(view -> showReportDialog());

        btnInfo.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    View.OnClickListener listener = v -> {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            getSupportActionBar().hide();
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            getSupportActionBar().show();
        }
    };

    public void showReportDialog() {
        reportDialoge.showDialogue();
    }

    private void handleReportSubmission() {
        // Implement the logic for handling the report submission
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            String selectedReason = radioButton.getText().toString();

            Toast.makeText(this, selectedReason + "", Toast.LENGTH_SHORT).show();
        }
    }

    //share wallpaper code
    private void shareWallpaper() {
        if (liveItemMain != null) {
            if (liveItemMain.type.contains("upload")) {
                Glide.with(this)
                        .asBitmap()
                        .load(Config.WEBSITE_URL + "/images/thumbs/" + liveItemMain.getImage())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("image/*");
                                String shareBodyText = "https://play.google.com/store/apps/details?id=" + getPackageName();
                                intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                intent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
                            }
                        });
            } else {
                shareGIfs(Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage());
            }
        } else {
            if (arrayList.get(currentPosition).type.contains("upload")) {
                Glide.with(this)
                        .asBitmap()
                        .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("image/*");
                                String shareBodyText = "https://play.google.com/store/apps/details?id=" + getPackageName();
                                intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(resource));
                                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                                intent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                                startActivity(Intent.createChooser(intent, getResources().getString(R.string.app_name)));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            } else {
                shareGIfs(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage());
            }
        }
    }

    private void shareGIfs(String imageURL) {
        Glide.with(this)
                .download(imageURL.replace(" ", "%20"))
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            Methods.shareImage(WallpaperDetailsActivity.this, Methods.getBytesFromFile(resource), Methods.createName(imageURL), "image/gif");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();
    }

    //help to share as image
    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            if (liveItemMain == null) {
                if (arrayList.get(currentPosition).type.equals("upload")) {
                    File file = new File(getExternalFilesDir(DIRECTORY_PICTURES),
                            "wallpaper" + System.currentTimeMillis() + ".png");
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.close();
                    bmpUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
                } else {
                    File file = new File(getExternalFilesDir(DIRECTORY_PICTURES),
                            "wallpaper" + System.currentTimeMillis() + ".gif");
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    bmpUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                }
            } else {
                if (liveItemMain.type.equals("upload")) {
                    File file = new File(getExternalFilesDir(DIRECTORY_PICTURES),
                            "wallpaper" + System.currentTimeMillis() + ".png");
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.close();
                    bmpUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                } else {
                    File file = new File(getExternalFilesDir(DIRECTORY_PICTURES),
                            "wallpaper" + System.currentTimeMillis() + ".gif");
                    FileOutputStream out = new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    bmpUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    //download image
    private void downloadBitmap() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        saveBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

    }

    //save image to phone storage
    private void saveBitmap(Bitmap bitmap) {
        OutputStream fos;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            ContentResolver resolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, DIRECTORY_PICTURES + "/" + getString(R.string.app_name));
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            Toast.makeText(WallpaperDetailsActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();
            try {
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String fileName = UUID.randomUUID() + ".jpg";
            String path = Environment.getExternalStorageDirectory().toString();
            File folder = new File(path + "/" + DIRECTORY_PICTURES + "/" + getString(R.string.app_name));
            folder.mkdir();

            filePath = new File(folder, fileName);
            if (filePath.exists())
                filePath.delete();

            try {

                FileOutputStream outputStream = new FileOutputStream(filePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                //send pictures to gallery
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(filePath)));

                Toast.makeText(WallpaperDetailsActivity.this, "Download Wallpaper Successfully", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        relativeLayoutLoadMore.setVisibility(View.GONE);
    }

    public void copyFile(File fromFile, File toFile) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        FileChannel fileChannelInput = null;
        FileChannel fileChannelOutput = null;
        try {
            fileInputStream = new FileInputStream(fromFile);
            fileOutputStream = new FileOutputStream(toFile);
            fileChannelInput = fileInputStream.getChannel();
            fileChannelOutput = fileOutputStream.getChannel();
            fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
                if (fileChannelInput != null)
                    fileChannelInput.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
                if (fileChannelOutput != null)
                    fileChannelOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //set gif as wallpaper
    public void setGif(final String str) {
//        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        String videoUrl = Config.WEBSITE_URL + "/images/wallpaper/" + liveItemMain.getImage();
        String fileName = liveItemMain.getImage_name() + ".mp4";
        File file = new File("/storage/emulated/0/Android/data/com.dsquare.wallzee/files/" + fileName);
        if (file.exists()) {
            Toast.makeText(this, "File is exists", Toast.LENGTH_SHORT).show();
            try {
                Uri uri = Uri.fromFile(file);
                openFileOutput("video_live_wallpaper_file_path", Context.MODE_PRIVATE).write(
                        Objects.requireNonNull(DocumentUtils.getPath(WallpaperDetailsActivity.this, uri)).getBytes()
                );
                VideoLiveWallpaperService.setToWallPaper(WallpaperDetailsActivity.this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            new Downloader.Builder(this, videoUrl)
                    .downloadListener(new OnDownloadListener() {
                        @Override
                        public void onStart() {
                            relativeLayoutLoadMore.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onPause() {

                        }

                        @Override
                        public void onResume() {

                        }

                        @Override
                        public void onProgressUpdate(int i, int i1, int i2) {

                        }

                        @Override
                        public void onCompleted(@Nullable File file) {
                            relativeLayoutLoadMore.setVisibility(View.GONE);
//                                Toast.makeText(WallpaperDetailsActivity.this, "Download Completed path" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            Log.d("FILEPATH", "onCompleted: fileName " + file.getName() + "and \n Path " + file.getAbsolutePath());
                            try {
                                Uri uri = Uri.fromFile(file);
                                openFileOutput("video_live_wallpaper_file_path", Context.MODE_PRIVATE).write(
                                        Objects.requireNonNull(DocumentUtils.getPath(WallpaperDetailsActivity.this, uri)).getBytes()
                                );
                                if (isOnce) {
                                    VideoLiveWallpaperService.setToWallPaper(WallpaperDetailsActivity.this);
                                }
                                isOnce = false;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }

                        @Override
                        public void onFailure(@Nullable String s) {

                        }

                        @Override
                        public void onCancel() {

                        }
                    })
                    .build().download();

        }
    }


    public void showSetUsOption() {
        showCustomDialog();
    }


    //set wallpaper on Home Screen
    private void setWallpaperToHomeScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperDetailsActivity.this);
                        try {

                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(rootLayout, "Wallpaper was set successfully", Snackbar.LENGTH_LONG).show();
                                    relativeLayoutLoadMore.setVisibility(View.GONE);
                                }
                            }, Constant.DELAY_SET_WALLPAPER);
                        } catch (IOException e) {
                            e.printStackTrace();
                            relativeLayoutLoadMore.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {


                    }
                });

    }

    //set wallpaper on Lock Screen
    private void setWallpaperToLockScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asBitmap()
                .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperDetailsActivity.this);
                        try {
                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_LOCK);

                            new Handler().postDelayed(() -> {
                                Snackbar.make(rootLayout, "Wallpaper was set successfully", Snackbar.LENGTH_LONG).show();
                                relativeLayoutLoadMore.setVisibility(View.GONE);
                            }, Constant.DELAY_SET_WALLPAPER);
                        } catch (IOException e) {
                            e.printStackTrace();
                            relativeLayoutLoadMore.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    //set wallpaper on Both Screen
    private void setWallpaperToBothScreen() {
        relativeLayoutLoadMore.setVisibility(View.VISIBLE);

        Glide.with(this)
                .asBitmap()
                .load(Config.WEBSITE_URL + "/images/wallpaper/" + arrayList.get(currentPosition).getImage())
                .into(new CustomTarget<Bitmap>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(WallpaperDetailsActivity.this);
                        try {

                            wallpaperManager.setBitmap(resource, null, true, WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(rootLayout, "Wallpaper was set successfully", Snackbar.LENGTH_LONG).show();
                                    relativeLayoutLoadMore.setVisibility(View.GONE);
                                }
                            }, Constant.DELAY_SET_WALLPAPER);
                        } catch (IOException e) {
                            e.printStackTrace();
                            relativeLayoutLoadMore.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

//my task
private static class MyTask extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        return Methods.getJSONString(params[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        if (null == result || result.length() == 0) {
            Log.d("TAG", "no data found!");
        } else {

            try {

                JSONObject mainJson = new JSONObject(result);
                JSONArray jsonArray = mainJson.getJSONArray("result");
                //This Admin panel and WallpaperX app Created by YMG Developers
                JSONObject objJson = null;
                for (int i = 0; i < jsonArray.length(); i++) {
                    objJson = jsonArray.getJSONObject(i);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) WallpaperDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } else {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (prefManager.loadNightModeState()) {
            Log.d("Dark", "MODE");
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
        super.onResume();
    }

private class LoadVideoTask extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... params) {
        try {
            // Load the video in the background
            Uri videoUri = Uri.parse(params[0]);
            detailPlayer.setVideoURI(videoUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        detailPlayer.start();
    }
}
}
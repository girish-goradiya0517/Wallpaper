package com.dsquare.wallzee.Activity;

import static java.util.Currency.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dsquare.wallzee.Adapter.AdapterRecent;
import com.dsquare.wallzee.Adapter.AdapterRingtone;
import com.dsquare.wallzee.Callback.CallbackUser;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.MainActivity;
import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.Model.Users;
import com.dsquare.wallzee.Model.Wallpaper;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.ApiService;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.AdsManager;
import com.dsquare.wallzee.Utils.AdsPref;
import com.dsquare.wallzee.Utils.DarkModeUtils;
import com.dsquare.wallzee.Utils.Methods;
import com.dsquare.wallzee.Utils.PrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
// Volley imports
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class ProfileActivity extends AppCompatActivity implements AdapterRingtone.MediaCallBack {

    TextView followBtn;

    GoogleSignInClient signInClient;
    TextView tvFollowers;
    TextView tvFollowing;
    MediaPlayer player;
    TextView tvUploads;
    TextView wallpaperBtn;
    TextView ringtoneBtn;
    ImageView ivUser;

    String userId;
    String tvUserName;
    String userImage;
    PrefManager prefManager;
    RecyclerView recyclerViewWallpaper,recyclerViewRingtone;

    private AdapterRecent adapterRecent;
    private AdapterRingtone adapterRingtone;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar load_more;

    private Call<CallbackUser> callbackCall = null;

    private AdsManager adsManager;
    private AdsPref adsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        prefManager = new PrefManager(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient =  GoogleSignIn.getClient(getApplicationContext(), gso);
        Intent intent = getIntent();
        userImage = intent.getStringExtra("userImage");
        tvUserName = intent.getStringExtra("tvUserName");
        userId = intent.getStringExtra("userId");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(tvUserName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        adsPref = new AdsPref(this);
        adsManager = new AdsManager(this);
        adsManager.loadBannerAd(true);
        adsManager.loadInterstitialAd(true,adsPref.getInterstitialAdInterval());

        followBtn = findViewById(R.id.followBtn);
        tvFollowers = findViewById(R.id.tvFollowers);
        tvUploads = findViewById(R.id.tvUploads);
        tvFollowing = findViewById(R.id.tvFollowing);
        ivUser = findViewById(R.id.ivUser);
        wallpaperBtn = findViewById(R.id.wallpaperBtn);
        ringtoneBtn = findViewById(R.id.ringtoneBtn);
        recyclerViewWallpaper = findViewById(R.id.recyclerViewWallpaper);
        recyclerViewRingtone = findViewById(R.id.recyclerViewRingtone);
        load_more = findViewById(R.id.load_more);

        Glide.with(this)
                .load(userImage)
                .into(ivUser);



        recyclerViewWallpaper.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        recyclerViewWallpaper.setHasFixedSize(true);
        //set data and list adapter
        adapterRecent = new AdapterRecent(this, recyclerViewWallpaper, new ArrayList<>());
        recyclerViewWallpaper.setAdapter(adapterRecent);



        recyclerViewRingtone.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        recyclerViewRingtone.setHasFixedSize(true);
        //set data and list adapter
        adapterRingtone = new AdapterRingtone(this, new ArrayList<>(),this);
        recyclerViewRingtone.setAdapter(adapterRingtone);



        loadWallpaper();

        if (prefManager.getString(userId).equals(userId)){
            followBtn.setText("Unfollow");
            followBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
        }else {
            followBtn.setText("Follow");
            followBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
        }


        if (prefManager.getInt("USER_ID") == Integer.parseInt(userId)){
            followBtn.setText("LOGOUT");
            followBtn.setOnClickListener(v -> {
                signInClient.signOut().addOnCompleteListener(task -> {
                   if (task.isComplete()){
                       prefManager.setInt("USER_ID",0);
                       startActivity(new Intent(this, MainActivity.class));
//                       onBackPressed();
                   }
                });
            });
//            followBtn.setAlpha(0.5f);
        }else {
            followBtn.setOnClickListener(view -> {
                if (prefManager.getString(userId).equals(userId)){
                    prefManager.setString(userId,"");
                    followBtn.setText("Follow");
                    followBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.colorAccent)));
                }else {
                    prefManager.setString(userId,userId);
                    followBtn.setText("Unfollow");
                    followBtn.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
            });
        }






        wallpaperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewRingtone.setVisibility(View.GONE);
                recyclerViewWallpaper.setVisibility(View.VISIBLE);
                wallpaperBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.dark_gray)));
                ringtoneBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.transparent)));
                wallpaperBtn.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.white));
                ringtoneBtn.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.darker_gray));
            }
        });

        ringtoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewRingtone.setVisibility(View.VISIBLE);
                recyclerViewWallpaper.setVisibility(View.GONE);
                ringtoneBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.dark_gray)));
                wallpaperBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ProfileActivity.this, R.color.transparent)));
                ringtoneBtn.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.white));
                wallpaperBtn.setTextColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.darker_gray));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (prefManager.getInt("USER_ID") == Integer.parseInt(userId)){
            getMenuInflater().inflate(R.menu.account, menu);
            MenuItem delete = menu.findItem(R.id.delete);
            SpannableString menuString =  new SpannableString(
                    delete.getTitle().toString()
            );
            menuString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, menuString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            delete.setOnMenuItemClickListener(menuItem -> {
                showDelete();
                return false;
            });
        }




        return true;
    }

    private void showDelete() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("Delete account");
        materialAlertDialogBuilder.setMessage("Warning: This accent can't be undone all uploaded wallpaper and following will be deleted");
        materialAlertDialogBuilder.setPositiveButton("Cancel", null);
        materialAlertDialogBuilder.setNegativeButton("Delete anyway", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Config.deleteAccount(ProfileActivity.this);
            }
        });
        materialAlertDialogBuilder.create();
        materialAlertDialogBuilder.show();

    }





    @Override
    public void onChangeMedia(MediaPlayer player) {
        if (player != null){
            this.player = player;
        }
    }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadWallpaper() {
        load_more.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);

        callbackCall = apiInterface.getUser(userId, Config.DEVELOPER_NAME);

        callbackCall.enqueue(new Callback<CallbackUser>() {
            @Override
            public void onResponse(Call<CallbackUser> call, Response<CallbackUser> response) {
                CallbackUser resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    displayApiResult(resp.wallpaper, resp.ringtone, resp.users);
                } else {

                }
            }

            @Override
            public void onFailure(Call<CallbackUser> call, Throwable t) {
                //
            }

        });
    }

    private void displayApiResult(final List<Wallpaper> wallpapers, final List<Ringtone> ringtones, final List<Users> users) {
        load_more.setVisibility(View.GONE);
        adapterRecent.insertData(wallpapers);
        adapterRingtone.setListData(ringtones);

        tvFollowers.setText(users.get(0).followers);
        tvFollowing.setText(users.get(0).followingList);
        tvUploads.setText(users.get(0).total_uploads);


        if (wallpapers.size() == 0) {
            //showNoItemView(true);
        }
    }

    public void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (DarkModeUtils.isDarkModeEnabledWithUiModeManager(this)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            if (prefManager.loadNightModeState()==true){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}
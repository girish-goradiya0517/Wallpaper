package com.dsquare.wallzee;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.dsquare.wallzee.Activity.FavoriteActivity;
import com.dsquare.wallzee.Activity.LatestActivity;
import com.dsquare.wallzee.Activity.ProfileActivity;
import com.dsquare.wallzee.Activity.RingtoneActivity;
import com.dsquare.wallzee.Activity.SearchActivity;
import com.dsquare.wallzee.Activity.SettingsActivity;
import com.dsquare.wallzee.Activity.UploadActivity;
import com.dsquare.wallzee.Callback.RegistrationResponse;
import com.dsquare.wallzee.Fragment.CategoryFragment;
import com.dsquare.wallzee.Fragment.FragmentDouble;
import com.dsquare.wallzee.Fragment.FragmentHome;
import com.dsquare.wallzee.Fragment.FragmentLive;
import com.dsquare.wallzee.Fragment.FragmentRecent;
import com.dsquare.wallzee.Fragment.RingtoneFragment;
import com.dsquare.wallzee.Model.RegistrationRequest;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.AdsManager;
import com.dsquare.wallzee.Utils.AdsPref;
import com.dsquare.wallzee.Utils.CustomTabLayout;
import com.dsquare.wallzee.Utils.DarkModeUtils;
import com.dsquare.wallzee.Utils.PrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.onesignal.OneSignal;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentHome.OnExploreCategoryClickListener{

    DrawerLayout drawerLayout;
    Toolbar toolBar;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    ViewPager viewPager;
    public CustomTabLayout smartTabLayout;
    public int tabCount = 6;

    PrefManager prefManager;
    private static final String TAG = "MainActivity";
    SimpleSearchView simpleSearchView;
    private static final String ONESIGNAL_APP_ID = "84d1119a-e420-4ff1-9ddf-377f687c432c";


    private static final int REQ_ONE_TAP = 2;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    private GoogleSignInClient mGoogleSignInClient;
    private String userId;
    private String refferCode;
    private Button mGoogleButton;

    private AdsManager adsManager;
    private AdsPref adsPref;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        Config.FONT_NAME = "g";

        prefManager = new PrefManager(this);
        adsPref = new AdsPref(this);
        adsManager = new AdsManager(this);
        adsManager.initializeAd();
        adsManager.updateConsentStatus();
        adsManager.loadBannerAd(false);
        adsManager.loadInterstitialAd(true,adsPref.getInterstitialAdInterval());

        initialize();
        signInProcess();


        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView textView = headerView.findViewById(R.id.userName);
        ImageView imageView = headerView.findViewById(R.id.imageView);

        ImageView iv_user_pp = findViewById(R.id.iv_user_pp);

        if (prefManager.getInt("USER_ID") == 0){
            textView.setText(R.string.app_name);
            Glide.with(this).load(R.drawable.logo).into(imageView);
            Glide.with(this).load(R.drawable.logo).circleCrop().into(iv_user_pp);
            imageView.setOnClickListener(view -> showLogin());

            iv_user_pp.setOnClickListener(view -> showLogin());
        }else {
            FirebaseAuth mAuth=FirebaseAuth.getInstance();
            currentUser = mAuth.getCurrentUser();
            textView.setText(currentUser.getDisplayName()+"");
            Glide.with(this).load(currentUser.getPhotoUrl()+"").into(imageView);
            Glide.with(this).load(currentUser.getPhotoUrl()+"").circleCrop().into(iv_user_pp);
            imageView.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("userImage", currentUser.getPhotoUrl()+"");
                intent.putExtra("tvUserName", currentUser.getDisplayName()+"");
                intent.putExtra("userId", prefManager.getInt("USER_ID")+"");
                startActivity(intent);
            });

            iv_user_pp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("userImage", currentUser.getPhotoUrl()+"");
                    intent.putExtra("tvUserName", currentUser.getDisplayName()+"");
                    intent.putExtra("userId", prefManager.getInt("USER_ID")+"");
                    startActivity(intent);
                }
            });
        }



        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        toolBar.setNavigationIcon(R.drawable.ic_action_action);

        smartTabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setOffscreenPageLimit(tabCount);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), tabCount));
        smartTabLayout.post(() -> smartTabLayout.setViewPager(viewPager));
        simpleSearchView = findViewById(R.id.search_view);
        simpleSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SimpleSearchView", "Submit:" + query);
                prefManager.setString("searchWallpaper", query);
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("wallpaper", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SimpleSearchView", "Text changed:" + newText);
                return false;
            }

            @Override
            public boolean onQueryTextCleared() {
                Log.d("SimpleSearchView", "Text cleared");
                return false;
            }
        });

    }

    @SuppressWarnings("deprecation")
    public class ViewPagerAdapter extends FragmentStatePagerAdapter {

        int noOfItems;

        public ViewPagerAdapter(FragmentManager fm, int noOfItems) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.noOfItems = noOfItems;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                FragmentHome fragmentHome = new FragmentHome();
                Log.d("TAG", "Fragment: Home");
                return fragmentHome;
            } else if (position == 1) {
                FragmentRecent fragmentRecent = new FragmentRecent();
                Log.d("TAG", "Fragment: fragmentRecent");
                return fragmentRecent;
            } else if (position == 2) {
                FragmentLive liveFragment = new FragmentLive();
                Log.d("TAG", "Fragment: FragmentLive");
                return liveFragment;
            } else if (position == 3) {
                FragmentDouble ringtoneFragment = new FragmentDouble();
                Log.d("TAG", "Fragment: FragmentDouble");
                return ringtoneFragment;
            } else if (position == 4) {
                RingtoneFragment ringtoneFragment = new RingtoneFragment();
                Log.d("TAG", "Fragment: RingtoneFragment");
                return ringtoneFragment;
            } else {
                CategoryFragment randomFragment = new CategoryFragment();
                Log.d("TAG", "Fragment: CategoryFragment");
                return randomFragment;
            }
        }

        @Override
        public int getCount() {
            return noOfItems;
        }

        @Override
        public String getPageTitle(int position) {
            if (position == 0) {
                return getResources().getString(R.string.menu_home);
            } else if (position == 1) {
                return getResources().getString(R.string.menu_latest);
            } else if (position == 2) {
                return getResources().getString(R.string.menu_live);
            } else if (position == 3) {
                return getResources().getString(R.string.menu_double);
            } else if (position == 4) {
                return getResources().getString(R.string.menu_ringtone);
            } else {
                return getResources().getString(R.string.menu_collection);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        simpleSearchView.setMenuItem(item);
        return true;
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
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (menuItem.getItemId() == R.id.upload){
            if (prefManager.getInt("USER_ID") == 0){
                showLogin();
            }else {
                startActivity(new Intent(this, UploadActivity.class));
            }

        }
        if (menuItem.getItemId() == R.id.nav_home){
            Intent intent = new Intent(MainActivity.this, LatestActivity.class);
            intent.putExtra("wallpaper", "Latest");
            startActivity(intent);

        }
        if (menuItem.getItemId() == R.id.nav_ringtone){
            Intent intent = new Intent(this, RingtoneActivity.class);
            intent.putExtra("categoryName", "Ringtones");
            intent.putExtra("categoryId", "13");
            startActivity(intent);

        }
        if (menuItem.getItemId() == R.id.nav_profile){
            startActivity(new Intent(this, FavoriteActivity.class));
        }
        if (menuItem.getItemId() == R.id.nav_setting){
           startActivity(new Intent(this, SettingsActivity.class));
        }
        if (menuItem.getItemId() == R.id.nav_about){
            showAbout();
        }
        if (menuItem.getItemId() == R.id.nav_insta){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.instagram.com/"+Config.INSTAGRAM));
            startActivity(browserIntent);
        }
        if (menuItem.getItemId() == R.id.nav_share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
            startActivity(Intent.createChooser(intent,"share via"));

        }
        if (menuItem.getItemId() == R.id.nav_rate){
            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            }catch (ActivityNotFoundException ex){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        if (menuItem.getItemId() == R.id.nav_feedback){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.setPackage("com.google.android.gm");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{Config.CONTACT_EMAIL});
            i.putExtra(Intent.EXTRA_SUBJECT, Config.CONTACT_SUBJECT);
            i.putExtra(Intent.EXTRA_TEXT, Config.CONTACT_TEXT);
            i.putExtra(Intent.EXTRA_ORIGINATING_URI, "mailto:"+Config.CONTACT_EMAIL);
            try {
                startActivity(Intent.createChooser(i, "Send mail"));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    @Override
    public void onExploreCategoryClick() {
        // Handle the click event and change the ViewPager page
        if (viewPager != null) {
            viewPager.setCurrentItem(5, true);
        }
    }

    @Override
    public void onExploreRingtoneClick() {
        if (viewPager != null) {
            viewPager.setCurrentItem(4, true);
        }
    }

    @Override
    public void onExploreLiveClick() {
        if (viewPager != null) {
            viewPager.setCurrentItem(2, true);
        }
    }

    @Override
    public void onExplorelatestClick() {
        if (viewPager != null) {
            viewPager.setCurrentItem(1, true);
        }
    }

    @Override
    public void onExploredouble() {
        if (viewPager != null) {
            viewPager.setCurrentItem(3, true);
        }
    }

    @Override
    public void onExplorePopularClick() {
        if (viewPager != null) {
            viewPager.setCurrentItem(0, true);
        }
    }


    @SuppressLint("SetTextI18n")
    private void showAbout() {
        final Dialog customDialog;
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_about, null);
        customDialog = new Dialog(this, R.style.DialogCustomTheme);
        customDialog.setContentView(customView);
        AppCompatButton tvClose = customDialog.findViewById(R.id.btn_done);

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }


    @SuppressLint("SetTextI18n")
    private void showLogin() {
        final Dialog customDialog;
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_install, null);

        customDialog = new Dialog(this, R.style.DialogCustomTheme);
        customDialog.setContentView(customView);

        TextView btn_done = customDialog.findViewById(R.id.btn_done);
        TextView tvClose = customDialog.findViewById(R.id.tvLater);

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
                customDialog.dismiss();
            }
        });

        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });
        customDialog.show();
    }


    private void showExitDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_exit);

        LinearLayout dialog_btn=dialog.findViewById(R.id.mbtnYes);
        LinearLayout mbtnNo=dialog.findViewById(R.id.mbtnNo);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mbtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    public void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {


        if (DarkModeUtils.isDarkModeEnabledWithUiModeManager(this)){

        }else {
            if (prefManager.loadNightModeState()==true){
                Log.d("Dark", "MODE");
            }else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
            }
        }
    }



    private void initialize(){
        mAuth = FirebaseAuth.getInstance();
    }

    private void signInProcess() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQ_ONE_TAP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQ_ONE_TAP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.d("Authentication","firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {

                Log.d("Authentication","" +e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            userId = currentUser.getEmail();
                            addToServer(userId, currentUser.getDisplayName(), String.valueOf(currentUser.getPhotoUrl()));
                        }
                    }
                });
    }

    private void addToServer(String email , String name , String image) {
        RegistrationRequest user = new RegistrationRequest();
        user.setEmail(email);
        user.setName(name);
        user.setProfileImageUrl(image);

        
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);
        Call<RegistrationResponse> call = apiInterface.registerUser(user);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful()) {
                    RegistrationResponse registrationResponse = response.body();
                    int userId = registrationResponse.getUserId();

                    prefManager.setInt("USER_ID",userId);
                    MainActivity.this.recreate();
                } else {
                    // Handle other API errors
                    Log.e("API_ERROR", "Error response: " + response.toString());
                    Toast.makeText(MainActivity.this, "API error, please check logs.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                // Handle network or request failure
                Log.e("API_ERROR", "Error response: " + t);
                Toast.makeText(MainActivity.this, t+"", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
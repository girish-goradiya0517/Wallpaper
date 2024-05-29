package com.dsquare.wallzee.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dsquare.wallzee.Adapter.AdapterRecent;
import com.dsquare.wallzee.Datebase.DBHelper;
import com.dsquare.wallzee.Model.Wallpaper;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Utils.PrefManager;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {


    private AdapterRecent adapterWallpaper;
    DBHelper dbHelper;
    ArrayList<Wallpaper> wallpaperArrayList;
    private RecyclerView recyclerView;
    PrefManager prefManager;
    View noFavoriteLayout;


    private final String TAG = FavoriteActivity.class.getSimpleName();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(R.string.favorite);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dbHelper = new DBHelper(this);
        prefManager = new PrefManager(this);


        wallpaperArrayList = new ArrayList<>();

        noFavoriteLayout=  findViewById(R.id.lyt_no_favorite);
        this.recyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        this.recyclerView.setHasFixedSize(true);
        adapterWallpaper = new AdapterRecent(this, recyclerView, wallpaperArrayList);
        this.adapterWallpaper = adapterWallpaper;
        this.recyclerView.setAdapter(adapterWallpaper);
        displayData();



    }

    private void displayData() {
        ArrayList<Wallpaper> allFavorite = this.dbHelper.getAllFavorite(DBHelper.TABLE_FAVORITE);
        adapterWallpaper.insertData(allFavorite);

        if (allFavorite.size() == 0) {
            noFavoriteLayout.setVisibility(View.VISIBLE);
        } else {
            noFavoriteLayout.setVisibility(View.GONE);
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

    public void onResume() {
        super.onResume();
       // displayData();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterWallpaper);
        initCheck();
        wallpaperArrayList.clear();
        displayData();
    }

    private void initCheck() {
        if (prefManager.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}
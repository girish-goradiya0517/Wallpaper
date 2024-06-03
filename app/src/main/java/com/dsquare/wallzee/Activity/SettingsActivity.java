package com.dsquare.wallzee.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.dsquare.wallzee.MainActivity;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Utils.PrefManager;

import java.io.File;
import java.text.DecimalFormat;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class SettingsActivity extends AppCompatActivity {

    private TextView tvCurrentVersion;
    private TextView tvSaveLocation;
    private TextView tvCacheValue;
    private TextView tvNotificationTag;
    private TextView tvColumns;
    private LinearLayout linearLayoutPolicyPrivacy;
    private LinearLayout linearLayoutClearCache;
    private LinearLayout linearLayoutColumes;
    private Switch switchButtonNotification;
    private AlertDialog alertDialog1;
    private CharSequence[] values = {" 2 Columns "," 3 Columns "};
    private PrefManager prefManager;
    private Switch switchDarkMode;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Settings");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prefManager = new PrefManager(this);

        tvCurrentVersion = findViewById(R.id.tvCurrentVersion);
        tvSaveLocation = findViewById(R.id.tvSaveLocation);
        tvCacheValue = findViewById(R.id.tvCacheValue);
        tvNotificationTag = findViewById(R.id.tvNotificationTag);
        tvColumns = findViewById(R.id.tvColumns);
        linearLayoutPolicyPrivacy = findViewById(R.id.linearLayoutPolicyPrivacy);
        linearLayoutClearCache = findViewById(R.id.linearLayoutClearCache);
        linearLayoutColumes = findViewById(R.id.linearLayoutColumes);
        switchButtonNotification = findViewById(R.id.switchButtonNotification);
        switchDarkMode = findViewById(R.id.switch_button_animation);

        tvCurrentVersion.setText("v "+getAppVersion());
        tvSaveLocation.setText(getResources().getString(R.string.storagelocation)+getResources().getString(R.string.app_name));
        tvCacheValue.setText(getResources().getString(R.string.label_cache)+readableFileSize((getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())));
        tvNotificationTag.setText(getResources().getString(R.string.label_notification)+getResources().getString(R.string.app_name));
        tvColumns.setText(prefManager.getInt("wallpaperColumns")+" Columns");

        linearLayoutClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                clearCache();
            }
        });
        linearLayoutPolicyPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity(new Intent(SettingsActivity.this, PrivacyPolicyActivity.class));
            }
        });
        linearLayoutColumes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                CreateAlertDialog();
            }
        });

        //Night Mode
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    prefManager.setNightModeState(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    onResume();
                }else {
                    prefManager.setNightModeState(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    onResume();
                }
            }
        });

    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    private String getAppVersion(){
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0 Bytes";
        }
        String[] units = new String[]{"Bytes", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10((double) size) / Math.log10(1024.0d));
        StringBuilder stringBuilder = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double d = (double) size;
        double pow = Math.pow(1024.0d, (double) digitGroups);
        Double.isNaN(d);
        stringBuilder.append(decimalFormat.format(d / pow));
        stringBuilder.append(" ");
        stringBuilder.append(units[digitGroups]);
        return stringBuilder.toString();
    }

    private void clearCache() {

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tvCacheValue.setText(getResources().getString(R.string.label_cache)+readableFileSize((getDirSize(getCacheDir())) + getDirSize(getExternalCacheDir())));
                Toast.makeText(SettingsActivity.this, getString(R.string.msg_cache_cleared), Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //This Admin panel and Prime TV app Created by YMG Developers
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void CreateAlertDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("Display Wallpaper");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item)
                {
                    case 0:
                        prefManager.setInt("wallpaperColumns",2);
                        prefManager.setString("wallpaperColumnsString","two");
                        onResume();
                        SettingsActivity.super.onBackPressed();
                        startActivity(new Intent(SettingsActivity.this,MainActivity.class));
                        break;
                    case 1:
                        prefManager.setInt("wallpaperColumns",3);
                        prefManager.setString("wallpaperColumnsString","three");
                        onResume();
                        SettingsActivity.super.onBackPressed();
                        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();
    }

    @SuppressLint("SetTextI18n")
    public void onResume() {
        super.onResume();
        tvColumns.setText(prefManager.getInt("wallpaperColumns")+" Columns");

        if (prefManager.loadNightModeState()==true){
            switchDarkMode.setChecked(true);
        }else {
            switchDarkMode.setChecked(false);

        }
        initCheck();

    }

    private void initCheck() {
        if (prefManager.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            //This Admin panel and Prime TV app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
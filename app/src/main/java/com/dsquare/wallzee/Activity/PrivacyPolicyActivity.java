package com.dsquare.wallzee.Activity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Utils.PrefManager;


public class PrivacyPolicyActivity extends AppCompatActivity {

    private TextView textView;
    private RelativeLayout relativeLayoutLoadMore;
    private PrefManager prefManager;
    private final String TAG = PrivacyPolicyActivity.class.getSimpleName();
    //This Admin panel and Prime TV app Created by YMG Developers

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.policy_privacy));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prefManager = new PrefManager(this);

        textView = findViewById(R.id.textView);
        relativeLayoutLoadMore = findViewById(R.id.relativeLayoutLoadMore);

        relativeLayoutLoadMore.setVisibility(View.GONE);
        initCheck();

        textView.setText(Html.fromHtml(prefManager.getPrivacyPolicy()));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCheck() {
        if (prefManager.loadNightModeState()){
            Log.d("Dark", "MODE");
        }else {
            //This Admin panel and Prime TV app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }
}
package com.dsquare.wallzee.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dsquare.wallzee.Callback.CallbackCategories;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Category;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.PrefManager;
import com.dsquare.wallzee.Utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class UploadActivity extends AppCompatActivity {

    private static final int FILE_PICKER_REQUEST = 1000;
    private Call<CallbackCategories> callbackCall = null;
    Spinner your_spinner_id;
    private Uri selectedFileUri;
    private EditText wallpaperName;
    private ImageView wallpaperPreview;
    private RelativeLayout chooseWallpaper;
    private LinearLayout noFileLayout;
    private TextView uploadBtn;

    int categoryID;
    Bitmap bitmap;

    private PrefManager prefManager;

    private boolean clickBtn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        prefManager = new PrefManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("Upload Content");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        your_spinner_id = findViewById(R.id.your_spinner_id);
        wallpaperName = findViewById(R.id.wallpaperName);
        wallpaperPreview = findViewById(R.id.wallpaper_preview);
        noFileLayout = findViewById(R.id.noFileLayout);
        chooseWallpaper = findViewById(R.id.chooseWallpaper);
        chooseWallpaper = findViewById(R.id.chooseWallpaper);
        uploadBtn = findViewById(R.id.uploadBtn);
        requestCategoriesApi();

        chooseWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilePicker();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickBtn){
                    if (!wallpaperName.getText().toString().equals("")){
                        if (bitmap != null) {
                            try {
                                uploadImageToServer(bitmap);
                            } catch (AuthFailureError e) {
                                throw new RuntimeException(e);
                            }
                            clickBtn = false;
                        }else {
                            Toast.makeText(UploadActivity.this, "Please Select Wallpaper Image", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(UploadActivity.this, "Please Enter Wallpaper Name", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_PICKER_REQUEST);


    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        initCheck();
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
            //This Admin panel and WallpaperX app Created by YMG Developers
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);   // set status text dark
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST && resultCode == RESULT_OK) {
            selectedFileUri = data.getData();
            if (selectedFileUri != null) {
                // Display the selected file or its name in a TextView or ImageView
                // For example, if you have an ImageView with ID "wallpaper_preview":
                // ImageView wallpaperPreview = findViewById(R.id.wallpaper_preview);
                wallpaperPreview.setImageURI(selectedFileUri);
                noFileLayout.setVisibility(View.GONE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedFileUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case when the fileUri is null (e.g., no file selected).
                Toast.makeText(getApplicationContext(), "No file selected.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void uploadImageToServer(final Bitmap bitmap) throws AuthFailureError {


        final Dialog customDialog;
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_loading, null);
        customDialog = new Dialog(this, R.style.DialogCustomTheme);
        customDialog.setContentView(customView);
        customDialog.setCancelable(false);

        TextView txt_link = customDialog.findViewById(R.id.txt_link);
        AppCompatButton btn_done = customDialog.findViewById(R.id.btn_done);
        ProgressBar progressBar = customDialog.findViewById(R.id.progressBar);
        btn_done.setVisibility(View.GONE);


        customDialog.show();



        // Replace with your server URL
        String uploadUrl = Config.WEBSITE_URL+"uploadw.php";

        // Get the tag and convert it to a string
        final String wallpaper_Name = wallpaperName.getText().toString().trim();


        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, uploadUrl,
                response -> {
                    String responseString = new String(response.data);
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String message = jsonObject.optString("message");

                        txt_link.setText(message);
                        progressBar.setVisibility(View.GONE);

                        btn_done.setVisibility(View.VISIBLE);
                        btn_done.setOnClickListener(v -> {
                            customDialog.dismiss();
                            finish();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    clickBtn = true;
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tags", wallpaper_Name);
                params.put("category", String.valueOf(categoryID));
                params.put("user_id", String.valueOf(prefManager.getInt("USER_ID")));
                params.put("status", "1");

                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                params.put("pic", new DataPart(imageName + ".png", getFileDataFromBitmap(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private byte[] getFileDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private void requestCategoriesApi() {
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);
        callbackCall = apiInterface.getAllCategories(Config.DEVELOPER_NAME);
        callbackCall.enqueue(new Callback<CallbackCategories>() {
            @Override
            public void onResponse(Call<CallbackCategories> call, retrofit2.Response<CallbackCategories> response) {
                CallbackCategories resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    List<Category> categories = resp.categories; // Extract categories from the response
                    Map<String, Integer> categoryMap = new HashMap<>();
                    // Clear the existing map before populating it
                    categoryMap.clear();

                    // Extract category names and category ids from the categories list
                    List<String> categoryNames = new ArrayList<>();
                    for (Category category : categories) {
                        String categoryName = category.category_name;
                        int categoryId = category.cid;
                        categoryNames.add(categoryName);

                        // Populate the map with category_name and category_id pairs
                        categoryMap.put(categoryName, categoryId);
                    }

                    // Create an ArrayAdapter and set it to the Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_spinner_item, categoryNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    Spinner spinner = findViewById(R.id.your_spinner_id);
                    spinner.setAdapter(adapter);

                    // Set an OnItemSelectedListener to handle item selection
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            // Get the selected category name
                            String selectedCategory = categoryNames.get(position);

                            // Get the corresponding category_id from the map
                            int categoryId = categoryMap.get(selectedCategory);

                            // Display the category_id in a toast message

                            categoryID = categoryId;

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            // Handle the case when nothing is selected
                        }
                    });
                } else {
                    // Handle the case when the API response is not "ok"
                }
            }

            @Override
            public void onFailure(Call<CallbackCategories> call, Throwable t) {
                // Handle network or request failure
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showLoading() {
        final Dialog customDialog;
        LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
        View customView = inflater.inflate(R.layout.dialog_loading, null);
        customDialog = new Dialog(this, R.style.DialogCustomTheme);
        customDialog.setContentView(customView);

        AppCompatButton tvClose = customDialog.findViewById(R.id.btn_done);


        customDialog.show();
    }

}
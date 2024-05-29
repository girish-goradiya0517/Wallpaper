package com.dsquare.wallzee;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;


import android.app.Activity;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dsquare.wallzee.Utils.PrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class Config {

    //Admin panel url with /
    public static final String WEBSITE_URL = "https://dashboard.wallzeeapp.com/";

    //Your Instagram Username
    public static final String INSTAGRAM = "wallzeeapp";

    //load more for next list channel
    public static final int LOAD_MORE = 15;

    public static final boolean LEGACY_GDPR = false;
    public static final String CONTACT_EMAIL = "contactwallzee@gmail.com";
    public static final String CONTACT_SUBJECT = "";
    public static final String CONTACT_TEXT = "Hello Team,";

    public static String FONT_NAME = "sniglet";

    public static final String PRIVACY_POLICY_URL = "https://www.app-privacy-policy.com/live.php?token=9fUVRT738zxTTnV5mBViu15AZy9al3TK";


    public static final String FONT_NAMES = Config.FONT_NAMES;


    //DO NOT CHANGE
    public static final String DEVELOPER_NAME = "YMG-Developers";

    public static final String STRING_NAME = "WU1HLURldmVsb3BlcnM=";




    public static void deleteAccount(Activity activity) {


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            String WEBSITE_URL = Config.WEBSITE_URL;
            String DELETE_ACCOUNT_URL = WEBSITE_URL + "deleteAccount.php?email="+userEmail;


            StringRequest stringRequest = new StringRequest(Request.Method.GET, DELETE_ACCOUNT_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    // Handle successful deletion
                    Toast.makeText(activity, response.trim(), Toast.LENGTH_SHORT).show();


                    new PrefManager(activity).setInt("USER_ID", 0);
                    // Sign out the user
                    FirebaseAuth.getInstance().signOut();

                    activity.startActivity(new Intent(activity, MainActivity.class));

                    activity.finish();
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Handle error
                    Toast.makeText(activity, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            });



            // Add the request to the RequestQueue
            RequestQueue requestQueue = Volley.newRequestQueue(activity);
            requestQueue.add(stringRequest);
        }
    }

}



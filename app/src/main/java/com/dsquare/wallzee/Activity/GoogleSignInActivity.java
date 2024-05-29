package com.dsquare.wallzee.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dsquare.wallzee.Callback.RegistrationResponse;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.RegistrationRequest;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleSignInActivity extends AppCompatActivity{

    private static final int REQ_ONE_TAP = 2;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;


    private GoogleSignInClient mGoogleSignInClient;
    private String userId;
    private String refferCode;
    private Button mGoogleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        mGoogleButton = findViewById(R.id.sign_in_button);

        initialize();
        signInProcess();
        listener();
    }

    private void initialize(){
        mAuth = FirebaseAuth.getInstance();
    }

    private void listener(){
        mGoogleButton.setOnClickListener(v-> signIn());
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

                            Toast.makeText(this, userId+"", Toast.LENGTH_SHORT).show();

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


                } else {
                    // Handle other API errors
                    Log.e("API_ERROR", "Error response: " + response.toString());
                    Toast.makeText(GoogleSignInActivity.this, "API error, please check logs.", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                // Handle network or request failure
                Toast.makeText(GoogleSignInActivity.this, t+"", Toast.LENGTH_SHORT).show();
            }
        });

    }


}


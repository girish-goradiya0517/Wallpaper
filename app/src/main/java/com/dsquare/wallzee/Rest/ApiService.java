package com.dsquare.wallzee.Rest;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("delete_user.php")
    Call<String> deleteUser(@Field("email") String email);
}

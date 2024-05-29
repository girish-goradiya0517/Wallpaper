package com.dsquare.wallzee.Rest;

import com.dsquare.wallzee.Callback.CallbackAds;
import com.dsquare.wallzee.Callback.CallbackCategories;
import com.dsquare.wallzee.Callback.CallbackCategoryDetails;
import com.dsquare.wallzee.Callback.CallbackDuo;
import com.dsquare.wallzee.Callback.CallbackHome;
import com.dsquare.wallzee.Callback.CallbackRingtone;
import com.dsquare.wallzee.Callback.CallbackUpload;
import com.dsquare.wallzee.Callback.CallbackUser;
import com.dsquare.wallzee.Callback.CallbackWallpaper;
import com.dsquare.wallzee.Callback.RegistrationResponse;
import com.dsquare.wallzee.Model.RegistrationRequest;
import com.dsquare.wallzee.Model.SliderModel;
import com.dsquare.wallzee.Model.responseReport;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    String CACHE = "Cache-Control: max-age=0";
    String AGENT = "Data-Agent: Your Videos Channel";


    @FormUrlEncoded
    @POST("delete_user.php")
    Call<String> deleteUser(@Field("email") String email);

    @Headers({CACHE, AGENT})
    @GET("api/get_videos")
    Call<CallbackWallpaper> getChannel(
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_duo")
    Call<CallbackDuo> getDuo(
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @POST("api/reportWallpaper")
    Call<responseReport> reportWallpaper(
            @Query("api_key") String api_key,
            @Query("wallpaperid") String wallpaperID,
            @Query("userid") String userID,
            @Query("message") String message,
            @Query("status") String status
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_rand")
    Call<CallbackWallpaper> getRandom(
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_live")
    Call<CallbackWallpaper> getLive(
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_category_index")
    Call<CallbackCategories> getAllCategories(
            @Query("api_key") String api_key
    );
    @Headers({CACHE, AGENT})
    @GET("api/get_slider")
    Call<SliderModel> getAllSliders(
            @Query("api_key") String api_key
    );
    @Headers({CACHE, AGENT})
    @GET("api/get_ringtone")
    Call<CallbackRingtone> getAllRingtone(
            @Query("api_key") String api_key
    );


    @Headers({CACHE, AGENT})
    @GET("api/get_category_ringtone")
    Call<CallbackCategoryDetails> getCategoryRingtone(
            @Query("id") int id,
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("api_key") String api_key
    );

    @Headers({CACHE, AGENT})
    @GET("api/get_home")
    Call<CallbackHome> getHome(
            @Query("api_key") String api_key
    );


    @Headers({CACHE, AGENT})
    @GET("api/get_user")
    Call<CallbackUser> getUser(
            @Query("id") String id,
            @Query("api_key") String api_key
    );


//    @Headers({CACHE, AGENT})
//    @GET("api/get_event")
//    Call<CallbackEvent> getEvent(
//            @Query("api_key") String api_key
//    );

    @Headers({CACHE, AGENT})
    @GET("api/get_category_wallpaper")
    Call<CallbackCategoryDetails> getCategoryWallpaper(
            @Query("id") int id,
            @Query("page") int page,
            @Query("count") int count,
            @Query("sort") String sort,
            @Query("api_key") String api_key
    );


    @Multipart
    @POST("code.php")
        // Replace with the actual URL
    Call<CallbackUpload> uploadWallpaper(
            @Part("save_wallpaper") RequestBody saveWallpaper,
            @Part("wallpaper_name") RequestBody wallpaperName,
            @Part("wallpaper_category") RequestBody wallpaperCategory,
            @Part("status") RequestBody status,
            @Part List<MultipartBody.Part> wallpaperImages
    );


//
//    @Headers({CACHE, AGENT})
//    @GET("api/get_author_videos")
//    Call<CallbackAuthorDetails> getAuthorVideos(
//            @Query("id") int id,
//            @Query("page") int page,
//            @Query("count") int count,
//            @Query("sort") String sort,
//            @Query("api_key") String api_key
//    );
//

    @Headers({CACHE, AGENT})
    @GET("api/get_search_results")
    Call<CallbackWallpaper> getSearchChannel(
            @Query("search") String search,
            @Query("count") int count,
            @Query("api_key") String api_key
    );

    //
    @Headers({CACHE, AGENT})
    @GET("api/get_ads")
    Call<CallbackAds> getAds(
            @Query("api_key") String api_key
    );
//
//    @Headers({CACHE, AGENT})
//    @GET("api/get_privacy_policy")
//    Call<Setting> getPrivacyPolicy(
//            @Query("api_key") String api_key
//    );


    @POST("reg.php")
        // Replace with your API endpoint
    Call<RegistrationResponse> registerUser(@Body RegistrationRequest request);
}

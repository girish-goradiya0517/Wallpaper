package com.dsquare.wallzee.Fragment;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsquare.wallzee.Activity.RingtoneActivity;
import com.dsquare.wallzee.Adapter.AdapterCategoryRingtone;
import com.dsquare.wallzee.Adapter.AdapterRingtone;
import com.dsquare.wallzee.Callback.CallbackRingtone;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Category;
import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.Constant;
import com.dsquare.wallzee.Utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RingtoneFragment extends Fragment implements AdapterRingtone.MediaCallBack {


    private RecyclerView recyclerView,recyclerViewCategory;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterRingtone adapterRingtone;
    MediaPlayer mediaPlayer;
    private AdapterCategoryRingtone adapterCategoryRingtone;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    private Call<CallbackRingtone> callbackCall = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ringtone, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerViewCategory = view.findViewById(R.id.recyclerViewCategory);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

//        recyclerViewCategory.setHasFixedSize(true);
        adapterCategoryRingtone = new AdapterCategoryRingtone(getActivity(), new ArrayList<>());
        recyclerViewCategory.setAdapter(adapterCategoryRingtone);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        recyclerView.setHasFixedSize(true);
        adapterRingtone = new AdapterRingtone(getActivity(), new ArrayList<>(),this);
        recyclerView.setAdapter(adapterRingtone);


        adapterCategoryRingtone.setOnItemClickListener((v, obj, position) -> {
           /* Intent intent = new Intent(getActivity(), RingtoneActivity.class);
            intent.putExtra("categoryName", obj.category_name);
            intent.putExtra("categoryId", obj.cid);
            startActivity(intent); */
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapterRingtone.resetListData();
            requestAction(view);
        });

        requestAction(view);
    }


    private void displayApiResult(final List<Ringtone> ringtones, List<Category> categories, View view) {
        adapterCategoryRingtone.setListData(categories);
        adapterRingtone.setListData(ringtones);
        swipeProgress(false);
        if (ringtones.size() == 0) {
            showNoItemView(true, view);
        }
    }

    private void requestCategoriesApi(View view) {
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);
        callbackCall = apiInterface.getAllRingtone(Config.DEVELOPER_NAME);
        callbackCall.enqueue(new Callback<CallbackRingtone>() {
            @Override
            public void onResponse(Call<CallbackRingtone> call, Response<CallbackRingtone> response) {
                CallbackRingtone resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    displayApiResult(resp.ringtones, resp.categories, view);
                } else {
                    onFailRequest(view);
                }
            }

            @Override
            public void onFailure(Call<CallbackRingtone> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(view);
            }
        });
    }

    private void onFailRequest(View view) {
        swipeProgress(false);
        if (Tools.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text),view);
        } else {
            showFailedView(true, getString(R.string.failed_text), view);
        }
    }

    private void requestAction(View view) {
        showFailedView(false, "", view);
        swipeProgress(true);
        showNoItemView(false, view);
        new Handler().postDelayed(() -> requestCategoriesApi(view), Constant.DELAY_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            callbackCall.cancel();
        }
    }

    private void showFailedView(boolean flag, String message, View view) {
        View lyt_failed = view.findViewById(R.id.lyt_failed_home);
        ((TextView) view.findViewById(R.id.failed_message)).setText(message);
        if (flag) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        view.findViewById(R.id.failed_retry).setOnClickListener(views -> requestAction(view));
    }

    private void showNoItemView(boolean show, View view) {
        View lyt_no_item = view.findViewById(R.id.lyt_no_item_home);
        ((TextView) view.findViewById(R.id.no_item_message)).setText(R.string.no_category_found);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            return;
        }
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
        });
    }

    @Override
    public void onChangeMedia(MediaPlayer player) {
        if (player != null){
            mediaPlayer = player;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

}
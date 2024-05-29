package com.dsquare.wallzee.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.dsquare.wallzee.Activity.WallpaperActivity;
import com.dsquare.wallzee.Adapter.AdapterCategoryFull;
import com.dsquare.wallzee.Callback.CallbackCategories;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Category;
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


public class CategoryFragment extends Fragment {


    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterCategoryFull adapterCategory;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    private Call<CallbackCategories> callbackCall = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//        recyclerView.setHasFixedSize(true);


        adapterCategory = new AdapterCategoryFull(getActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapterCategory);

        adapterCategory.setOnItemClickListener((v, obj, position) -> {
            Intent intent = new Intent(getActivity(), WallpaperActivity.class);
            intent.putExtra(EXTRA_OBJC, obj);
            startActivity(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapterCategory.resetListData();
            requestAction(view);
        });

        requestAction(view);
    }


    private void displayApiResult(final List<Category> categories, View view) {
        adapterCategory.setListData(categories);
        swipeProgress(false);
        if (categories.size() == 0) {
            showNoItemView(true, view);
        }
    }

    private void requestCategoriesApi(View view) {
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);
        callbackCall = apiInterface.getAllCategories(Config.DEVELOPER_NAME);
        callbackCall.enqueue(new Callback<CallbackCategories>() {
            @Override
            public void onResponse(Call<CallbackCategories> call, Response<CallbackCategories> response) {
                CallbackCategories resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    displayApiResult(resp.categories, view);
                    resp.categories.forEach(category -> {
                    });
                } else {
                    onFailRequest(view);
                }
            }

            @Override
            public void onFailure(Call<CallbackCategories> call, Throwable t) {
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

}
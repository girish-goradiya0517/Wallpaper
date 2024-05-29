package com.dsquare.wallzee.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dsquare.wallzee.Adapter.AdapterDuo;
import com.dsquare.wallzee.Callback.CallbackDuo;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Duo;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.Constant;
import com.dsquare.wallzee.Utils.PrefManager;
import com.dsquare.wallzee.Utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDouble extends Fragment {

    private View root_view;
    private RecyclerView recyclerView;
    private AdapterDuo adapterRecent;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackDuo> callbackCall = null;
    private int post_total = 0;
    private int failed_page = 0;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    private String[] separated;
    private PrefManager prefManager;

    String type = Constant.ADDED_NEWEST;
    RelativeLayout llLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_category, container, false);

        prefManager = new PrefManager(getActivity());

        swipeRefreshLayout = root_view.findViewById(R.id.swipe_refresh_layout_home);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        llLayout = root_view.findViewById(R.id.llLayout);

        recyclerView = root_view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
//        recyclerView.setHasFixedSize(true);

        //set data and list adapter
        adapterRecent = new AdapterDuo(getActivity(), recyclerView, new ArrayList<Duo>());
        recyclerView.setAdapter(adapterRecent);


        // detect when scroll reach bottom
        adapterRecent.setOnLoadMoreListener(current_page -> {
            if (post_total > adapterRecent.getItemCount() && current_page != 0) {
                int next_page = current_page + 1;
                requestAction(next_page);
            } else {
                adapterRecent.setLoaded();
            }
        });

        adapterRecent.setOnItemClickListener((v, obj, position) -> {

        });

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
            adapterRecent.resetListData();
            requestAction(1);
        });

        requestAction(1);

        return root_view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.type, menu);
        MenuItem typeItem = menu.findItem(R.id.action_type);

        // Set the click listener for the type menu item
        typeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                popup();
                return true;
            }

            private void popup () {
                PopupMenu popup = new PopupMenu((Context) getActivity(), llLayout);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick (MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.action_new) {
                            type = Constant.ADDED_NEWEST;
                            if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                            adapterRecent.resetListData();
                            requestAction(1);
                            return true;
                        } else if (itemId == R.id.action_pop) {
                            type = Constant.ADDED_OLDEST;
                            if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
                            adapterRecent.resetListData();
                            requestAction(1);

                            return true;
                        }
                        return false;
                    }
                });
                popup.inflate(R.menu.option);

                popup.show();
            }
        });
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayApiResult(final List<Duo> videos) {
        adapterRecent.insertData(videos);
        swipeProgress(false);
        if (videos.size() == 0 && adapterRecent.getList().isEmpty()) {
            showNoItemView(true);
        }
    }

    private void requestListPostApi(final int page_no) {

        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);

        callbackCall = apiInterface.getDuo(page_no, Config.LOAD_MORE, type, Config.DEVELOPER_NAME);

        callbackCall.enqueue(new Callback<CallbackDuo>() {
            @Override
            public void onResponse(Call<CallbackDuo> call, Response<CallbackDuo> response) {
                CallbackDuo resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    post_total = resp.count_total;
                    displayApiResult(resp.duo);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackDuo> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest(page_no);
            }

        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapterRecent.setLoaded();
        swipeProgress(false);
        if (Tools.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    private void requestAction(final int page_no) {
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            adapterRecent.setLoading();
        }
        new Handler().postDelayed(() -> requestListPostApi(page_no), Constant.DELAY_TIME);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed_home);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }
        root_view.findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction(failed_page));
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_home);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.msg_no_item);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        adapterRecent.notifyDataSetChanged();
    }

}

package com.dsquare.wallzee.Fragment;

import static com.dsquare.wallzee.Config.DEVELOPER_NAME;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.dsquare.wallzee.Activity.ProfileActivity;
import com.dsquare.wallzee.Activity.WallpaperActivity;
import com.dsquare.wallzee.Adapter.AdapterCategory;
import com.dsquare.wallzee.Adapter.AdapterPopular;
import com.dsquare.wallzee.Adapter.AdapterRecent;
import com.dsquare.wallzee.Adapter.AdapterRingtone;
import com.dsquare.wallzee.Adapter.AdapterUser;
import com.dsquare.wallzee.Adapter.SliderAdapter;
import com.dsquare.wallzee.Callback.CallbackHome;
import com.dsquare.wallzee.Callback.CallbackWallpaper;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.Model.Category;
import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.Model.Slider;
import com.dsquare.wallzee.Model.SliderModel;
import com.dsquare.wallzee.Model.Users;
import com.dsquare.wallzee.Model.Wallpaper;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.Constant;
import com.dsquare.wallzee.Utils.PrefManager;
import com.dsquare.wallzee.Utils.Tools;
import com.github.islamkhsh.CardSliderViewPager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentHome extends Fragment implements SliderAdapter.OnSliderClickListner, AdapterRingtone.MediaCallBack {

    private static final String ARG_ORDER = "order";
    private static final String ARG_FILTER = "filter";
    View root_view;

    ArrayList<Slider> sliderModels;
    private RecyclerView recyclerViewRecipe, recyclerViewCategory, recyclerViewTopCreator, recyclerViewWallpaperPopular, recyclerViewLiveWallpaper, recyclerViewRingtone;
    private AdapterRecent adapterWallpaper;
    private AdapterPopular adapterLive;
    private AdapterRingtone adapterRingtone;
    private AdapterPopular adapterPopular;
    private AdapterCategory adapterCategory;
    private AdapterUser adapterUser;
    //private RecipePagerAdapter pagerAdapter;
    private ViewPager viewPager;

    private SliderAdapter sliderAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackHome> callbackCall = null;

    private Call<SliderModel> sliderModelCall;
    private int post_total = 0;
    private int failed_page = 0;
    List<Wallpaper> items = new ArrayList<>();
    List<Wallpaper> live = new ArrayList<>();
    List<Ringtone> ringtones = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();

    List<Users> users = new ArrayList<>();
    String order, filter;
    private Timer timer;
    private TimerTask timerTask;
    private PrefManager prefManager;
    private RelativeLayout rlMain;

    private CardSliderViewPager cardSliderViewPager;

    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";

    TextView tvExplorePopular;
    TextView tvExploreLive;
    TextView tvExploreCategory;
    TextView tvExploreRingtone;
    TextView tvExploreLatest;

    private OnExploreCategoryClickListener exploreCategoryClickListener;

    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order = getArguments() != null ? getArguments().getString(ARG_ORDER) : "";
        filter = getArguments() != null ? getArguments().getString(ARG_FILTER) : "";
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnExploreCategoryClickListener) {
            exploreCategoryClickListener = (OnExploreCategoryClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnExploreCategoryClickListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root_view = inflater.inflate(R.layout.fragment_main, container, false);

        prefManager = new PrefManager(requireActivity());

        cardSliderViewPager = root_view.findViewById(R.id.ScreenSlider);
        requestSlider();
        sliderAdapter = new SliderAdapter(sliderModels, requireContext(), this);
        rlMain = root_view.findViewById(R.id.lyt_parent);
        swipeRefreshLayout = root_view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        rlMain.setVisibility(View.GONE);

        recyclerViewCategory = root_view.findViewById(R.id.recyclerViewCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategory.setLayoutManager(layoutManager);
        adapterCategory = new AdapterCategory(getActivity(), categoryList);
        recyclerViewCategory.setAdapter(adapterCategory);

        adapterCategory.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(getActivity(), WallpaperActivity.class);
            intent.putExtra(EXTRA_OBJC, obj);
            startActivity(intent);
        });


        //users
        recyclerViewTopCreator = root_view.findViewById(R.id.recyclerViewTopCreator);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTopCreator.setLayoutManager(layoutManager1);

        adapterUser = new AdapterUser(getActivity(), users);
        recyclerViewTopCreator.setAdapter(adapterUser);

        adapterUser.setOnItemClickListener((view, obj, position) -> {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("userImage", obj.creatorProfileUrl);
            intent.putExtra("tvUserName", obj.creatorName);
            intent.putExtra("userId", obj.id + "");
            startActivity(intent);
        });


        //Live

        recyclerViewLiveWallpaper = root_view.findViewById(R.id.recyclerViewLiveWallpaper);
        StaggeredGridLayoutManager layoutManager3 = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        layoutManager3.setReverseLayout(false); // Set reverseLayout as needed

        recyclerViewLiveWallpaper.setLayoutManager(layoutManager3);
        adapterLive = new AdapterPopular(getActivity(), recyclerViewLiveWallpaper, live);
        recyclerViewLiveWallpaper.setAdapter(adapterLive);


        //Ringtone
        recyclerViewRingtone = root_view.findViewById(R.id.recyclerViewRingtone);
        StaggeredGridLayoutManager layoutManager4 = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        layoutManager4.setReverseLayout(false);

        recyclerViewRingtone.setLayoutManager(layoutManager4);
        adapterRingtone = new AdapterRingtone(getActivity(), ringtones, this);
        recyclerViewRingtone.setAdapter(adapterRingtone);


        //recent wallpaper
        recyclerViewRecipe = root_view.findViewById(R.id.recyclerViewWallpaper);
        recyclerViewRecipe.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        adapterWallpaper = new AdapterRecent(getActivity(), recyclerViewRecipe, items);
        recyclerViewRecipe.setAdapter(adapterWallpaper);


        //Popular
        recyclerViewWallpaperPopular = root_view.findViewById(R.id.recyclerViewWallpaperPopular);
        StaggeredGridLayoutManager layoutManager5 = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        layoutManager3.setReverseLayout(false); // Set reverseLayout as needed

        recyclerViewWallpaperPopular.setLayoutManager(layoutManager5);
        adapterPopular = new AdapterPopular(getActivity(), recyclerViewWallpaperPopular, items);
        recyclerViewWallpaperPopular.setAdapter(adapterPopular);


        recyclerViewRecipe.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView v, int state) {
                super.onScrollStateChanged(v, state);
            }
        });

        // detect when scroll reach bottom
        adapterWallpaper.setOnLoadMoreListener(this::setLoadMore);

        // on swipe list
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (callbackCall != null && callbackCall.isExecuted()) callbackCall.cancel();
            //featured.clear();
            //resetViewPager();
            adapterCategory.resetListData();
            adapterWallpaper.resetListData();
            adapterRingtone.resetListData();
            adapterLive.resetListData();
            adapterPopular.resetListData();
            adapterUser.resetListData();
            sliderAdapter.resetListData();
            rlMain.setVisibility(View.GONE);
            requestAction(1);
            requestSlider();
        });

        requestAction(1);


        tvExplorePopular = root_view.findViewById(R.id.tvExplorePopular);
        tvExploreLive = root_view.findViewById(R.id.tvExploreLive);
        tvExploreCategory = root_view.findViewById(R.id.tvExploreCategory);
        tvExploreRingtone = root_view.findViewById(R.id.tvExploreRingtone);
        tvExploreLatest = root_view.findViewById(R.id.tvExploreLatest);

        tvExplorePopular.setOnClickListener(view -> {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExplorelatestClick();
            }
        });


        tvExploreLive.setOnClickListener(view -> {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploreLiveClick();
            }
        });

        tvExploreCategory.setOnClickListener(view -> {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploreCategoryClick();
            }
        });

        tvExploreRingtone.setOnClickListener(view -> {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploreRingtoneClick();
            }
        });

        tvExploreLatest.setOnClickListener(view -> {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExplorelatestClick();
            }
        });
        return root_view;
    }

    @Override
    public void onSliderClick(Slider slider, int position) {
        if (slider.getScreenName().toLowerCase().contains("catergory")) {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploreCategoryClick();
            }
        } else if (slider.getScreenName().toLowerCase().contains("ringtone")) {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploreRingtoneClick();
            }
        } else if (slider.getScreenName().toLowerCase().contains("live")) {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploreLiveClick();
            }
        } else if (slider.getScreenName().toLowerCase().contains("double")) {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExploredouble();
            }
        } else if (slider.getScreenName().toLowerCase().contains("wallpaper")) {
            if (exploreCategoryClickListener != null) {
                exploreCategoryClickListener.onExplorelatestClick();
            }
        }
    }

    @Override
    public void onChangeMedia(MediaPlayer player) {

    }

    public interface OnExploreCategoryClickListener {
        void onExploreCategoryClick();

        void onExploreRingtoneClick();

        void onExploreLiveClick();

        void onExplorelatestClick();

        void onExploredouble();

        void onExplorePopularClick();

    }


    public void setLoadMore(int current_page) {
        if (post_total > adapterWallpaper.getItemCount() && current_page != 0) {
            int next_page = current_page + 1;
            requestAction(next_page);
        } else {
            adapterWallpaper.setLoaded();
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private void displayApiResult(final List<Wallpaper> wallpapers, final List<Category> categories, final List<Users> users,
                                  final List<Wallpaper> live, final List<Ringtone> ringtones) {
        adapterUser.setListData(users);
        adapterCategory.setListData(categories);
        adapterWallpaper.insertDataWithNativeAd(wallpapers);
        Collections.reverse(live);
        adapterLive.insertData(live);
        adapterRingtone.setListData(ringtones);
        swipeProgress(false);
    }

    private void requestSlider() {
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);
        sliderModelCall = apiInterface.getAllSliders(DEVELOPER_NAME);
        sliderModelCall.enqueue(new Callback<SliderModel>() {
            @Override
            public void onResponse(Call<SliderModel> call, Response<SliderModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sliderAdapter = new SliderAdapter((ArrayList<Slider>) response.body().getSliderItems(), requireContext(), FragmentHome.this);
                    cardSliderViewPager.setAdapter(sliderAdapter);
                } else {
                    Toast.makeText(requireContext(), "Slider not Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SliderModel> call, Throwable t) {

            }
        });
    }

    private void requestListPostApi(final int page_no) {
        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);
        callbackCall = apiInterface.getHome(DEVELOPER_NAME);
        callbackCall.enqueue(new Callback<CallbackHome>() {
            @Override
            public void onResponse(@NonNull Call<CallbackHome> call, @NonNull Response<CallbackHome> response) {
                CallbackHome resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    rlMain.setVisibility(View.VISIBLE);
                    post_total = resp.count_total;
                    Log.d("SIZE", "onResponse: " + live.size());
                    displayApiResult(resp.recent, resp.categories, resp.users, Wallpaper.distinct(resp.live), resp.ringtone);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackHome> call, Throwable t) {
                swipeProgress(false);
            }
        });
        apiInterface.getChannel(page_no, Config.LOAD_MORE, Constant.ADDED_NEWEST, Config.DEVELOPER_NAME).enqueue(new Callback<CallbackWallpaper>() {
            @Override
            public void onResponse(Call<CallbackWallpaper> call, Response<CallbackWallpaper> response) {
                CallbackWallpaper  resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    rlMain.setVisibility(View.VISIBLE);
                    post_total = resp.count_total;
                    Log.d("SIZE", "onResponse: " + live.size());
                    adapterPopular.insertData(resp.posts);
                } else {
                    onFailRequest(page_no);
                }
            }

            @Override
            public void onFailure(Call<CallbackWallpaper> call, Throwable throwable) {
                failed_page = page_no;
                adapterPopular.setLoaded();
                swipeProgress(false);
                if (Tools.isConnect(getActivity())) {
                    showFailedView(true, getString(R.string.failed_text));
                } else {
                    showFailedView(true, getString(R.string.failed_text));
                }
            }
        });
    }

    private void onFailRequest(int page_no) {
        failed_page = page_no;
        adapterWallpaper.setLoaded();
        swipeProgress(false);
        if (Tools.isConnect(getActivity())) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.failed_text));
        }
    }

    public void requestAction(final int page_no) {
        //lyt_home_content.setVisibility(View.GONE);
        showFailedView(false, "");
        showNoItemView(false);
        if (page_no == 1) {
            swipeProgress(true);
        } else {
            adapterWallpaper.setLoading();
        }
        new Handler(Looper.getMainLooper()).postDelayed(() -> requestListPostApi(page_no), 0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed_home);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);
        if (show) {
            rlMain.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            lyt_failed.setVisibility(View.GONE);
        }
        root_view.findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction(failed_page));
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_home);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.msg_no_item);
        if (show) {
            rlMain.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
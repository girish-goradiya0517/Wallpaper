package com.dsquare.wallzee.Callback;


import com.dsquare.wallzee.Model.Category;
import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.Model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class CallbackCategoryDetails {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public Category category = null;
    public List<Wallpaper> wallpapers = new ArrayList<>();
    public List<Ringtone> ringtones = new ArrayList<>();

}

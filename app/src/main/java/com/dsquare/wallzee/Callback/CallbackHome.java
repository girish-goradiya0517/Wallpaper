package com.dsquare.wallzee.Callback;



import com.dsquare.wallzee.Model.Category;
import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.Model.Users;
import com.dsquare.wallzee.Model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class CallbackHome {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Wallpaper> recent = new ArrayList<>();
    public List<Wallpaper> popula = new ArrayList<>();
    public List<Wallpaper> live = new ArrayList<>();
    public List<Category> categories = new ArrayList<>();

    public List<Category> featured = new ArrayList<>();
    public List<Users> users = new ArrayList<>();
    public List<Ringtone> ringtone = new ArrayList<>();

}

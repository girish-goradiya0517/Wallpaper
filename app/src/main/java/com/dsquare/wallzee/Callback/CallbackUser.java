package com.dsquare.wallzee.Callback;



import com.dsquare.wallzee.Model.Ringtone;
import com.dsquare.wallzee.Model.Users;
import com.dsquare.wallzee.Model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class CallbackUser {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Wallpaper> wallpaper = new ArrayList<>();
    public List<Users> users = new ArrayList<>();
    public List<Ringtone> ringtone = new ArrayList<>();

}

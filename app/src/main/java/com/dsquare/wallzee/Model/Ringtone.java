package com.dsquare.wallzee.Model;

import java.io.Serializable;

public class Ringtone implements Serializable {

    public int id = -1;
    public String ringtone_name = "";
    public String ringtone_url = "";
    public String downloads = "";

    public RingtoneType type = RingtoneType.OTHERS;

}

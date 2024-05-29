package com.dsquare.wallzee.Model;

import java.io.Serializable;

public class Duo implements Serializable {

    public int id = -1;
    public String wall1 = "";
    public String wall2 = "";
    public int status;


    public Duo(int id, String wall1, String wall2, int status) {
        this.id = id;
        this.wall1 = wall1;
        this.wall2 = wall2;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWall1() {
        return wall1;
    }

    public void setWall1(String wall1) {
        this.wall1 = wall1;
    }

    public String getWall2() {
        return wall2;
    }

    public void setWall2(String wall2) {
        this.wall2 = wall2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

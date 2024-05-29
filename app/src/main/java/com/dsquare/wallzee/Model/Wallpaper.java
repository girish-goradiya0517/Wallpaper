package com.dsquare.wallzee.Model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Wallpaper implements Serializable {

    public String image_id;
    public String image_name;
    public String image;
    public String type;
    public int view_count;
    public int download_count;
    public int set_count;
    public String image_resolution;
    public String image_size;
    public String category_id;
    public String category_name;
    public String user_id;
    public String creatorName;
    public String creatorProfileUrl;

    public Wallpaper(String image_id, String image_name, String image, String type, int view_count, int download_count, int set_count, String image_resolution, String image_size, String category_id, String category_name, String user_id, String creatorName, String creatorProfileUrl) {
        this.image_id = image_id;
        this.image_name = image_name;
        this.image = image;
        this.type = type;
        this.view_count = view_count;
        this.download_count = download_count;
        this.set_count = set_count;
        this.image_resolution = image_resolution;
        this.image_size = image_size;
        this.category_id = category_id;
        this.category_name = category_name;
        this.user_id = user_id;
        this.creatorName = creatorName;
        this.creatorProfileUrl = creatorProfileUrl;
    }

    public Wallpaper() {

    }


    public String getId() {
        return image_id;
    }

    public void setId(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getView_count() {
        return view_count;
    }

    public void setView_count(int view_count) {
        this.view_count = view_count;
    }

    public int getDownload_count() {
        return download_count;
    }

    public void setDownload_count(int download_count) {
        this.download_count = download_count;
    }

    public int getSet_count() {
        return set_count;
    }

    public void setSet_count(int set_count) {
        this.set_count = set_count;
    }

    public String getImage_resolution() {
        return image_resolution;
    }

    public void setImage_resolution(String image_resolution) {
        this.image_resolution = image_resolution;
    }

    public String getImage_size() {
        return image_size;
    }

    public void setImage_size(String image_size) {
        this.image_size = image_size;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorProfileUrl() {
        return creatorProfileUrl;
    }

    public void setCreatorProfileUrl(String creatorProfileUrl) {
        this.creatorProfileUrl = creatorProfileUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "WallpaperImageName :"
                + getImage_name() + "\n " +
                "WallpaperType :" + getType() + "" +
                "\n WallpaperName " + getImage() +
                "\n WallpaperID " + getId() + "\n Creator Name :" + getCreatorName();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallpaper wallpaper = (Wallpaper) o;
        return Objects.equals(image_id, wallpaper.image_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image_id);
    }

    // Custom distinct function
    public static List<Wallpaper> distinct(List<Wallpaper> wallpapers) {
        Set<Wallpaper> uniqueWallpapers = new LinkedHashSet<>(wallpapers);
        return new ArrayList<>(uniqueWallpapers);
    }
}
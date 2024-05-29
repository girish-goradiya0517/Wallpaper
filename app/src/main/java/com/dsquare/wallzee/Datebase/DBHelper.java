package com.dsquare.wallzee.Datebase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.dsquare.wallzee.Model.Wallpaper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String CATEGORY_NAME = "category_name";

    private static String DATABASE_NAME = "mwp.db";
    private static int DATABASE_VERSION = 2;
    public static final String DOWNLOADS = "downloads";
    public static final String SETS = "sets";
    public static final String FEATURED = "featured";
    public static final String ID = "id";
    public static final String IMAGE_ID = "image_id";
    public static final String IMAGE_NAME = "image_name";
    public static final String IMAGE_UPLOAD = "image_upload";
    public static final String IMAGE_URL = "image_url";
    public static final String LAST_UPDATE = "last_update";
    public static final String MIME = "mime";
    public static final String RESOLUTION = "resolution";
    public static final String SIZE = "size";

    public static final String COLOR_NAME = "color_name";
    public static final String COLOR_ID = "color_id";

    public static final String TABLE_CATEGORY = "tbl_category";
    public static final String TABLE_CATEGORY_DETAIL = "tbl_category_detail";
    public static final String TABLE_FAVORITE = "tbl_favorite";
    public static final String TABLE_FEATURED = "tbl_featured";
    public static final String TABLE_GIF = "tbl_gif";
    public static final String TABLE_POPULAR = "tbl_popular";
    public static final String TABLE_RANDOM = "tbl_random";
    public static final String TABLE_RECENT = "tbl_recent";
    public static final String TAGS = "tags";
    public static final String TOTAL_WALLPAPER = "total_wallpaper";
    public static final String TYPE = "type";
    public static final String VIEWS = "views";
    private String DB_PATH;
    private final Context context;
    private SQLiteDatabase db;
    String outFileName = "";
    SharedPreferences.Editor spEdit;

    public DBHelper(Context context2) {
        super(context2, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, DATABASE_VERSION);
        this.context = context2;
        this.db = getWritableDatabase();
        Log.d("DB", "Constructor");
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.d("DB", "onCreate");
        createTableWallpaper(sQLiteDatabase, TABLE_RECENT);
        createTableWallpaper(sQLiteDatabase, TABLE_FEATURED);
        createTableWallpaper(sQLiteDatabase, TABLE_POPULAR);
        createTableWallpaper(sQLiteDatabase, TABLE_RANDOM);
        createTableWallpaper(sQLiteDatabase, TABLE_GIF);
        createTableWallpaper(sQLiteDatabase, TABLE_FAVORITE);
        createTableWallpaper(sQLiteDatabase, TABLE_CATEGORY_DETAIL);
        createTableCategory(sQLiteDatabase, TABLE_CATEGORY);
    }

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_recent");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_featured");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_popular");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_random");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_gif");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_favorite");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_category_detail");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS tbl_category");
        createTableWallpaper(sQLiteDatabase, TABLE_RECENT);
        createTableWallpaper(sQLiteDatabase, TABLE_FEATURED);
        createTableWallpaper(sQLiteDatabase, TABLE_POPULAR);
        createTableWallpaper(sQLiteDatabase, TABLE_RANDOM);
        createTableWallpaper(sQLiteDatabase, TABLE_GIF);
        createTableWallpaper(sQLiteDatabase, TABLE_FAVORITE);
        createTableWallpaper(sQLiteDatabase, TABLE_CATEGORY_DETAIL);
        createTableCategory(sQLiteDatabase, TABLE_CATEGORY);
    }

    public void truncateTableWallpaper(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + str);
        createTableWallpaper(this.db, str);
    }

    public void truncateTableCategory(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + str);
        createTableCategory(this.db, str);
    }

    private void createTableCategory(SQLiteDatabase sQLiteDatabase, String str) {
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY_ID + " TEXT, " + CATEGORY_NAME + " TEXT, " + CATEGORY_IMAGE + " TEXT, " + TOTAL_WALLPAPER + " TEXT )");
    }

    private void createTableWallpaper(SQLiteDatabase sQLiteDatabase, String str) {
        sQLiteDatabase.execSQL("CREATE TABLE " + str + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + IMAGE_ID + " TEXT, " + IMAGE_NAME + " TEXT, " + IMAGE_UPLOAD + " TEXT, " + IMAGE_URL + " TEXT, " + TYPE + " TEXT, " + RESOLUTION + " TEXT, " + SIZE + " TEXT, " + MIME + " TEXT, views INTEGER, " + DOWNLOADS + " INTEGER, " + SETS + " INTEGER, " + TAGS + " TEXT, " + CATEGORY_ID + " TEXT, " + CATEGORY_NAME + " TEXT, " + COLOR_ID + " TEXT, " + COLOR_NAME + " TEXT, " + LAST_UPDATE + " TEXT )");
    }

    public void onOpen(SQLiteDatabase sQLiteDatabase) {
        super.onOpen(sQLiteDatabase);
        if (Build.VERSION.SDK_INT >= 16) {
            sQLiteDatabase.disableWriteAheadLogging();
        }
    }

//    public void addListCategory(List<Category> list, String str) {
//        for (Category category : list) {
//            addOneCategory(this.db, category, str);
//        }
//        getAllCategory(str);
//    }
//
//    public void addListWallpaper(List<Wallpaper> list, String str) {
//        for (Wallpaper wallpaper : list) {
//            addOneWallpaper(this.db, wallpaper, str);
//        }
//        getAllWallpaper(str);
//    }

//    public void addOneCategory(SQLiteDatabase sQLiteDatabase, Category category, String str) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(CATEGORY_ID, category.category_id);
//        contentValues.put(CATEGORY_NAME, category.category_name);
//        contentValues.put(CATEGORY_IMAGE, category.category_image);
//        contentValues.put(TOTAL_WALLPAPER, category.total_wallpaper);
//        sQLiteDatabase.insert(str, null, contentValues);
//    }
//
//    public void addOneWallpaper(SQLiteDatabase sQLiteDatabase, Wallpaper wallpaper, String str) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(IMAGE_ID, wallpaper.image_id);
//        contentValues.put(IMAGE_NAME, wallpaper.image_name);
//        contentValues.put(IMAGE_UPLOAD, wallpaper.image_upload);
//        contentValues.put(IMAGE_URL, wallpaper.image_url);
//        contentValues.put(TYPE, wallpaper.type);
//        contentValues.put(RESOLUTION, wallpaper.resolution);
//        contentValues.put(SIZE, wallpaper.size);
//        contentValues.put(MIME, wallpaper.mime);
//        contentValues.put("views", Integer.valueOf(wallpaper.views));
//        contentValues.put(DOWNLOADS, Integer.valueOf(wallpaper.downloads));
//        contentValues.put(FEATURED, wallpaper.featured);
//        contentValues.put(TAGS, wallpaper.tags);
//        contentValues.put(CATEGORY_ID, wallpaper.category_id);
//        contentValues.put(CATEGORY_NAME, wallpaper.category_name);
//        contentValues.put(LAST_UPDATE, wallpaper.last_update);
//        sQLiteDatabase.insert(str, null, contentValues);
//    }

    public void addOneFavorite(Wallpaper wallpaper) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(IMAGE_ID, wallpaper.image_id);
        contentValues.put(IMAGE_NAME, wallpaper.image_name);
        contentValues.put(IMAGE_UPLOAD, wallpaper.image);
        contentValues.put(IMAGE_URL, wallpaper.image);
        contentValues.put(TYPE, wallpaper.type);

        contentValues.put(RESOLUTION, wallpaper.category_name);
        contentValues.put(SIZE, wallpaper.category_name);
        contentValues.put(MIME, wallpaper.category_name);

        contentValues.put("views", Integer.valueOf(wallpaper.view_count));
        contentValues.put(DOWNLOADS, Integer.valueOf(wallpaper.download_count));
        contentValues.put(SETS, Integer.valueOf(wallpaper.set_count));

        contentValues.put(TAGS, wallpaper.image_name);
        contentValues.put(CATEGORY_ID, wallpaper.category_id);
        contentValues.put(CATEGORY_NAME, wallpaper.category_name);

        contentValues.put(COLOR_ID, wallpaper.image_name);
        contentValues.put(COLOR_NAME, wallpaper.image_name);

        contentValues.put(LAST_UPDATE, wallpaper.category_name);

        this.db.insert(TABLE_FAVORITE, null, contentValues);
    }

    public void deleteAll(String str) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("DELETE FROM " + str);
    }

    public void deleteWallpaperByCategory(String str, String str2) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("DELETE FROM " + str + " WHERE " + CATEGORY_ID + " = " + str2);
    }

    public void deleteFavorites(Wallpaper wallpaper) {
        SQLiteDatabase sQLiteDatabase = this.db;
        sQLiteDatabase.delete(TABLE_FAVORITE, "image_id = ?", new String[]{wallpaper.image_id + ""});
    }

//    public List<Category> getAllCategory(String str) {
//        return getAllCategories(str);
//    }

    public List<Wallpaper> getAllWallpaper(String str) {
        return getAllWallpapers(str);
    }

    public List<Wallpaper> getAllWallpaperByCategory(String str, String str2) {
        return getAllWallpapersByCategory(str, str2);
    }

    public ArrayList<Wallpaper> getAllFavorite(String str) {
        return getAllFavorites(str);
    }




//    private List<Category> getAllCategories(String str) {
//        SQLiteDatabase sQLiteDatabase = this.db;
//        return getAllCategoryFormCursor(sQLiteDatabase.rawQuery("SELECT * FROM " + str + " ORDER BY id ASC", null));
//    }

    private List<Wallpaper> getAllWallpapers(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        return getAllWallpaperFormCursor(sQLiteDatabase.rawQuery("SELECT * FROM " + str + " ORDER BY id ASC LIMIT 100", null));
    }

    private List<Wallpaper> getAllWallpapersByCategory(String str, String str2) {
        SQLiteDatabase sQLiteDatabase = this.db;
        return getAllWallpaperFormCursor(sQLiteDatabase.rawQuery("SELECT * FROM " + str + " WHERE " + CATEGORY_ID + " = " + str2 + " ORDER BY id ASC LIMIT 100", null));
    }


//
    private ArrayList<Wallpaper> getAllFavorites(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        return getAllWallpaperFormCursor(sQLiteDatabase.rawQuery("SELECT * FROM " + str + " ORDER BY id DESC", null));
    }

    public boolean isFavoritesExist(String str) {
        SQLiteDatabase sQLiteDatabase = this.db;
        Cursor rawQuery = sQLiteDatabase.rawQuery("SELECT * FROM tbl_favorite WHERE image_id = ?", new String[]{str + ""});
        int count = rawQuery.getCount();
        rawQuery.close();
        if (count > 0) {
            return true;
        }
        return false;
    }


//    @SuppressLint("Range")
//    private List<Category> getAllCategoryFormCursor(Cursor cursor) {
//        ArrayList arrayList = new ArrayList();
//        if (cursor.moveToFirst()) {
//            do {
//                Category category = new Category();
//                category.category_id = cursor.getString(cursor.getColumnIndex(CATEGORY_ID));
//                category.category_name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
//                category.category_image = cursor.getString(cursor.getColumnIndex(CATEGORY_IMAGE));
//                category.total_wallpaper = cursor.getString(cursor.getColumnIndex(TOTAL_WALLPAPER));
//                arrayList.add(category);
//            } while (cursor.moveToNext());
//        }
//        return arrayList;
//    }

    @SuppressLint("Range")
    private ArrayList<Wallpaper> getAllWallpaperFormCursor(Cursor cursor) {
        ArrayList arrayList = new ArrayList();
        if (cursor.moveToFirst()) {
            do {
                Wallpaper wallpaper = new Wallpaper();
                wallpaper.image_id = cursor.getString(cursor.getColumnIndex(IMAGE_ID));
                wallpaper.image_name = cursor.getString(cursor.getColumnIndex(IMAGE_NAME));
                wallpaper.image = cursor.getString(cursor.getColumnIndex(IMAGE_UPLOAD));
                wallpaper.image = cursor.getString(cursor.getColumnIndex(IMAGE_URL));


                wallpaper.type = cursor.getString(cursor.getColumnIndex(TYPE));
                wallpaper.category_name = cursor.getString(cursor.getColumnIndex(SIZE));
                wallpaper.category_name = cursor.getString(cursor.getColumnIndex(MIME));

                wallpaper.view_count = cursor.getInt(cursor.getColumnIndex("views"));
                wallpaper.download_count = cursor.getInt(cursor.getColumnIndex(DOWNLOADS));
                wallpaper.set_count = cursor.getInt(cursor.getColumnIndex(SETS));
                wallpaper.image_name = cursor.getString(cursor.getColumnIndex(TAGS));
                wallpaper.category_id = cursor.getString(cursor.getColumnIndex(CATEGORY_ID));
                wallpaper.category_name = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));

                wallpaper.image_name = cursor.getString(cursor.getColumnIndex(COLOR_ID));
                wallpaper.image_name = cursor.getString(cursor.getColumnIndex(COLOR_NAME));

                wallpaper.category_name = cursor.getString(cursor.getColumnIndex(LAST_UPDATE));
                arrayList.add(wallpaper);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }
}

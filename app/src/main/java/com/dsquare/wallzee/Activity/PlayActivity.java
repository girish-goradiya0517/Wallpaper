package com.dsquare.wallzee.Activity;

import static com.dsquare.wallzee.Utils.Methods.getRealPathFromURI;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Utils.AdsManager;
import com.dsquare.wallzee.Utils.AdsPref;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    TextView tvCategory;
    String TAG = "PlayActivity";
    ImageView ivCategory;
    ProgressBar progressBar;
    ImageView ivShare;
    ImageView ivFav;
    ImageView ivDownload;

    String name, url,temp;
    RelativeLayout llBackground;

    private MediaPlayer mediaPlayer;
    private ImageView playPauseButton;
    private boolean isPlaying = false;

    private PlayerView playerView;
    private ExoPlayer player;

    File pathfile;
    File outputfile;

    private long downloadId;
    private BroadcastReceiver downloadReceiver;
    private static final int REQUEST_WRITE_SETTINGS = 1;

    boolean setRingtone = false;
    boolean isShare = false;
    boolean isDownload = false;

    private AdsManager adsManager;
    private AdsPref adsPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(name);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        adsPref = new AdsPref(this);
        adsManager = new AdsManager(this);
        adsManager.loadBannerAd(true);
        adsManager.loadInterstitialAd(true, adsPref.getInterstitialAdInterval());


        tvCategory = findViewById(R.id.tvCategory);
        playPauseButton = findViewById(R.id.ivCategory);
        progressBar = findViewById(R.id.progressBar);
        ivShare = findViewById(R.id.ivShare);
        ivFav = findViewById(R.id.ivFav);
        ivDownload = findViewById(R.id.ivDownload);
        llBackground = findViewById(R.id.playerCircle);

        playerView = findViewById(R.id.playerView);

        tvCategory.setText(name);

        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == id) {
                    int status = getDownloadStatus(id);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        //statusText.setText("Download Successful");
                        //int colorAccent = ContextCompat.getColor(context, R.color.colorSecondary);
                        //statusText.setTextColor(colorAccent);
                        getDownloadedVideoInfo(downloadId);
                        //postLayout.setVisibility(View.VISIBLE);

                    }
                }
            }
        };

        //registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // Set media item
        Uri mediaUri = Uri.parse(Config.WEBSITE_URL + "ringtone/" + url);
        MediaItem mediaItem = MediaItem.fromUri(mediaUri);
        player.setMediaItem(mediaItem);

        // Prepare the player
        player.prepare();

        // Add completion listener
        player.addListener(new Player.Listener() {
            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    // Handle playback started
                    progressBar.setVisibility(View.VISIBLE);
                    playPauseButton.setImageResource(R.drawable.pause_icon);
                } else {
                    // Handle playback stopped
                    progressBar.setVisibility(View.GONE);
                    playPauseButton.setImageResource(R.drawable.round_play_circle_outline_24);

                }
            }
        });

        // Play button click listener
        playPauseButton = findViewById(R.id.ivCategory);
        playPauseButton.setOnClickListener(v -> {
            if (player.isPlaying()) {
                // Pause playback
                player.pause();
                progressBar.setVisibility(View.GONE);
                playPauseButton.setImageResource(R.drawable.round_play_circle_outline_24);
            } else {
                // Start playback
                player.play();
                progressBar.setVisibility(View.VISIBLE);
                playPauseButton.setImageResource(R.drawable.pause_icon);
            }
        });


        // Create an array of possible start and end colors for your gradients
        int[] startColors = {Color.parseColor("#FFB6C1"), Color.parseColor("#FFD700"), Color.parseColor("#98FB98"), Color.parseColor("#87CEEB")};
        int[] endColors = {Color.parseColor("#FF69B4"), Color.parseColor("#FFA500"), Color.parseColor("#90EE90"), Color.parseColor("#ADD8E6")};
        int randomStartColor = startColors[new Random().nextInt(startColors.length)];
        int randomEndColor = endColors[new Random().nextInt(endColors.length)];
        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{randomStartColor, randomEndColor}
        );
        gradientDrawable.setCornerRadius(0);

        llBackground.setBackground(gradientDrawable);

        pathfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        //pathfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getResources().getString(R.string.app_name));
        outputfile = new File(pathfile, url);


        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShare = true;
                downloadVideo(Config.WEBSITE_URL + "ringtone/" + url);
            }
        });


        ivDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDownload = true;
                downloadVideo(Config.WEBSITE_URL + "ringtone/" + url);
            }
        });

        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRingtone = true;
                downloadVideo(Config.WEBSITE_URL + "ringtone/" + url);
            }
        });


    }

    private int getDownloadStatus(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            cursor.close();
            return status;
        }
        cursor.close();
        return DownloadManager.ERROR_UNKNOWN;
    }

    private void startDownload(String url) {
        // Get the Downloads directory
        File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create an instance of DownloadFileAsync and execute it
        new DownloadFileAsync(downloadsDirectory).execute(url);
    }

    class DownloadFileAsync extends AsyncTask<String, Integer, String> {

        private File outputDirectory; // specify your desired output directory
        private File outputFile;

        public DownloadFileAsync(File outputDirectory) {
            this.outputDirectory = outputDirectory;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Initialize the output file
            outputFile = new File(outputDirectory, "your_audio_file.mp3");

            // Add any additional setup or UI changes as needed
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {
                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lengthOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Length of file: " + lengthOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(outputFile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress((int) ((total * 100) / lengthOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            Log.d("ANDRO_ASYNC", progress[0].toString());
            // Update your progress UI if needed
        }

        @Override
        protected void onPostExecute(String unused) {
            // Perform any actions after download completion
            // For example, you can trigger the ringtone setting here
        }
    }


    @SuppressLint("Range")
    private void downloadVideo(String videoUrl) {
        Uri uri = Uri.parse(videoUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Get the custom download directory
        //File customDownloadDirectory = getCustomDownloadDirectory();

        String targetPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/" + getString(R.string.app_name);
        File customDownloadDirectory = new File(targetPath);


        // Set the download destination path and file name
        String memeTypeString;
        if (videoUrl.contains(".mp3")) {
            memeTypeString = ".mp3";
        } else {
            memeTypeString = ".jpg";
        }
        String fileName = getString(R.string.app_name) + "_" + System.currentTimeMillis() + memeTypeString;
        File targetFile = new File(customDownloadDirectory, fileName);
        request.setDestinationUri(Uri.fromFile(targetFile));
        temp = targetFile.getAbsolutePath();
        // Set other optional configurations
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setTitle(fileName);
        request.setDescription("Please wait...");

        // Get the download service and enqueue the request
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);

        new Thread(() -> {

            boolean downloading = true;
            while (downloading) {

                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(downloadId);
                Cursor cursor = downloadManager.query(q);
                cursor.moveToFirst();
                @SuppressLint("Range") int bytes_downloaded = cursor.getInt(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false;

                }

                final int dl_progress = (int) ((bytes_downloaded * 100L) / bytes_total);


                runOnUiThread(() -> {
                    //tvCategory.setText("Download File : "+String.valueOf(dl_progress)+"%");
                    if (dl_progress == 100) {

                        if (isDownload) {
                            Toast.makeText(this, "Download Successful", Toast.LENGTH_SHORT).show();
                            isDownload = false;
                        }

                        if (setRingtone) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!Settings.System.canWrite(this)) {
                                    // If the app does not have permission, request it
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                    intent.setData(Uri.parse("package:" + getPackageName()));
                                    startActivityForResult(intent, REQUEST_WRITE_SETTINGS);
                                } else {
                                    // The app already has permission, proceed with setting the ringtone
                                    setRingtone(outputfile);
                                }
                            } else {
                                // For versions below Android 6.0, no runtime permission is needed
                                setRingtone(outputfile);
                            }
                            setRingtone = false;
                        }

                    }
                });
                cursor.close();
            }
        }).start();
    }


    @SuppressLint("Range")
    private void getDownloadedVideoInfo(long downloadId) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                String localUriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                long timestamp = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));


                if (localUriString != null) {
                    Uri localUri = Uri.parse(localUriString);
                    String filePath = localUri.getPath();
                    File videoFile = new File(filePath);
                    long fileSizeBytes = videoFile.length();

                    // Convert the file size to KB or MB
                    double fileSizeKB = fileSizeBytes / 1024.0;
                    double fileSizeMB = fileSizeKB / 1024.0;

                    // Display the file size in the appropriate unit (KB or MB)
                    String fileSizeText;
                    if (fileSizeMB >= 1.0) {
                        fileSizeText = String.format("%.2f MB", fileSizeMB);
                    } else {
                        fileSizeText = String.format("%.2f KB", fileSizeKB);
                    }

                    if (isShare) {
                        Uri localUri1 = Uri.parse(localUriString);

                        // Create a File object using the Uri
                        File fileToShare = new File(localUri1.getPath());

                        // Log the file path for debugging
                        Log.d("YMG", "File path: " + fileToShare.getAbsolutePath());

                        // Check if the file exists before sharing
                        if (fileToShare.exists()) {
                            shareFile(fileToShare);
                        } else {
                            // Handle the case where the file doesn't exist
                            Toast.makeText(PlayActivity.this, "File not found", Toast.LENGTH_SHORT).show();
                        }
                        isShare = false;
                    }

                }
            }
        }
        cursor.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
    private String getMimeType(String fileUrl) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void setRingtone(File audioFile) {
        try {
            File file = new File(temp);
            int indexOfDot = file.getName().indexOf('.');
            String substringBeforeDot = file.getName().substring(0, indexOfDot);
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.TITLE, substringBeforeDot);
            values.put(MediaStore.MediaColumns.MIME_TYPE, getMimeType(file.getAbsolutePath()));
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Uri newUri = this.getContentResolver()
                        .insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
                try (OutputStream os = getContentResolver().openOutputStream(newUri)) {

                    int size = (int) file.length();
                    byte[] bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(Files.newInputStream(file.toPath()));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();

                        os.write(bytes);
                        os.close();
                        os.flush();
                    } catch (IOException e) {
                        Log.d(TAG, "setRingtone: IOException "+e);
                    }
                } catch (Exception ignored) {
                    Log.d(TAG, "setRingtone: Exception "+ignored);
                }
                RingtoneManager.setActualDefaultRingtoneUri(PlayActivity.this,RingtoneManager.TYPE_RINGTONE,
                        newUri);
                Toast.makeText(this, "Ringtone Set SuccessFully", Toast.LENGTH_SHORT).show();

            } else {
                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());

                Uri uri = MediaStore.Audio.Media.getContentUriForPath(file
                        .getAbsolutePath());

                getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + file.getAbsolutePath() + "\"", null);


                Uri newUri = PlayActivity.this.getContentResolver().insert(uri, values);
                RingtoneManager.setActualDefaultRingtoneUri(PlayActivity.this, RingtoneManager.TYPE_RINGTONE,
                        newUri);
                Toast.makeText(this, "Ringtone Set SuccessFully", Toast.LENGTH_SHORT).show();
                this.getContentResolver()
                        .insert(Objects.requireNonNull(MediaStore.Audio.Media.getContentUriForPath(file
                                .getAbsolutePath())), values);

            }

        } catch (Exception e) {
            Log.e(TAG, "setRingtone: Error setting ringtone", e);
            Toast.makeText(this, "Ringtone Set Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    private String getMimeType(File file) {
        String mimeType = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        if (extension != null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }



    private void shareFile1(File file) {
        if (file.exists()) {
            Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            // Change "video/*" to "image/*" for image files
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share File"));
        } else {
            // Handle the case when the file does not exist
            Toast.makeText(this, "File not found.", Toast.LENGTH_SHORT).show();
        }
        Log.d("YMG1", file.getAbsolutePath());
    }

    private void shareFile(File file) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*"); // Set the MIME type to audio
        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

        // Grant read permission to other apps
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share Audio File"));
    }


    private void pauseAudio() {
        mediaPlayer.pause();
        isPlaying = false;
        progressBar.setVisibility(View.GONE);
        playPauseButton.setImageResource(R.drawable.round_play_circle_outline_24);
    }


    @Override
    public void onBackPressed() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.release();
            }
        }
        super.onStop();
    }
}
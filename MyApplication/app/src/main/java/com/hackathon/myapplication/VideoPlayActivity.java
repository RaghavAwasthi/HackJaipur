package com.hackathon.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hackathon.myapplication.elearn.E_LearnModel;

import java.io.File;
import java.io.IOException;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class VideoPlayActivity extends AppCompatActivity {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    VideoView vdView;
    AppCompatTextView title, date, description, creator_name;
    SimpleExoPlayer player;
    PlayerView playerView;
    File localFile = null;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        Intent i = getIntent();
        E_LearnModel em = (E_LearnModel) i.getExtras().get("MODEL");
        playerView = findViewById(R.id.video_view);
        ;
        StorageReference islandRef = storage.getReference("app_videos/" + em.getPathspec() + em.getDate());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        }
        vdView = findViewById(R.id.vdview);
        title = findViewById(R.id.title_name);
        date = findViewById(R.id.upload_date);
        description = findViewById(R.id.description);
        title.setText(em.getTitle());
        date.setText(Utils.getDateTime(em.getDate()));
        description.setText(em.getDescription());
        initializePlayer(Uri.parse(em.getUrl()));
        try {
            localFile = File.createTempFile(em.getPathspec() + em.getDate(), "mp4");
            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    playVideoPlayer();
                    Toast.makeText(VideoPlayActivity.this, "Video Loaded", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {

        }


    }

    private void initializePlayer(Uri uri) {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-codelab");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    public void playVideoPlayer() {
        vdView.setVideoPath(localFile.getAbsolutePath());
        MediaController mediaController = new MediaController(this);
        vdView.start();
        vdView.setMediaController(mediaController);
        mediaController.setAnchorView(vdView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localFile.delete();
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}

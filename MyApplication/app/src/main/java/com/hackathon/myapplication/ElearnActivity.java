package com.hackathon.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.myapplication.elearn.E_LearnModel;

import java.util.UUID;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class ElearnActivity extends AppCompatActivity {
    int RC_VIDEO_PICKER = 125;

    EditText videoname, video_description;
    Button mChooseFile, mUploadFile;
    ImageView thumbnail_view;
    Uri imageUri;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mVideoStorageReference;
    private ProgressDialog progressBar;
    StorageReference videoref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elearn);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
            getApplication().setTheme(R.style.AppTheme_Dark);

        }

        videoname = findViewById(R.id.video_name);
        video_description = findViewById(R.id.video_description);
        thumbnail_view = findViewById(R.id.thumbnail_view);
        mChooseFile = findViewById(R.id.mchoose_file);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you cannot cancel
        progressBar.setMessage("Processing");
        mChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_VIDEO_PICKER);
            }
        });
        mUploadFile = findViewById(R.id.submit_button);
        mVideoStorageReference = mFirebaseStorage.getReference().child("app_videos");
        mUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.show();
                videoref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                E_LearnModel em = new E_LearnModel();
                                em.setUid(UUID.randomUUID().toString());
                                em.setTitle(videoname.getText().toString());
                                em.setDescription(video_description.getText().toString());
                                em.setUrl(uri.toString());
                                em.setPathspec(imageUri.getLastPathSegment());
                                em.setDate(Utils.getCurrentDateTime());
                                db.collection(ConstantsUtils.ELEARN).add(em).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        progressBar.hide();
                                        Toast.makeText(ElearnActivity.this, "Video Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });
                    }
                })
                ;

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_VIDEO_PICKER && resultCode == RESULT_OK) {
            imageUri = data.getData();

            Bitmap bmp123 = BitmapFactory.decodeFile(imageUri.getPath());
            // Log.d("url",imageUri.getPath());
            Toast.makeText(getApplicationContext(), imageUri.getPath(), Toast.LENGTH_LONG).show();
            thumbnail_view.setImageBitmap(bmp123);
//            Bitmap bmp = ThumbnailUtils.createVideoThumbnail(imageUri.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
//            thumbnail_view.setImageBitmap(bmp);
            videoref = mVideoStorageReference.child(imageUri.getLastPathSegment() + Utils.getCurrentDateTime());


        }
    }
}

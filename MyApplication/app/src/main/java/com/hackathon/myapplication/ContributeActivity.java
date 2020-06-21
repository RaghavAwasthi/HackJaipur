package com.hackathon.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class ContributeActivity extends AppCompatActivity {
    EditText foodDetails, userloc, userContact;
    int IMAGE_FROM_GALLERY = 508;
    int IMAGE_FROM_CAMERA = 507;
    int CAMERA_PERMISSION_CODE = 102;
    int RC_PHOTO_PICKER = 101;
    int foodtype = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mChatPhotosStorageReference;
    String urlimage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FusedLocationProviderClient fusedLocationClient;
    private ProgressDialog progressBar;
    private ImageView imageView;
    Button upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
            getApplication().setTheme(R.style.AppTheme_Dark);


        }
        setContentView(R.layout.contribute_food);
        foodDetails = findViewById(R.id.foodDetails);
        userloc = findViewById(R.id.userloc);
        userContact = findViewById(R.id.userContact);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you cannot cancel
        progressBar.setMessage("Processing");
        imageView = findViewById(R.id.my_image);
        upload = findViewById(R.id.upload);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                userloc.setText(address);
                                String city = addresses.get(0).getLocality();
                                userloc.setText(city);
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
                            } catch (IOException e) {
                                Toast.makeText(ContributeActivity.this, "Unknown Error Occured while retrieving location", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });


        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");
        AppCompatButton submit = findViewById(R.id.submit);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<EditText> list = new ArrayList<>();
                list.add(userContact);
                list.add(userloc);
                list.add(foodDetails);
                if (validate(list)) {
                    progressBar.show();
                    ContributionFoodModel fm = new ContributionFoodModel();
                    fm.setFoodname(foodDetails.getText().toString());
                    fm.setLocation(userloc.getText().toString());
                    fm.setFoodimage_url(urlimage);
                    fm.setUser(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    fm.setType(foodtype);
                    fm.setId(UUID.randomUUID().toString());
                    fm.setNumber(Long.parseLong(userContact.getText().toString()));
                    fm.setDate(Utils.getCurrentDateTime());
                    db.collection(ConstantsUtils.CONTRIBUTION).document(mAuth.getCurrentUser().getEmail()).collection(ConstantsUtils.CONTRIBUTION).add(fm).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            DocumentReference reference = db.collection(ConstantsUtils.VOLUNTEER).document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            reference.update("contributionCount", FieldValue.increment(1));
                            db.collection("CONTRIBUTIONPOOL").document(fm.getId()).set(fm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ContributeActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), SuccessActivity.class);
                                    i.putExtra(ConstantsUtils.MODEL, fm);
                                    progressBar.hide();
                                    startActivity(i);
                                    finish();
                                }
                            });

                        }
                    });

                }


            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            upload.setBackgroundColor(getResources().getColor(R.color.fui_linkColor));
            upload.setText("Uploaded");
            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
            progressBar.show();
            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    progressBar.hide();
                                    Uri downloadUrl = uri;
                                    urlimage = downloadUrl.toString();
                                    Glide.with(getApplicationContext()).load(urlimage).into(imageView);
                                    Toast.makeText(ContributeActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                }
                            });

                            // Set the download URL to the message box, so that the user can send it to the database

                        }
                    });
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radio_liq:
                if (checked)
                    // Pirates are the best
                    foodtype = 0;
                break;
            case R.id.radio_dry:
                if (checked)
                    // Ninjas rule
                    foodtype = 1;
                break;

            case R.id.radio_mix:
                if (checked)
                    foodtype = 2;
                break;
        }

    }

    public boolean validate(List<EditText> edittextlist) {
        boolean flag = true;
        for (EditText appCompatEditText : edittextlist) {
            if (appCompatEditText == null || appCompatEditText.getText().toString().length() < 1) {
                appCompatEditText.setError("Field is required");
                flag = false;
            }
            if (userContact.getText().toString().length() != 10) {
                userContact.setError("INVALID LENGTH of Number");
                flag = false;

            }


        }
        return flag;
    }

}



package com.hackathon.myapplication;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class PostSignUpActivity extends AppCompatActivity {
    EditText name, userAge, userAddress, userCity, userContact, userState;
    Button signup;
    ImageView volunteerImage;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "PostSignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
            getApplication().setTheme(R.style.AppTheme_Dark);

        }
        setContentView(R.layout.create_account_activity);
        name = findViewById(R.id.username);
        userAge = findViewById(R.id.userage);
        userAddress = findViewById(R.id.userAddress);
        userCity = findViewById(R.id.userCity);
        userContact = findViewById(R.id.userContact);
        volunteerImage = findViewById(R.id.imageView);
        userState = findViewById(R.id.user_state);
        signup = findViewById(R.id.signup);
        name.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        List<EditText> list = new ArrayList<>();
        list.add(name);
        list.add(userAge);
        list.add(userCity);
        list.add(userContact);
        list.add(userAddress);
        list.add(userState);
        final ProgressDialog progressBar = new ProgressDialog(getApplicationContext());
        ;
        Glide.with(this).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(volunteerImage);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate(list)) {
                    VolunteerModel vm = new VolunteerModel();
                    vm.setAccountStatus(0);
                    vm.setUsername(name.getText().toString());
                    vm.setAddress(userAddress.getText().toString());
                    vm.setAge(Integer.parseInt(userAge.getText().toString()));
                    vm.setState(userState.getText().toString());
                    vm.setMobileNumber(Long.parseLong(userContact.getText().toString()));
                    vm.setId(UUID.randomUUID().toString());
                    vm.setCity(userCity.getText().toString());
                    DocumentReference reference = db.collection(ConstantsUtils.VOLUNTEER).document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    reference.set(vm).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(PostSignUpActivity.this, "Sign up Successfull", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostSignUpActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onFailure: " + e);

                            finish();
                        }
                    });
                }
            }
        });


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

package com.hackathon.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackathon.myapplication.about.AboutActivity;
import com.hackathon.myapplication.contribute.ContributeFragment;
import com.hackathon.myapplication.elearn.ElearnFragment;
import com.hackathon.myapplication.home.HomeFragment;
import com.hackathon.myapplication.profile.ProfileFragment;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Arrays;

import static com.hackathon.myapplication.ConstantsUtils.ACCOUNTSTATUS;
import static com.hackathon.myapplication.ConstantsUtils.CONTRIBUTIONCOUNT;
import static com.hackathon.myapplication.ConstantsUtils.EMAIL;
import static com.hackathon.myapplication.ConstantsUtils.MOBILE;
import static com.hackathon.myapplication.ConstantsUtils.VOLUNTEERAGE;
import static com.hackathon.myapplication.ConstantsUtils.VOLUNTEERNAME;
import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    int RC_SIGN_IN = 1254;
    FirebaseAuth mAuth;
    private MainActivityViewModel mViewModel;
    private BottomNavigationView navigationView;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private Menu mOptionsMenu;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
            getApplication().setTheme(R.style.AppTheme_Dark);

        }
        setContentView(R.layout.activity_main);
        AndroidThreeTen.init(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);


        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            signIn();
        }
        fab = findViewById(R.id.scan_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ContributeActivity.class);
                startActivity(i);
            }
        });

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mViewModel.getSelectedNavItem().observe(this, this::displayNavigationItem);
        navigationView.setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            selectInitialNavigationItem();
        }

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    signIn();
            }
        });


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        mViewModel.setmSelectedNavItem(MainActivityViewModel.MainActivityNavigationItem.HOME);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        final MainActivityViewModel.MainActivityNavigationItem employeeNavigationItem;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                employeeNavigationItem = MainActivityViewModel.MainActivityNavigationItem.HOME;
                break;
            case R.id.nav_e_learn:
                employeeNavigationItem = MainActivityViewModel.MainActivityNavigationItem.ELEARN;
                break;
            case R.id.nav_profile:
                employeeNavigationItem = MainActivityViewModel.MainActivityNavigationItem.PROFILE;
                break;
            case R.id.nav_contribution:
                employeeNavigationItem = MainActivityViewModel.MainActivityNavigationItem.CONTRIBUTION;
                break;
            default:
                Log.e(TAG, "onNavigationItemSelected: Navigation item not resolved", new IllegalArgumentException());
                throw new IllegalArgumentException();
        }
        mViewModel.setmSelectedNavItem(employeeNavigationItem);

        return true;
    }

    public void selectInitialNavigationItem() {
        int initialItem = R.id.nav_home;
        onNavigationItemSelected(navigationView.getMenu().findItem(initialItem));
        navigationView.setSelectedItemId(initialItem);
    }

    private void displayNavigationItem(MainActivityViewModel.MainActivityNavigationItem employeeNavigationItem) {
        MainFragment newFragment;
        fab.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(200);
        switch (employeeNavigationItem) {

            case HOME:
                fab.animate().cancel();
                fab.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(200);
                newFragment = new HomeFragment();
                break;
            case ELEARN:
                newFragment = new ElearnFragment();
                break;

            case PROFILE:
                fab.animate().scaleX(0f).scaleY(0f).alpha(0f).setDuration(200);
                newFragment = new ProfileFragment();
                break;
            case CONTRIBUTION:
                newFragment = new ContributeFragment();
                break;
            default:
                Log.e(TAG, "displayNavigationItem: name not resolved", new IllegalArgumentException());
                throw new IllegalArgumentException();
        }
        if (newFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, newFragment)
                    .commit();
            setTitle(newFragment.getTitle());

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mOptionsMenu = menu;
        return true;
    }

    public void signIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            // already signed in
        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder().setLogo(R.mipmap.ic_launcher)
                            .setIsSmartLockEnabled(false, true)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
            // not signed in
        }
    }

    public void signOut() {

        AuthUI.getInstance().signOut(getApplicationContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                preferences = getSharedPreferences(ConstantsUtils.PREFERENCENAME, MODE_PRIVATE);
                preferences.edit().clear().commit();


                Toast.makeText(MainActivity.this, "Logout Successfull", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_signout:
                signOut();
                break;
            case R.id.about:
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.darktheme:
                SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                boolean useDarkTheme = preferences.getBoolean(AboutActivity.PREF_DARK_THEME, false);
                if (!useDarkTheme) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putBoolean(PREF_DARK_THEME, true);
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putBoolean(PREF_DARK_THEME, false);
                    editor.apply();
                }
                Intent restarter = getIntent();
                finish();
                startActivity(restarter);

        }
        return true;
    }

    public void initFab() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof ContributeFragment) {

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ContributeActivity.class);
                    startActivity(i);
                }
            });
            fab.setImageResource(R.drawable.ic_equipment);
            fab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
        } else if (fragment instanceof ElearnFragment) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(), ElearnActivity.class);
                    startActivity(i);
                }
            });
            fab.setImageResource(R.drawable.ic_camera);
            fab.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection(ConstantsUtils.VOLUNTEER).document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {


                        if (documentSnapshot.exists()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            VolunteerModel volunteerModel = documentSnapshot.toObject(VolunteerModel.class);
                            editor.putString(EMAIL, volunteerModel.getEmail());
                            editor.putString(MOBILE, Long.toString(volunteerModel.getMobileNumber()));
                            editor.putString(VOLUNTEERNAME, volunteerModel.getUsername());
                            editor.putInt(VOLUNTEERAGE, volunteerModel.getAge());
                            editor.putInt(ACCOUNTSTATUS, volunteerModel.getAccountStatus());
                            editor.putInt(CONTRIBUTIONCOUNT, volunteerModel.getContributionCount());
                            editor.apply();
                            Intent i = new Intent(getApplicationContext(), PostSignUpActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(i);

                        } else {
                            Intent i = new Intent(getApplicationContext(), PostSignUpActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplicationContext().startActivity(i);
                        }

                        Toast.makeText(MainActivity.this, documentSnapshot.toString(), Toast.LENGTH_SHORT).show();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error occured" + e, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: " + e);
                    }
                });


            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, "Sgn in is required for performing any operation", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(Toast.LENGTH_LONG);
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();

                }

                if (response != null) {
                    if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "No Network Found", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(Toast.LENGTH_LONG);
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                    Log.e("MainActivity", "Sign-in error: ", response.getError());
                }
            }
        }
    }
}

package com.hackathon.myapplication.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hackathon.myapplication.ConstantsUtils;
import com.hackathon.myapplication.ContributionFoodModel;
import com.hackathon.myapplication.MainFragment;
import com.hackathon.myapplication.R;
import com.hackathon.myapplication.Utils;
import com.hackathon.myapplication.VolunteerModel;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class HomeFragment extends MainFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView view1;

    @Override
    public int getTitle() {
        return R.string.home;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            getActivity().setTheme(R.style.AppTheme_BG);
            view.findViewById(R.id.holder).setBackgroundColor(getResources().getColor(R.color.black));

        }

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AppCompatTextView contributioncount = view.findViewById(R.id.contribution_count);
        if (mAuth.getCurrentUser() != null) {
            db.collection(ConstantsUtils.VOLUNTEER).document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        VolunteerModel vm = documentSnapshot.toObject(VolunteerModel.class);
                        contributioncount.setText(Integer.toString(vm.getContributionCount()));
                    }

                }
            });


        }
        view1 = view.findViewById(R.id.list);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Query query = FirebaseFirestore.getInstance().collection("CONTRIBUTIONPOOL").orderBy("date", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<ContributionFoodModel> options = new FirestorePagingOptions.Builder<ContributionFoodModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ContributionFoodModel.class)
                .build();

        FirestorePagingAdapter<ContributionFoodModel, ContributionViewHolder> adapter = new FirestorePagingAdapter<ContributionFoodModel, ContributionViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContributionViewHolder holder, int position, @NonNull ContributionFoodModel model) {

                holder.equipmentName.setText(model.getFoodname());
                holder.location.setText(model.getLocation());
                holder.serviceDate.setText(Utils.getDateTime(model.getDate()));
                holder.mobile.setText(Long.toString(model.getNumber()));
            }

            @NonNull
            @Override
            public ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contribution_item_home, parent, false);
                return new ContributionViewHolder(view);
            }
        };

        view1.setAdapter(adapter);
        view1.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    class ContributionViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView equipmentName, location, serviceDate, mobile;


        public ContributionViewHolder(@NonNull View itemView) {
            super(itemView);
            equipmentName = itemView.findViewById(R.id.food_name);
            location = itemView.findViewById(R.id.location);
            serviceDate = itemView.findViewById(R.id.date);
            mobile = itemView.findViewById(R.id.mobile);

        }
    }
}

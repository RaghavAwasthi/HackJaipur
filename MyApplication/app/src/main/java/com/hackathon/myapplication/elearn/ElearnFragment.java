package com.hackathon.myapplication.elearn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hackathon.myapplication.ConstantsUtils;
import com.hackathon.myapplication.MainActivity;
import com.hackathon.myapplication.MainFragment;
import com.hackathon.myapplication.R;
import com.hackathon.myapplication.VideoPlayActivity;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

public class ElearnFragment extends MainFragment {
    @Override
    public int getTitle() {
        return R.string.e_learn;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_e_learn, container, false);
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.initFab();
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        AdView mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        RecyclerView view1 = view.findViewById(R.id.list);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Query query = FirebaseFirestore.getInstance().collection(ConstantsUtils.ELEARN).orderBy("date", Query.Direction.DESCENDING);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        FirestorePagingOptions<E_LearnModel> options = new FirestorePagingOptions.Builder<E_LearnModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, E_LearnModel.class)
                .build();
        FirestorePagingAdapter<E_LearnModel, ElearnViewHolder> adapter = new FirestorePagingAdapter<E_LearnModel, ElearnViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ElearnViewHolder holder, int position, @NonNull E_LearnModel model) {
                holder.videoName.setText(model.getTitle());
                holder.videoName.setTag(model);
                holder.videoDescription.setText(model.getDescription());
                holder.videoName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), VideoPlayActivity.class);
                        i.putExtra("MODEL", (E_LearnModel) v.getTag());
                        startActivity(i);

                    }
                });

            }

            @NonNull
            @Override
            public ElearnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.e_learn_item, parent, false);
                return new ElearnViewHolder(view);
            }
        };
        view1.setAdapter(adapter);
        view1.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    class ElearnViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView videoName, videoDescription;
        ImageView thumbnail;
        MaterialCardView cell;

        public ElearnViewHolder(@NonNull View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.Video_Name);
            videoDescription = itemView.findViewById(R.id.video_description);
            thumbnail = itemView.findViewById(R.id.raster_bmp);
            cell=itemView.findViewById(R.id.calls_card_cell);


        }
    }
}

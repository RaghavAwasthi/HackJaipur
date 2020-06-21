package com.hackathon.myapplication.contribute;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hackathon.myapplication.ConstantsUtils;
import com.hackathon.myapplication.ContributionFoodModel;
import com.hackathon.myapplication.MainActivity;
import com.hackathon.myapplication.MainFragment;
import com.hackathon.myapplication.R;
import com.hackathon.myapplication.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContributeFragment extends MainFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ContributeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contribute, container, false);
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.initFab();
        }

        RecyclerView view1 = view.findViewById(R.id.list);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Query query = FirebaseFirestore.getInstance().collection(ConstantsUtils.CONTRIBUTION).document(mAuth.getCurrentUser().getEmail()).collection(ConstantsUtils.CONTRIBUTION).orderBy("date", Query.Direction.DESCENDING);
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
            }

            @NonNull
            @Override
            public ContributionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contributionitem, parent, false);
                return new ContributionViewHolder(view);
            }
        };

        view1.setAdapter(adapter);
        view1.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public int getTitle() {
        return R.string.contribution;
    }

    class ContributionViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView equipmentName, location, serviceDate;
        AppCompatImageView imageview;

        public ContributionViewHolder(@NonNull View itemView) {
            super(itemView);
            equipmentName = itemView.findViewById(R.id.food_name);
            location = itemView.findViewById(R.id.location);
            serviceDate = itemView.findViewById(R.id.date);
            imageview = itemView.findViewById(R.id.view_button);

        }
    }
}

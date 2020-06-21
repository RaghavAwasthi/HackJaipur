package com.hackathon.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


public class ContributorFragment extends MainFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ContributorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contributor2, container, false);

        TextView tmp = view.findViewById(R.id.tmp);

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
                if (tmp.getVisibility() == View.VISIBLE) {
                    tmp.setVisibility(View.GONE);
                }
                holder.equipmentName.setText(model.getFoodname());
                holder.equipmentName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference ref=db.collection("CONTRIBUTIONPOOL").document(model.getId());
                        Toast.makeText(getContext(), "delete start", Toast.LENGTH_SHORT).show();
                        ref.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext() , "Delete Successfull", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                });
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
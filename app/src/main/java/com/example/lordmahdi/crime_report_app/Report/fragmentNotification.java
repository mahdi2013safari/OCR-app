package com.example.lordmahdi.crime_report_app.Report;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.Report.classes.frag_home_class_items;
import com.example.lordmahdi.crime_report_app.Report.classes.frag_notify_adapter;
import com.example.lordmahdi.crime_report_app.Report.classes.frag_notify_items;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentNotification extends Fragment {


    View rootView;
    List<frag_notify_items> items;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<frag_home_class_items,fragmentHome.fragHome_ViewHolder> adapter;

    public fragmentNotification() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fragment_notification, container, false);

        items =  new ArrayList<>();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.frag_notify_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("new_report")
                .limitToLast(10);// count of show items in recycler adapter

        FirebaseRecyclerOptions<frag_home_class_items> options =
                new FirebaseRecyclerOptions.Builder<frag_home_class_items>()
                        .setQuery(query, frag_home_class_items.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<frag_home_class_items, fragmentHome.fragHome_ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(fragmentHome.fragHome_ViewHolder holder, int position, frag_home_class_items model) {

            }

            @Override
            public fragmentHome.fragHome_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.frag_notif_adapter, parent, false);
                return new fragmentHome.fragHome_ViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        return rootView;
    }
    public static class fragHome_ViewHolder extends RecyclerView.ViewHolder{
        TextView title ;
        TextView desc;
        ImageView imageReport;

        public fragHome_ViewHolder(View Itemview){
            super(Itemview);
            title = (TextView)itemView.findViewById(R.id.frag_notify_ReportTitle);
            desc = (TextView) itemView.findViewById(R.id.frag_notify_adapter_showDescription);
            imageReport = (ImageView) itemView.findViewById(R.id.frag_nofitiy_imageReport);
        }
    }

}

package com.example.lordmahdi.crime_report_app.Report;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.Report.classes.frag_home_class_adapter;
import com.example.lordmahdi.crime_report_app.Report.classes.frag_home_class_items;
import com.example.lordmahdi.crime_report_app.helper.DbHelper;
import com.example.lordmahdi.crime_report_app.helper.ParseImage_endcode;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class fragmentHome extends Fragment {


    frag_home_class_adapter class_adapter;
    List<frag_home_class_items> list_home;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference myRef ;
    View rootView;
    Bitmap bitmap= null;
    ParseImage_endcode encode;
    frag_home_class_items class_items;
    FirebaseRecyclerAdapter<frag_home_class_items,fragHome_ViewHolder> adapter;
    ProgressDialog progressDialog;
    DbHelper helper;
    public fragmentHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        class_items = new frag_home_class_items();

        rootView =  inflater.inflate(R.layout.fragment_fragment_home, container, false);
        encode = new ParseImage_endcode();
        helper = new DbHelper(getActivity());
        TextView checknet = (TextView) rootView.findViewById(R.id.checkNet);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Witing ..");
        recyclerView = (RecyclerView) rootView.findViewById(R.id.frag_home_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("new_report");

        //retriveData();

        if(isConnected()){
            checknet.setText("");
        }else{
            final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage("Check your internet and try Again!!");
            alert.setTitle("Internet Connection");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alert.setCancelable(true);
                }
            });
            alert.show();
        }

        showRecyclerView();//Method adapter load items from firebase

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        return rootView;
    }
    public static class fragHome_ViewHolder extends RecyclerView.ViewHolder{
        View view ;
        TextView title ;
        TextView desc;
        TextView date;
        ImageView imageView;
        ImageButton btnImgMenu;
        public fragHome_ViewHolder(View Itemview){
            super(Itemview);
            title = (TextView)itemView.findViewById(R.id.frag_home_adapter_txtTitle);
            desc = (TextView) itemView.findViewById(R.id.frag_home_adapter_txtDescription);
            imageView=(ImageView)itemView.findViewById(R.id.frag_home_adapter_imageView);
            date = (TextView) itemView.findViewById(R.id.frag_home_adapter_txtDate);
            btnImgMenu = (ImageButton) itemView.findViewById(R.id.btnImageReportMenu);
        }
    }

    public void showRecyclerView(){
        final String phonehelper=helper.getPhoneLastUserDBlocal();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("new_report_user").child(helper.getPhoneLastUserDBlocal())
                .limitToLast(2);// count of show items in recycler adapter

        FirebaseRecyclerOptions<frag_home_class_items> options =
                new FirebaseRecyclerOptions.Builder<frag_home_class_items>()
                        .setQuery(query, frag_home_class_items.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<frag_home_class_items, fragHome_ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final fragHome_ViewHolder holder, final int position, frag_home_class_items model) {
                    holder.title.setText(model.getTitle());
                    holder.desc.setText(model.getDescription());
                    holder.date.setText(model.getCurrentdate());
                    String pathUri = model.getImage();
                    Uri uri = Uri.parse(pathUri);
                    holder.imageView.setImageURI(uri);
                    holder.btnImgMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final PopupMenu popup = new PopupMenu(getActivity(), holder.btnImgMenu);
                            popup.getMenuInflater().inflate(R.menu.menu_report, popup.getMenu());
                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    int id = item.getItemId();
                                    if (id == R.id.menu_report_delete) {
                                        adapter.getRef(position).removeValue();
                                    }
                                    return true;
                                }
                            });
                            popup.show();
                        }
                    });
                    Picasso.with(getActivity()).load(uri).resize(100,100).centerCrop().into(holder.imageView);

            }

            @Override
            public fragHome_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_home_adapter, parent, false);
                return new fragHome_ViewHolder(view);
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();// when internet is ON show items
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();// when internet is OFF do not show items
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

}

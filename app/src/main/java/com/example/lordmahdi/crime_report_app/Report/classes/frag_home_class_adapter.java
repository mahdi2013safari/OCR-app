package com.example.lordmahdi.crime_report_app.Report.classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lordmahdi.crime_report_app.R;
import com.example.lordmahdi.crime_report_app.helper.ParseImage_endcode;

import java.io.IOException;
import java.util.List;

/**
 * Created by Lord Mahdi on 11/1/2017.
 */

public class frag_home_class_adapter extends RecyclerView.Adapter<frag_home_class_adapter.frag_home_Holder> {

    ParseImage_endcode encode = new ParseImage_endcode();
    Context context;
    List<frag_home_class_items> list ;
    Bitmap bitmap= null;

    @Override
    public frag_home_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_home_adapter,null);
        frag_home_Holder home_holder = new frag_home_Holder(view);
        return home_holder;
    }


    public frag_home_class_adapter(Context con,List<frag_home_class_items> homeList){
        this.context = con;
        this.list = homeList;
    }

    @Override
    public void onBindViewHolder(frag_home_Holder holder, int position) {
        frag_home_class_items itemsClass = list.get(position);
        holder.title.setText(itemsClass.getTitle());
        holder.desc.setText(itemsClass.getDescription());
        holder.date.setText(itemsClass.getCurrentdate());

    }

    @Override
    public int getItemCount() {
        int arr = 0;
        try{
            if(list.size()==0){
                arr = 0;
            }
            else{
                arr=list.size();
            }
        }catch (Exception e){
            Log.e("Error",e.getMessage());
        }
        return arr;
    }

    class frag_home_Holder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView title, desc , date;
        public frag_home_Holder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.frag_home_adapter_imageView);
            title = (TextView) itemView.findViewById(R.id.frag_home_adapter_txtTitle);
            desc = (TextView) itemView.findViewById(R.id.frag_home_adapter_txtDescription);
            date = (TextView) itemView.findViewById(R.id.frag_home_adapter_txtDate);
        }
    }

}

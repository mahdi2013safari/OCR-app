package com.example.lordmahdi.crime_report_app.Report.classes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lordmahdi.crime_report_app.R;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Lord Mahdi on 11/1/2017.
 */

public class frag_notify_adapter extends RecyclerView.Adapter<frag_notify_adapter.frag_notify_ViewHolder> {

    Context context;
    List<frag_notify_items> list;

    public frag_notify_adapter(Context context, List<frag_notify_items> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public frag_notify_ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.frag_notif_adapter,null);
        frag_notify_ViewHolder holder = new frag_notify_ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(frag_notify_ViewHolder holder, int position) {
        frag_notify_items items = list.get(position);
        holder.reportNumber.setText(items.getReportNumber());
        holder.contentTitle.setText(items.getTitle());
        holder.descReplay.setText(items.getDesc());

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    class frag_notify_ViewHolder extends RecyclerView.ViewHolder{

        TextView reportNumber , contentTitle, descReplay;

        public frag_notify_ViewHolder(View itemView) {
            super(itemView);
            reportNumber = (TextView) itemView.findViewById(R.id.frag_notify_adapter_showReportID);
            contentTitle = (TextView) itemView.findViewById(R.id.frag_notify_ReportTitle);
            descReplay = (TextView) itemView.findViewById(R.id.frag_notify_adapter_showDescription);

        }
    }

}

package com.example.lordmahdi.crime_report_app.Report.classes;

/**
 * Created by Lord Mahdi on 11/1/2017.
 */

public class frag_notify_items {

    String title;
    String ReportNumber;
    String desc;


    public frag_notify_items(String title, String reportNumber, String desc) {
        this.title = title;
        ReportNumber = reportNumber;
        this.desc = desc;
    }

    public String getTitle() {
        return title;
    }

    public String getReportNumber() {
        return ReportNumber;
    }

    public String getDesc() {
        return desc;
    }
}

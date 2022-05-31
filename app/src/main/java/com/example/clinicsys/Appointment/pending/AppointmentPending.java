package com.example.clinicsys.Appointment.pending;

public class AppointmentPending {
    private String category_name, sub_cat, schedule;
    public AppointmentPending(String category_name, String sub_cat, String schedule){

        this.category_name = category_name;
        this.sub_cat = sub_cat;
        this.schedule = schedule;
    }

    public String getCategory() {
        return category_name;
    }

    public void setCategory(String category_name) {
        this.category_name = category_name;
    }

    public String getSub_cat() {
        return sub_cat;
    }

    public void setSub_cat(String sub_cat) {
        this.sub_cat = sub_cat;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

//    public AppointmentPendingCancel(String category_name, String sub_cat, String schedule){
//
//        this.category_name = category_name;
//        this.sub_cat = sub_cat;
//        this.schedule = schedule;
//    }



}

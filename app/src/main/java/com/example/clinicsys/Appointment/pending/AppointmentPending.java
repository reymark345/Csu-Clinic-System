package com.example.clinicsys.Appointment.pending;

public class AppointmentPending {
    private String idd,userId,category_name, sub_cat, schedule;


    public AppointmentPending(String idd,String userId, String category_name, String sub_cat, String schedule){

        this.idd = idd;
        this.userId = userId;
        this.category_name = category_name;
        this.sub_cat = sub_cat;
        this.schedule = schedule;
    }

    public String getIdd() {
        return idd;
    }

    public void setIdd(String idd) {
        this.idd = idd;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

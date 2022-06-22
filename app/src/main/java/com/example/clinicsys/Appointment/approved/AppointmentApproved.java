package com.example.clinicsys.Appointment.approved;

public class AppointmentApproved {
    private String idd,userId,category_name, sub_cat, schedule, patient_name;


    public AppointmentApproved(String idd, String userId, String category_name, String sub_cat, String schedule, String patient_name){

        this.idd = idd;
        this.userId = userId;
        this.category_name = category_name;
        this.sub_cat = sub_cat;
        this.schedule = schedule;
        this.patient_name = patient_name;
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

    public String getPatientName() {
        return patient_name;
    }

    public void setPatientName(String patient_name) {
        this.patient_name = patient_name;
    }
}

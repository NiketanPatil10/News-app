package com.example.finalnews;

public class ReadWriteuserDetails {
    public String fullName,email,doB,gender,mobile;

    public ReadWriteuserDetails(){

    }

    public ReadWriteuserDetails(String textFullName,String textEmail,String textDoB,String textGender, String textMobile){
        this.email = textEmail;
        this.fullName =textFullName;
        this.doB =textDoB;
        this.gender =textGender;
        this.mobile =textMobile;

    }
}

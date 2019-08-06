package com.example.myapplication;

public class Contacts {
    public String name, status, Image;

    public Contacts()
    {

    }

    public Contacts(String name, String status, String image) {
        this.name = name;
        this.status = status;
        Image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}

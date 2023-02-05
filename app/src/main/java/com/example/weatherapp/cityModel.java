package com.example.weatherapp;

public class cityModel {
    String city;
    String weather;
    int image;

    public cityModel(String city, String weather, int image) {
        this.city = city;
        this.weather = weather;
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public String getWeather() {
        return weather;
    }

    public int getImage() {
        return image;
    }

}

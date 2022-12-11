package com.example.viverocom.Clases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatosUbicacion {

    private List<Weather> weather;
    private HashMap<String,String> main;

    public HashMap<String, String> getMain() {
        return main;
    }

    public void setMain(HashMap<String, String> main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}

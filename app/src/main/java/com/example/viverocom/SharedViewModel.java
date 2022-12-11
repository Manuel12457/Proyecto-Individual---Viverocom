package com.example.viverocom;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.viverocom.Clases.OrdenDetalle;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<ArrayList<OrdenDetalle>> carrito = new MutableLiveData<>();

    public void setCarrito(ArrayList<OrdenDetalle> input) {
        carrito.setValue(input);
    }

    public MutableLiveData<ArrayList<OrdenDetalle>> getCarrito() {
        return carrito;
    }

}

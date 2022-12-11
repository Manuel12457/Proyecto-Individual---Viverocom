package com.example.viverocom.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.viverocom.Adapters.HomeAdapter;
import com.example.viverocom.Adapters.ListaProductosAdapter;
import com.example.viverocom.Clases.Home;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavHomeBinding;
import com.example.viverocom.databinding.FragmentNavProductosBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class nav_home extends Fragment {

    DatabaseReference databaseReference;
    HomeAdapter adapterHome;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;

    ArrayList<Home> listaHome;
    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private FragmentNavHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_nav_home,container,false);
        binding = FragmentNavHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.idRecyclerHome);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaHome = new ArrayList<>();
        adapterHome = new HomeAdapter(listaHome, getContext());
        recyclerView.setAdapter(adapterHome);
        databaseReference = FirebaseDatabase.getInstance().getReference("productos");
        valueEventListener = databaseReference.addValueEventListener(new nav_home.listener());

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaHome.clear();

                ArrayList<Producto> listaRecientes = new ArrayList<>();
                ArrayList<Producto> listaPlantas = new ArrayList<>();
                ArrayList<Producto> listaCuidados = new ArrayList<>();
                ArrayList<Producto> listaHerramientas = new ArrayList<>();
                ArrayList<Producto> listaDecoracion = new ArrayList<>();

                Date currentTime = Calendar.getInstance().getTime();
                Calendar calFiltro = Calendar.getInstance();
                try {
                    calFiltro.setTime(sf.parse(sf.format(currentTime)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calFiltro.add(Calendar.DATE, -1);

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Producto producto = ds.getValue(Producto.class);

                    Calendar calProducto = Calendar.getInstance();
                    try {
                        calProducto.setTime(sf.parse(producto.getFechaCreacion()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (producto.isEstado()) {

                        if (calProducto.getTime().after(calFiltro.getTime())) {
                            listaRecientes.add(producto);
                        }

                        if (producto.getTipo().equals("Planta de interior") || producto.getTipo().equals("Planta de exterior")) {
                            listaPlantas.add(producto);
                        } else if (producto.getTipo().equals("Cuidado de la planta")) {
                            listaCuidados.add(producto);
                        } else if (producto.getTipo().equals("Herramientas")) {
                            listaHerramientas.add(producto);
                        } else if (producto.getTipo().equals("Adorno para jardín")) {
                            listaDecoracion.add(producto);
                        }
                    }

                }

                if (!listaRecientes.isEmpty()) {
                    Home homeRecientes = new Home();
                    homeRecientes.setTitulo("Productos recientes");
                    homeRecientes.setListaProductos(listaRecientes);
                    listaHome.add(homeRecientes);
                }

                Home homePlantas = new Home();
                homePlantas.setTitulo("Nuestras plantas");
                homePlantas.setListaProductos(listaPlantas);
                listaHome.add(homePlantas);
                Home homeCuidados = new Home();
                homeCuidados.setTitulo("Todo para su cuidado");
                homeCuidados.setListaProductos(listaCuidados);
                listaHome.add(homeCuidados);
                Home homeHerramientas = new Home();
                homeHerramientas.setTitulo("Herramientas para su jardín");
                homeHerramientas.setListaProductos(listaHerramientas);
                listaHome.add(homeHerramientas);
                Home homeDecoracion = new Home();
                homeDecoracion.setTitulo("Decore su jardín");
                homeDecoracion.setListaProductos(listaDecoracion);
                listaHome.add(homeDecoracion);

//                if (listaDispositivos.isEmpty()) {
//                    recyclerView.setVisibility(View.INVISIBLE);
//                    spinnera.setVisibility(View.INVISIBLE);
//                    noregistro.setVisibility(View.VISIBLE);
//                } else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    spinnera.setVisibility(View.VISIBLE);
//                    noregistro.setVisibility(View.INVISIBLE);
//                }
                Log.d("tamanio", "listaHome: " + listaHome.size());
                Log.d("tamanio", "ListaPlantas: " + listaPlantas.size());
                Log.d("tamanio", "ListaCuidados: " + listaCuidados.size());
                Log.d("tamanio", "ListaHerramientas: " + listaHerramientas.size());
                Log.d("tamanio", "ListaAdorno: " + listaDecoracion.size());
                adapterHome.notifyDataSetChanged();
            } else {
                listaHome.clear();
                adapterHome.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseReference.removeEventListener(valueEventListener);
        binding = null;
    }

}
package com.example.viverocom.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.viverocom.Adapters.ConsultaDetalleAdapter;
import com.example.viverocom.Adapters.HistorialDetalleAdapter;
import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.Clases.Orden;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavDetalleconsultaBinding;
import com.example.viverocom.databinding.FragmentNavHistorialdetalleBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class nav_historialdetalle extends Fragment {

    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    Query query;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Orden> listaOrdenes;
    ArrayList<OrdenDetalle> listaOrdenesDetalle;
    ArrayList<Producto> listaProductos;

    DatabaseReference databaseReference;
    HistorialDetalleAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;
    View view;

    Producto producto;
    String id;

    private FragmentNavHistorialdetalleBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNavHistorialdetalleBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        Bundle bundle = this.getArguments();
        id = bundle.getString("id");

        firebaseDatabase = FirebaseDatabase.getInstance();
        recyclerView = view.findViewById(R.id.idDetalleOrden);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaOrdenes = new ArrayList<>();
        adapter = new HistorialDetalleAdapter(listaOrdenes, listaOrdenesDetalle, listaProductos, getContext());
        recyclerView.setAdapter(adapter);

        query = FirebaseDatabase.getInstance().getReference("ordenes").orderByChild("id").equalTo(id);
        valueEventListener = query.addValueEventListener(new nav_historialdetalle.listener());

        return view;
    }

    public void aceptarOrden() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setMessage("¿Desea procesar la orden?");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        DatabaseReference ref = firebaseDatabase.getReference().child("ordenes").child(id);
                        ref.child("estado").setValue("PROCESADA")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Navigation.findNavController(view).navigate(R.id.nav_historial);
                                        //SU SNACKBAR
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void cancelarOrden() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setMessage("¿Desea cancelar la orden?");
        builder.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //FECHA ACTUAL
                        Date currentTime = Calendar.getInstance().getTime();
                        Calendar cal = Calendar.getInstance();
                        try {
                            cal.setTime(sf.parse(sf.format(currentTime)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //FECHA ACTUAL

                        //FECHA LIMITE USUARIO
                        Calendar calLimiteUsuario = Calendar.getInstance();
                        try {
                            calLimiteUsuario.setTime(sf.parse(listaOrdenes.get(0).getFechaRecojo()));
                            calLimiteUsuario.add(Calendar.HOUR_OF_DAY, -1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //FECHA LIMITE USUARIO

                        //FECHA LIMITE ADMIN
                        Calendar calLimiteAdmin = Calendar.getInstance();
                        try {
                            calLimiteAdmin.setTime(sf.parse(listaOrdenes.get(0).getFechaRecojo()));
                            calLimiteAdmin.add(Calendar.DATE, 1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        //FECHA LIMITE ADMIN

                        //if (calLimite#.getTime().after(cal.getTime())) {}
                        DatabaseReference ref = firebaseDatabase.getReference().child("ordenes").child(id);
                        ref.child("estado").setValue("CANCELADA")
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Navigation.findNavController(view).navigate(R.id.nav_historial);
                                        //SU SNACKBAR
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });
        builder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public Producto obtenerProducto(String id) {

        String url = "https://viverocom-9a343-default-rtdb.firebaseio.com/productos/" + id + "/.json";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Gson gson = new Gson();
                    producto = gson.fromJson(response, Producto.class);
                },
                error -> Log.e("data", error.getMessage()));
        queue.add(stringRequest);

        return producto;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaOrdenes.clear();
                listaOrdenesDetalle.clear();
                listaProductos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Orden orden = ds.getValue(Orden.class);
                    listaOrdenes.add(orden);
                }

                for (Orden o : listaOrdenes) {

                    //Conseguir OrdenesDetalle que tengan el id de la orden
                    FirebaseDatabase.getInstance().getReference("ordenesdetalle").orderByChild("idOrden").equalTo(o.getId())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            OrdenDetalle ordenDetalle = ds.getValue(OrdenDetalle.class);
                                            listaOrdenesDetalle.add(ordenDetalle);
                                        }

                                        //Conseguir Productos que tengan el id de las ordenesDetalle
                                        for (OrdenDetalle od : listaOrdenesDetalle) {
                                            listaProductos.add(obtenerProducto(od.getIdProducto()));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }

                adapter.notifyDataSetChanged();
            } else {
                listaOrdenes.clear();
                listaOrdenesDetalle.clear();
                listaProductos.clear();
                adapter.notifyDataSetChanged();
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
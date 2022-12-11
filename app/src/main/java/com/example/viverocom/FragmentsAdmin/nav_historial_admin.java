package com.example.viverocom.FragmentsAdmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.viverocom.Adapters.ListaHistorialAdapter;
import com.example.viverocom.AdaptersAdmin.ListaHistorialAdapterAdmin;
import com.example.viverocom.Clases.Orden;
import com.example.viverocom.Fragments.nav_historial;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavHistorialAdminBinding;
import com.example.viverocom.databinding.FragmentNavHistorialBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class nav_historial_admin extends Fragment {

    ArrayList<Orden> listaOrdenes;
    DatabaseReference databaseReference;
    ListaHistorialAdapterAdmin adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;
    TextView nohistorial;

    boolean todaslasordenes = true;
    boolean ultimas24h = false;
    boolean ultimasemana = false;
    boolean ultimomes = false;
    String fecha;

    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private FragmentNavHistorialAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_nav_historial, container, false);
        binding = FragmentNavHistorialAdminBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.idRecyclerHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaOrdenes = new ArrayList<>();
        adapter = new ListaHistorialAdapterAdmin(listaOrdenes, getContext());
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("ordenes");
        valueEventListener = databaseReference.addValueEventListener(new nav_historial_admin.listener());

        nohistorial = view.findViewById(R.id.noHistorial);

        TextInputLayout spinnera = view.findViewById(R.id.spinner_filtro_historial);
        AutoCompleteTextView spinner = view.findViewById(R.id.idFiltroHistorial);
        String[] fechasarray = getResources().getStringArray(R.array.fechas);
        spinner.setText(fechasarray[0], false);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fecha = spinner.getText().toString();
                if (fecha.equals("Todas las consultas")) {
                    todaslasordenes = true;
                    ultimas24h = false;
                    ultimasemana = false;
                    ultimomes = false;
                    valueEventListener = databaseReference.addValueEventListener(new nav_historial_admin.listener());
                } else if (fecha.equals("Últimas 24h")) {
                    todaslasordenes = false;
                    ultimas24h = true;
                    ultimasemana = false;
                    ultimomes = false;
                    valueEventListener = databaseReference.addValueEventListener(new nav_historial_admin.listener());
                } else if (fecha.equals("Última semana")) {
                    todaslasordenes = false;
                    ultimas24h = false;
                    ultimasemana = true;
                    ultimomes = false;
                    valueEventListener = databaseReference.addValueEventListener(new nav_historial_admin.listener());
                } else if (fecha.equals("Último mes")) {
                    todaslasordenes = false;
                    ultimas24h = false;
                    ultimasemana = false;
                    ultimomes = true;
                    valueEventListener = databaseReference.addValueEventListener(new nav_historial_admin.listener());
                }
            }
        });

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaOrdenes.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Orden orden = ds.getValue(Orden.class);

                    //FECHA CONSULTA
                    Calendar calConsulta = Calendar.getInstance();
                    try {
                        calConsulta.setTime(sf.parse(orden.getFechaEmision()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //FECHA CONSULTA

                    //FECHA FILTRO
                    Date currentTime = Calendar.getInstance().getTime();
                    Calendar calFiltro = Calendar.getInstance();
                    try {
                        calFiltro.setTime(sf.parse(sf.format(currentTime)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (ultimas24h) {
                        Log.d("filtro", "ULTIMAS 24H");
                        calFiltro.add(Calendar.DATE, -1);
                    } else if (ultimasemana) {
                        Log.d("filtro", "ULTIMA SEMANA");
                        calFiltro.add(Calendar.DATE, -7);
                    } else if (ultimomes) {
                        Log.d("filtro", "ULTIMO MES");
                        calFiltro.add(Calendar.DATE, -30);
                    }
                    //FECHA FILTRO


                    if (todaslasordenes) {
                        listaOrdenes.add(orden);
                    } else {
                        if (calConsulta.getTime().after(calFiltro.getTime())) {
                            Log.d("filtro", "CUMPLE FILTRO");
                            listaOrdenes.add(orden);
                        }
                    }

                }

                if (listaOrdenes.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    nohistorial.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    nohistorial.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            } else {
                listaOrdenes.clear();
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
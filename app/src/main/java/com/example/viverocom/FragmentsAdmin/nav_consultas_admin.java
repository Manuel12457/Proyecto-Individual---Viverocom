package com.example.viverocom.FragmentsAdmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.viverocom.Adapters.ListaConsultasAdapter;
import com.example.viverocom.AdaptersAdmin.ListaConsultasAdapterAdmin;
import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.Fragments.nav_consultas;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavConsultasAdminBinding;
import com.example.viverocom.databinding.FragmentNavConsultasBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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

public class nav_consultas_admin extends Fragment {

    ArrayList<Consulta> listaConsultas;
    DatabaseReference databaseReference;
    ListaConsultasAdapterAdmin adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;
    TextView noconsultas;

    boolean todaslasconsultas = true;
    boolean ultimas24h = false;
    boolean ultimasemana = false;
    boolean ultimomes = false;
    String fecha;

    SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private FragmentNavConsultasAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_nav_consultas, container, false);
        binding = FragmentNavConsultasAdminBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), bundle.getString("exito"), Snackbar.LENGTH_LONG).show();
        }

        recyclerView = view.findViewById(R.id.idRecyclerConsultas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaConsultas = new ArrayList<>();
        adapter = new ListaConsultasAdapterAdmin(listaConsultas, getContext());
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("consultas");
        valueEventListener = databaseReference.addValueEventListener(new nav_consultas_admin.listener());

        noconsultas = view.findViewById(R.id.noConsultas);

        String[] fechasarray = getResources().getStringArray(R.array.fechas);
        AutoCompleteTextView spinner = view.findViewById(R.id.idFiltroConsulta);
        ArrayAdapter<String> adapterarray = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, fechasarray);
        spinner.setAdapter(adapterarray);
        TextInputLayout spinnera = view.findViewById(R.id.spinner_filtro_consultas);
        spinner.setText(fechasarray[0], false);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                fecha = spinner.getText().toString();
                if (fecha.equals("Todas las consultas")) {
                    todaslasconsultas = true;
                    ultimas24h = false;
                    ultimasemana = false;
                    ultimomes = false;
                    valueEventListener = databaseReference.addValueEventListener(new nav_consultas_admin.listener());
                } else if (fecha.equals("Últimas 24h")) {
                    todaslasconsultas = false;
                    ultimas24h = true;
                    ultimasemana = false;
                    ultimomes = false;
                    valueEventListener = databaseReference.addValueEventListener(new nav_consultas_admin.listener());
                } else if (fecha.equals("Última semana")) {
                    todaslasconsultas = false;
                    ultimas24h = false;
                    ultimasemana = true;
                    ultimomes = false;
                    valueEventListener = databaseReference.addValueEventListener(new nav_consultas_admin.listener());
                } else if (fecha.equals("Último mes")) {
                    todaslasconsultas = false;
                    ultimas24h = false;
                    ultimasemana = false;
                    ultimomes = true;
                    valueEventListener = databaseReference.addValueEventListener(new nav_consultas_admin.listener());
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaConsultas.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Consulta consulta = ds.getValue(Consulta.class);

                    //FECHA CONSULTA
                    Calendar calConsulta = Calendar.getInstance();
                    try {
                        calConsulta.setTime(sf.parse(consulta.getFechahoraconsulta()));
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

                    if (!consulta.getIdrespuesta().equals("RESPUESTA DEL ADMINISTRADOR")) {
                        if (todaslasconsultas) {
                            listaConsultas.add(consulta);
                        } else {
                            if (calConsulta.getTime().after(calFiltro.getTime())) {
                                Log.d("filtro", "CUMPLE FILTRO");
                                listaConsultas.add(consulta);
                            }
                        }
                    }

                }

                if (listaConsultas.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noconsultas.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noconsultas.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            } else {
                listaConsultas.clear();
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
package com.example.viverocom.Fragments;

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

import com.example.viverocom.Adapters.ConsultaDetalleAdapter;
import com.example.viverocom.Adapters.ListaConsultasAdapter;
import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavDetalleconsultaBinding;
import com.example.viverocom.databinding.FragmentNavProductosBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class nav_detalleconsulta extends Fragment {

    ArrayList<Consulta> consultas;
    ArrayList<Consulta> consultasAMostrar;
    DatabaseReference databaseReference;
    ConsultaDetalleAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;
    View view;

    String id;
    String idRespuesta;

    private FragmentNavDetalleconsultaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //view = inflater.inflate(R.layout.fragment_nav_detalleconsulta, container, false);
        binding = FragmentNavDetalleconsultaBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        Bundle bundle = this.getArguments();
        id = bundle.getString("id");

        recyclerView = view.findViewById(R.id.idDetalleConsulta);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        consultas = new ArrayList<>();
        consultasAMostrar = new ArrayList<>();
        adapter = new ConsultaDetalleAdapter(consultasAMostrar, getContext());
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("consultas");
        valueEventListener = databaseReference.addValueEventListener(new nav_detalleconsulta.listener());

        return view;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                consultas.clear();
                consultasAMostrar.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Consulta consulta = ds.getValue(Consulta.class);
                    consultas.add(consulta);
                }
                for (Consulta c : consultas) {
                    if (c.getId().equals(id)) {
                        consultasAMostrar.add(c);
                        idRespuesta = c.getIdrespuesta();
                    }
                }
                if (idRespuesta != null && !idRespuesta.equals("")) {
                    for (Consulta c : consultas) {
                        if (c.getId().equals(idRespuesta)) {
                            consultasAMostrar.add(c);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            } else {
                consultas.clear();
                consultasAMostrar.clear();
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
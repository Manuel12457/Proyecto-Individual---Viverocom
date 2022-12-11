package com.example.viverocom.FragmentsAdmin;

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

import com.example.viverocom.Adapters.ConsultaDetalleAdapter;
import com.example.viverocom.Clases.Consulta;
import com.example.viverocom.Fragments.nav_detalleconsulta;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavDetalleconsultaAdminBinding;
import com.example.viverocom.databinding.FragmentNavDetalleconsultaBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class nav_detalleconsulta_admin extends Fragment implements MenuProvider {

    ArrayList<Consulta> consultas;
    ArrayList<Consulta> consultasAMostrar;
    DatabaseReference databaseReference;
    ConsultaDetalleAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;
    View view;

    boolean mostrarMenu = true;

    String id;
    String idRespuesta;

    private FragmentNavDetalleconsultaAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //view = inflater.inflate(R.layout.fragment_nav_detalleconsulta, container, false);
        binding = FragmentNavDetalleconsultaAdminBinding.inflate(inflater, container, false);
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
        valueEventListener = databaseReference.addValueEventListener(new nav_detalleconsulta_admin.listener());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_detalleconsulta,menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.responder:
                if (mostrarMenu) {
                    Log.d("menu", "RESPONDER");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", consultas.get(0).getId());
                    bundle.putString("asunto", "RESPUESTA - " + consultas.get(0).getAsunto());
                    Navigation.findNavController(view).navigate(R.id.nav_nuevaconsulta_admin, bundle);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Esta consulta ya fue respondida", Snackbar.LENGTH_LONG).show();
                }
                return true;
        }
        return false;
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
                            mostrarMenu = false;
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
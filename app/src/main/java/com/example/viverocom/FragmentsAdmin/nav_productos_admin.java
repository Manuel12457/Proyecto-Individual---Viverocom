package com.example.viverocom.FragmentsAdmin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.viverocom.Adapters.ListaProductosAdapter;
import com.example.viverocom.AdaptersAdmin.ListaProductosAdapterAdmin;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.Fragments.nav_productos;
import com.example.viverocom.R;
import com.example.viverocom.databinding.FragmentNavProductosAdminBinding;
import com.example.viverocom.databinding.FragmentNavProductosBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class nav_productos_admin extends Fragment {

    DatabaseReference databaseReference;
    ListaProductosAdapterAdmin adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;

    ArrayList<Producto> listaProductos;
    String busqueda;
    TextView noproductos;

    boolean filtroPlantaInterior = false;
    boolean filtroPlantaExterior = false;
    boolean filtroHerramientas = false;
    boolean filtroCuidadosPlanta = false;
    boolean filtroDecoracion = false;

    private FragmentNavProductosAdminBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_nav_productos,container,false);
        binding = FragmentNavProductosAdminBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), bundle.getString("exito"), Snackbar.LENGTH_LONG).show();
        }

        recyclerView = view.findViewById(R.id.idRecyclerProductos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        listaProductos = new ArrayList<>();
        adapter = new ListaProductosAdapterAdmin(listaProductos, getContext());
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("productos");
        valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());

        noproductos = view.findViewById(R.id.idnoresultados);
        TextInputLayout search = view.findViewById(R.id.inputbusqueda);
        search.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                busqueda = search.getEditText().getText().toString();
                valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());
            }
        });

        Chip chipPlantaInterior = view.findViewById(R.id.chipPlantasInterior);
        chipPlantaInterior.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    filtroPlantaInterior = true;
                } else {
                    filtroPlantaInterior = false;
                }
                valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());
            }
        });

        Chip chipPlantaExterior = view.findViewById(R.id.chipPlantasExterior);
        chipPlantaExterior.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    filtroPlantaExterior = true;
                } else {
                    filtroPlantaExterior = false;
                }
                valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());
            }
        });

        Chip chipHerramienta = view.findViewById(R.id.chipHerramientas);
        chipHerramienta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    filtroHerramientas = true;
                } else {
                    filtroHerramientas = false;
                }
                valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());
            }
        });

        Chip chipCuidadoPlanta = view.findViewById(R.id.chipCuidadoPlanta);
        chipCuidadoPlanta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    filtroCuidadosPlanta = true;
                } else {
                    filtroCuidadosPlanta = false;
                }
                valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());
            }
        });

        Chip chipDecoracion = view.findViewById(R.id.chipDecoracion);
        chipDecoracion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    filtroDecoracion = true;
                } else {
                    filtroDecoracion = false;
                }
                valueEventListener = databaseReference.addValueEventListener(new nav_productos_admin.listener());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = view.findViewById(R.id.btnNuevoProducto);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_productosedicion_admin);
            }
        });
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                listaProductos.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Producto producto = ds.getValue(Producto.class);

                    if (producto.isEstado()) {
                        if ((busqueda != null && !busqueda.equals(""))) {
                            if (producto.getNombre().toLowerCase().contains(busqueda.toLowerCase())) {

                                if (filtroPlantaInterior || filtroPlantaExterior || filtroCuidadosPlanta || filtroHerramientas || filtroDecoracion) {
                                    if (filtroPlantaInterior && producto.getTipo().equals("Planta de interior")) {
                                        listaProductos.add(producto);
                                    }
                                    if (filtroPlantaExterior && producto.getTipo().equals("Planta de exterior")) {
                                        listaProductos.add(producto);
                                    }
                                    if (filtroCuidadosPlanta && producto.getTipo().equals("Cuidado de la planta")) {
                                        listaProductos.add(producto);
                                    }
                                    if (filtroHerramientas && producto.getTipo().equals("Herramientas")) {
                                        listaProductos.add(producto);
                                    }
                                    if (filtroDecoracion && producto.getTipo().equals("Adorno para jardín")) {
                                        listaProductos.add(producto);
                                    }
                                } else {
                                    listaProductos.add(producto);
                                }

                            }
                        } else {

                            if (filtroPlantaInterior || filtroPlantaExterior || filtroCuidadosPlanta || filtroHerramientas || filtroDecoracion) {
                                if (filtroPlantaInterior && producto.getTipo().equals("Planta de interior")) {
                                    listaProductos.add(producto);
                                }
                                if (filtroPlantaExterior && producto.getTipo().equals("Planta de exterior")) {
                                    listaProductos.add(producto);
                                }
                                if (filtroCuidadosPlanta && producto.getTipo().equals("Cuidado de la planta")) {
                                    listaProductos.add(producto);
                                }
                                if (filtroHerramientas && producto.getTipo().equals("Herramientas")) {
                                    listaProductos.add(producto);
                                }
                                if (filtroDecoracion && producto.getTipo().equals("Adorno para jardín")) {
                                    listaProductos.add(producto);
                                }
                            } else {
                                listaProductos.add(producto);
                            }

                        }
                    }

                }

                if (listaProductos.isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    noproductos.setVisibility(View.VISIBLE);
                    if (busqueda == null || busqueda.equals("")) {
                        noproductos.setText("No se han encontrado productos");
                    } else {
                        noproductos.setText("No se han encontrado productos de nombre nombre \""+busqueda+"\"");
                    }
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noproductos.setVisibility(View.INVISIBLE);
                }
                adapter.notifyDataSetChanged();
            } else {
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
package com.example.viverocom.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviderGetKt;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.viverocom.Adapters.ImagenesEdicionProductoAdapter;
import com.example.viverocom.Adapters.ListaProductosAdapter;
import com.example.viverocom.Adapters.ProductoDetalleAdapter;
import com.example.viverocom.Clases.Coordenada;
import com.example.viverocom.Clases.DataCoordenada;
import com.example.viverocom.Clases.DataDepartamento;
import com.example.viverocom.Clases.DataProvincia;
import com.example.viverocom.Clases.Departamento;
import com.example.viverocom.Clases.Orden;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.Clases.Provincia;
import com.example.viverocom.R;
import com.example.viverocom.SharedViewModel;
import com.example.viverocom.databinding.FragmentNavProductosBinding;
import com.example.viverocom.databinding.FragmentNavProductosdetalleBinding;
import com.example.viverocom.databinding.FragmentNavProductosedicionBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class nav_productosdetalle extends Fragment implements MenuProvider {

    Query query;
    DatabaseReference databaseReferenceOrdenes;
    DatabaseReference databaseReferenceOrdenesDetalle;
    ValueEventListener valueEventListenerOrdenes;
    ValueEventListener valueEventListenerOrdenesDetalle;

    ProductoDetalleAdapter adapter;
    ValueEventListener valueEventListener;
    RecyclerView recyclerView;

    ArrayList<Producto> productoid;
    ArrayList<OrdenDetalle> listaOrdenesDetalle = new ArrayList<>();
    ArrayList<String> listaidOrdenes = new ArrayList<>();
    String id;
    View view;
    Button btnUbicacionUsuario;

    private ActivityResultLauncher<String[]> requestPermissionForLocation;

    private FragmentNavProductosdetalleBinding binding;
    String longitud = "";
    String latitud = "";
    FusedLocationProviderClient client;

    SharedViewModel viewModel;
    ArrayList<OrdenDetalle> carrito = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //view = inflater.inflate(R.layout.fragment_nav_productosdetalle, container, false);
        binding = FragmentNavProductosdetalleBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        //PERMISOS
//        requestPermissionForLocation = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
//            Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
//            Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
//            if (fineLocationGranted != null && fineLocationGranted) {
//                mostrarDatosUbicacion();
//            } else if (coarseLocationGranted != null && coarseLocationGranted) {
//
//            } else {
//
//            }
//        });
        //PERMISOS

        client = LocationServices.getFusedLocationProviderClient(getActivity());
        btnUbicacionUsuario = view.findViewById(R.id.btnUbicacionUsuario);
        btnUbicacionUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(
                        getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // When permission is granted
                    // Call method
                    getCurrentLocation();
                } else {
                    // When permission is not granted
                    // Call method
                    requestPermissions(new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }

            }
        });

        Bundle bundle = this.getArguments();
        id = bundle.getString("id");
        listenersParaValidacionDeEdicionyEliminacion();

        recyclerView = view.findViewById(R.id.idDetalleProducto);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productoid = new ArrayList<>();
        adapter = new ProductoDetalleAdapter(productoid, getContext(), getActivity(), "", "");
        recyclerView.setAdapter(adapter);

        query = FirebaseDatabase.getInstance().getReference("productos").orderByChild("id").equalTo(id);
        valueEventListener = query.addValueEventListener(new nav_productosdetalle.listener());

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            // When permission are granted
            // Call  method
            getCurrentLocation();
        } else {
            // When permission are denied
            // Display toast
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // Initialize Location manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // Check condition
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // When location service is enabled
            // Get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    // Initialize location
                    Location location = task.getResult();
                    // Check condition
                    if (location != null) {
                        adapter = new ProductoDetalleAdapter(productoid, getContext(), getActivity(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                        recyclerView.setAdapter(adapter);
                        query = FirebaseDatabase.getInstance().getReference("productos").orderByChild("id").equalTo(id);
                        valueEventListener = query.addValueEventListener(new nav_productosdetalle.listener());
                    } else {
                        // When location result is null
                        // initialize location request
                        LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                        // Initialize location call back
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                adapter = new ProductoDetalleAdapter(productoid, getContext(), getActivity(), String.valueOf(location1.getLatitude()), String.valueOf(location1.getLongitude()));
                                recyclerView.setAdapter(adapter);
                                query = FirebaseDatabase.getInstance().getReference("productos").orderByChild("id").equalTo(id);
                                valueEventListener = query.addValueEventListener(new nav_productosdetalle.listener());
                            }
                        };
                        // Request location updates
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            // When location service is not enabled
            // open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().addMenuProvider(this, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCarrito().observe(getViewLifecycleOwner(), new Observer<ArrayList<OrdenDetalle>>() {
            @Override
            public void onChanged(ArrayList<OrdenDetalle> ordenDetalles) {
                carrito = ordenDetalles;
            }
        });

    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.contextualactionbar_productos, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.guardarProductosEnCarrito:
                Log.d("menu", "AGREGAR PRODUCTO A CARRITO");
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                builder.setMessage("¿Desea agregar este elemento a su carrito?");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (carrito.isEmpty()) {
                                    OrdenDetalle ordenDetalle = new OrdenDetalle();
                                    ordenDetalle.setCantidad(1);
                                    ordenDetalle.setIdProducto(id);
                                    carrito.add(ordenDetalle);
                                    viewModel.setCarrito(carrito);
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), "El producto fue agregado a su carrito", Snackbar.LENGTH_LONG).show();
                                } else {
                                    if (!carrito.contains(id)) {
                                        OrdenDetalle ordenDetalle = new OrdenDetalle();
                                        ordenDetalle.setCantidad(1);
                                        ordenDetalle.setIdProducto(id);
                                        carrito.add(ordenDetalle);
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "El producto fue agregado a su carrito", Snackbar.LENGTH_LONG).show();

                                    } else {
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Este producto ya se encuentra en su carrito", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return true;
        }
        return false;
    }

    class listener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) { //Nodo referente existe
                productoid.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Producto producto = ds.getValue(Producto.class);
                    productoid.add(producto);
                    if (producto.getTipo().equals("Planta de interior") || producto.getTipo().equals("Planta de exterior")) {
                        btnUbicacionUsuario.setVisibility(View.VISIBLE);
                    }
                }

                adapter.notifyDataSetChanged();
            } else {
                productoid.clear();
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("msg", "Error onCancelled", error.toException());
        }
    }

    public void listenersParaValidacionDeEdicionyEliminacion() {
        databaseReferenceOrdenes = FirebaseDatabase.getInstance().getReference().child("ordenes");

        valueEventListenerOrdenes = databaseReferenceOrdenes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaidOrdenes.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Orden orden = ds.getValue(Orden.class);
                        if (orden.getEstado().equals("PENDIENTE")) {
                            listaidOrdenes.add(orden.getId());
                        }
                    }
                } else {
                    listaidOrdenes.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("msg", "Error onCancelled", error.toException());
            }
        });

        databaseReferenceOrdenesDetalle = FirebaseDatabase.getInstance().getReference().child("ordenesdetalle");

        valueEventListenerOrdenesDetalle = databaseReferenceOrdenesDetalle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listaOrdenesDetalle.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        OrdenDetalle ordenDetalle = ds.getValue(OrdenDetalle.class);
                        for (String s : listaidOrdenes) {
                            if (s.equals(ordenDetalle.getIdOrden()) && ordenDetalle.getIdProducto().equals(id)) {
                                listaOrdenesDetalle.add(ordenDetalle);
                            }
                        }
                        listaOrdenesDetalle.add(ordenDetalle);
                    }

                } else {
                    listaOrdenesDetalle.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("msg", "Error onCancelled", error.toException());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //mapView.onDestroy();
        query.removeEventListener(valueEventListener);
        databaseReferenceOrdenes.removeEventListener(valueEventListenerOrdenes);
        databaseReferenceOrdenesDetalle.removeEventListener(valueEventListenerOrdenesDetalle);
        binding = null;
    }

}
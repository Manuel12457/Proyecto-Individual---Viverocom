package com.example.viverocom;

import static com.example.viverocom.R.id.nav_host_fragment_content_navigation_drawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.Clases.Usuario;
import com.example.viverocom.Fragments.nav_productos;
import com.example.viverocom.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viverocom.databinding.ActivityNavigationDrawerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class NavigationDrawer extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationDrawerBinding binding;

    Usuario usuario;
    String rol;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationDrawerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        Menu nav_Menu = navigationView.getMenu();
        TextView navNombreUsuario = headerView.findViewById(R.id.nombreUsuario);
        TextView navApellidoUsuario = headerView.findViewById(R.id.apellidoUsuario);
        ImageView navFotoUsuario = headerView.findViewById(R.id.imageViewUsuario);
        //ACTUALIZAR NAVHEADER

        FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Usuario usuario = ds.getValue(Usuario.class);
                                rol = usuario.getRol();

                                navNombreUsuario.setText(usuario.getNombre());
                                navApellidoUsuario.setText(usuario.getApellido());
                                for (Map.Entry<String, String> entry : usuario.getImagen().entrySet()) {
                                    Glide.with(NavigationDrawer.this)
                                            .load(entry.getValue())
                                            .into(navFotoUsuario);
                                }

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_productos, R.id.nav_carrito, R.id.nav_historial, R.id.nav_consultas)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(NavigationDrawer.this, nav_host_fragment_content_navigation_drawer);
        NavigationUI.setupActionBarWithNavController(NavigationDrawer.this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        BottomNavigationView bottomnavView = findViewById(R.id.nav_view_bottom);
        NavigationUI.setupWithNavController(bottomnavView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (navDestination.getId() == R.id.nav_home) {
                    getSupportActionBar().setTitle("Menú");
                    bottomnavView.setVisibility(View.VISIBLE);
                } else if (navDestination.getId() == R.id.nav_productos) {
                    getSupportActionBar().setTitle("Buscar productos");
                    bottomnavView.setVisibility(View.VISIBLE);
                } else if (navDestination.getId() == R.id.nav_carrito) {
                    getSupportActionBar().setTitle("Carrito de compras");
                    bottomnavView.setVisibility(View.VISIBLE);
                } else if (navDestination.getId() == R.id.nav_historial) {
                    getSupportActionBar().setTitle("Historial de compras");
                    bottomnavView.setVisibility(View.VISIBLE);
                } else if (navDestination.getId() == R.id.nav_consultas) {
                    getSupportActionBar().setTitle("Consultas");
                    bottomnavView.setVisibility(View.GONE);
                } else if (navDestination.getId() == R.id.nav_nuevaconsulta) {
                    getSupportActionBar().setTitle("Redactar");
                    bottomnavView.setVisibility(View.GONE);
                } else if (navDestination.getId() == R.id.nav_productosdetalle || navDestination.getId() == R.id.nav_detalleconsulta) {
                    getSupportActionBar().setTitle("");
                    bottomnavView.setVisibility(View.GONE);
                }
            }
        });

        nav_Menu.findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NavigationDrawer.this);
                builder.setMessage("¿Seguro que desea cerrar sesión? Se eliminarán los productos de su carrito");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(NavigationDrawer.this, IniciarSesion.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                builder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                builder.show();
                return false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, nav_host_fragment_content_navigation_drawer);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
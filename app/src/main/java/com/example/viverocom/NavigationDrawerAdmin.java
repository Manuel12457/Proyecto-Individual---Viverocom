package com.example.viverocom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.Usuario;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.viverocom.databinding.ActivityNavigationDrawerAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class NavigationDrawerAdmin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityNavigationDrawerAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavigationDrawerAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarNavigationDrawerAdmin.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        Menu nav_Menu = navigationView.getMenu();
        TextView navNombreUsuario =  headerView.findViewById(R.id.nombreUsuario);
        TextView navApellidoUsuario =  headerView.findViewById(R.id.apellidoUsuario);
        ImageView navFotoUsuario =  headerView.findViewById(R.id.imageViewUsuario);

        FirebaseDatabase.getInstance().getReference("usuarios").orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                Usuario usuario = ds.getValue(Usuario.class);
                                navNombreUsuario.setText(usuario.getNombre());
                                navApellidoUsuario.setText(usuario.getApellido());
                                for (Map.Entry<String, String> entry : usuario.getImagen().entrySet()) {
                                    Glide.with(NavigationDrawerAdmin.this)
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
                R.id.nav_productos_admin, R.id.nav_consultas_admin, R.id.nav_historial_admin)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer_admin);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                 if (navDestination.getId() == R.id.nav_productos_admin) {
                    getSupportActionBar().setTitle("Buscar productos");
                } else if (navDestination.getId() == R.id.nav_historial_admin) {
                    getSupportActionBar().setTitle("Historial de compras");
                } else if (navDestination.getId() == R.id.nav_consultas_admin) {
                    getSupportActionBar().setTitle("Consultas");
                } else if (navDestination.getId() == R.id.nav_nuevaconsulta_admin) {
                    getSupportActionBar().setTitle("Redactar");
                } else if (navDestination.getId() == R.id.nav_productosdetalle_admin || navDestination.getId() == R.id.nav_detalleconsulta_admin ||  navDestination.getId() == R.id.nav_productosedicion_admin || navDestination.getId() == R.id.nav_historialdetalle_admin) {
                    getSupportActionBar().setTitle("");
                }
            }
        });

        nav_Menu.findItem(R.id.logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(NavigationDrawerAdmin.this);
                builder.setMessage("¿Seguro que desea cerrar sesión?");
                builder.setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(NavigationDrawerAdmin.this, IniciarSesion.class);
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
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_navigation_drawer_admin);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

}
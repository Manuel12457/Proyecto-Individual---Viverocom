package com.example.viverocom.Adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.MainActivity;
import com.example.viverocom.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

public class ListaProductosAdapter extends RecyclerView.Adapter<ListaProductosAdapter.ProductosViewHolder> {

    private ArrayList<Producto> listaProductos;
    private Context context;

    public ListaProductosAdapter(ArrayList<Producto> listaProductos, Context context) {
        this.setListaProductos(listaProductos);
        this.setContext(context);
    }

    public ArrayList<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(ArrayList<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_producto, parent, false);
        return new ListaProductosAdapter.ProductosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
        Producto producto = getListaProductos().get(position);
        holder.nombre.setText(producto.getNombre());
        holder.tipo.setText(producto.getTipo());
        holder.costo.setText("S/" + producto.getCosto());
        holder.stock.setText("Stock: " + producto.getStock());

        boolean primeraImagen = true;
        if (producto.getImagenes() != null) {
            for (Map.Entry<String, String> entry : producto.getImagenes().entrySet()) {
                if (primeraImagen) {
                    primeraImagen = false;
                    Uri primeraImagenUri = Uri.parse(entry.getValue());
                    Glide.with(getContext())
                            .load(primeraImagenUri)
                            .into(holder.imageView);
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("id", producto.getId());
                Navigation.findNavController(v).navigate(R.id.nav_productosdetalle, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListaProductos().size();
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView tipo;
        public TextView costo;
        public TextView stock;
        public ImageView imageView;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.nombreProducto);
            this.tipo = itemView.findViewById(R.id.tipoProducto);
            this.costo = itemView.findViewById(R.id.precio);
            this.stock = itemView.findViewById(R.id.stock);
            this.imageView = itemView.findViewById(R.id.idImagenProducto);
        }
    }
}

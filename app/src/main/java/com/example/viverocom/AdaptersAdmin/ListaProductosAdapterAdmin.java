package com.example.viverocom.AdaptersAdmin;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viverocom.Adapters.ListaProductosAdapter;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;

import java.util.ArrayList;
import java.util.Map;

public class ListaProductosAdapterAdmin extends RecyclerView.Adapter<ListaProductosAdapterAdmin.ProductosAdminViewHolder> {

    private ArrayList<Producto> listaProductos;
    private Context context;

    public ListaProductosAdapterAdmin(ArrayList<Producto> listaProductos, Context context) {
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
    public ListaProductosAdapterAdmin.ProductosAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_producto, parent, false);
        return new ListaProductosAdapterAdmin.ProductosAdminViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProductosAdapterAdmin.ProductosAdminViewHolder holder, int position) {
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
                Navigation.findNavController(v).navigate(R.id.nav_productosdetalle_admin, bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return getListaProductos().size();
    }

    public class ProductosAdminViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView tipo;
        public TextView costo;
        public TextView stock;
        public ImageView imageView;

        public ProductosAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.nombreProducto);
            this.tipo = itemView.findViewById(R.id.tipoProducto);
            this.costo = itemView.findViewById(R.id.precio);
            this.stock = itemView.findViewById(R.id.stock);
            this.imageView = itemView.findViewById(R.id.idImagenProducto);
        }
    }

}

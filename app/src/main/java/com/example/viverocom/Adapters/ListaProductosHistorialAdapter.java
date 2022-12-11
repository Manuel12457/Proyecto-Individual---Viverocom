package com.example.viverocom.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.Home;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;

import java.util.ArrayList;
import java.util.Map;

public class ListaProductosHistorialAdapter extends RecyclerView.Adapter<ListaProductosHistorialAdapter.ListaProductosHistorialViewHolder> {

    private ArrayList<OrdenDetalle> listaOrdenDetalle;
    private ArrayList<Producto> listaProductos;
    private Context context;

    public ListaProductosHistorialAdapter(ArrayList<OrdenDetalle> listaOrdenDetalle, ArrayList<Producto> listaProductos, Context context) {
        this.setListaOrdenDetalle(listaOrdenDetalle);
        this.setListaProductos(listaProductos);
        this.setContext(context);
    }

    public ArrayList<OrdenDetalle> getListaOrdenDetalle() {
        return listaOrdenDetalle;
    }

    public void setListaOrdenDetalle(ArrayList<OrdenDetalle> listaOrdenDetalle) {
        this.listaOrdenDetalle = listaOrdenDetalle;
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
    public ListaProductosHistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_productohistorial, parent, false);
        return new ListaProductosHistorialAdapter.ListaProductosHistorialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProductosHistorialViewHolder holder, int position) {
        OrdenDetalle ordenDetalle = getListaOrdenDetalle().get(position);
        Producto producto = getListaProductos().get(position);
        holder.nombre.setText(producto.getNombre());
        holder.tipo.setText(producto.getTipo());
        holder.monto.setText("Monto: S/" + String.valueOf(Math.round(Double.valueOf(Double.valueOf(producto.getCosto())*Double.valueOf(ordenDetalle.getCantidad()) * 100.0) / 100.0)));
        holder.unidades.setText("Unidades: " + ordenDetalle.getCantidad());
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
    }

    @Override
    public int getItemCount() {
        return getListaOrdenDetalle().size();
    }

    public class ListaProductosHistorialViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre;
        public TextView tipo;
        public TextView monto;
        public TextView unidades;
        public ImageView imageView;
        public ListaProductosHistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombre = itemView.findViewById(R.id.nombreProducto_historial);
            this.tipo = itemView.findViewById(R.id.tipoProducto_historial);
            this.monto = itemView.findViewById(R.id.monto_historial);
            this.unidades = itemView.findViewById(R.id.cantidad_historial);
            this.imageView = itemView.findViewById(R.id.idImagenProducto_historial);
        }
    }

}

package com.example.viverocom.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viverocom.Clases.Home;
import com.example.viverocom.Clases.Orden;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HistorialDetalleAdapter extends RecyclerView.Adapter<HistorialDetalleAdapter.ListaProductosHistorialViewHolder> {

    private ArrayList<Orden> listaOrdenes;
    private ArrayList<OrdenDetalle> listaOrdenDetalle;
    private ArrayList<Producto> listaProductos;
    private Context context;

    public HistorialDetalleAdapter(ArrayList<Orden> listaOrdenes, ArrayList<OrdenDetalle> listaOrdenDetalle, ArrayList<Producto> listaProductos, Context context) {
        this.setListaOrdenes(listaOrdenes);
        this.setListaOrdenDetalle(listaOrdenDetalle);
        this.setListaProductos(listaProductos);
        this.setContext(context);
    }

    public ArrayList<Orden> getListaOrdenes() {
        return listaOrdenes;
    }

    public void setListaOrdenes(ArrayList<Orden> listaOrdenes) {
        this.listaOrdenes = listaOrdenes;
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
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_detallehistorial, parent, false);
        return new HistorialDetalleAdapter.ListaProductosHistorialViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaProductosHistorialViewHolder holder, int position) {
        Orden orden = getListaOrdenes().get(position);

        holder.codigo.setText("Código: "+orden.getCodigo());
        holder.monto.setText("Monto total: S/"+orden.getMontoTotal());
        holder.estado.setText(" "+orden.getEstado());
        holder.fechaEmision.setText("Fecha de emisión: " + orden.getFechaEmision());
        holder.fechaRecojo.setText("Fecha de recojo: " + orden.getFechaRecojo());

        SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        //FECHA LIMITE
        Calendar calRecojo = Calendar.getInstance();
        try {
            calRecojo.setTime(sf.parse(orden.getFechaRecojo()));
            calRecojo.add(Calendar.HOUR_OF_DAY, -1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.fechaLimiteCancelacion.setText("Fecha límite de cancelación: " + calRecojo);
        //FECHA LIMITE

        ListaProductosHistorialAdapter listaProductosHistorialAdapter = new ListaProductosHistorialAdapter(getListaOrdenDetalle(),getListaProductos(),getContext());
        holder.childRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.childRecyclerView.setAdapter(listaProductosHistorialAdapter);
    }

    @Override
    public int getItemCount() {
        return getListaOrdenes().size();
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

    public class ListaProductosHistorialViewHolder extends RecyclerView.ViewHolder {
        public TextView codigo;
        public TextView monto;
        public TextView estado;
        public TextView fechaEmision;
        public TextView fechaRecojo;
        public TextView fechaLimiteCancelacion;
        private RecyclerView childRecyclerView;
        public ListaProductosHistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            this.codigo = itemView.findViewById(R.id.codigoCompra);
            this.monto = itemView.findViewById(R.id.textView26);
            this.estado = itemView.findViewById(R.id.estadoCompra);
            this.fechaEmision = itemView.findViewById(R.id.fechaEmision);
            this.fechaRecojo = itemView.findViewById(R.id.fechaRecojo);
            this.fechaLimiteCancelacion = itemView.findViewById(R.id.fechaLimite);
            this.childRecyclerView = itemView.findViewById(R.id.idRecyclerProductosCompra);
        }
    }
}

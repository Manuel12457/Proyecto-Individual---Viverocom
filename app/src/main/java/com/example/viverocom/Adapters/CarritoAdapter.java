package com.example.viverocom.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viverocom.Clases.OrdenDetalle;
import com.example.viverocom.Clases.Producto;
import com.example.viverocom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Map;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private ArrayList<OrdenDetalle> listaOrdenesDetalle;
    private Context context;
    private ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved countOfImagesWhenRemoved;
    private ImagenesEdicionProductoAdapter.ItemClickListener itemClickListener;

    public CarritoAdapter(ArrayList<OrdenDetalle> listaOrdenesDetalle, Context context, ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved countOfImagesWhenRemoved, ImagenesEdicionProductoAdapter.ItemClickListener itemClickListener) {
        this.setListaOrdenesDetalle(listaOrdenesDetalle);
        this.setContext(context);
        this.setCountOfImagesWhenRemoved(countOfImagesWhenRemoved);
        this.setItemClickListener(itemClickListener);
    }

    public ArrayList<OrdenDetalle> getListaOrdenesDetalle() {
        return listaOrdenesDetalle;
    }

    public void setListaOrdenesDetalle(ArrayList<OrdenDetalle> listaOrdenesDetalle) {
        this.listaOrdenesDetalle = listaOrdenesDetalle;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved getCountOfImagesWhenRemoved() {
        return countOfImagesWhenRemoved;
    }

    public void setCountOfImagesWhenRemoved(ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved countOfImagesWhenRemoved) {
        this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
    }

    public ImagenesEdicionProductoAdapter.ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ImagenesEdicionProductoAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        OrdenDetalle ordenDetalle = listaOrdenesDetalle.get(position);

        Log.d("prueba", "TAMANIO LISTA EN ADAPTER" + getListaOrdenesDetalle().size());

        FirebaseDatabase.getInstance().getReference("productos").orderByChild("id").equalTo(ordenDetalle.getIdProducto())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Producto producto = snapshot.getValue(Producto.class);
                            boolean primeraImagen = true;
                            if (producto.getImagenes() != null) {
                                for (Map.Entry<String, String> entry : producto.getImagenes().entrySet()) {
                                    if (primeraImagen) {
                                        primeraImagen = false;
                                        Uri primeraImagenUri = Uri.parse(entry.getValue());
                                        Glide.with(getContext())
                                                .load(primeraImagenUri)
                                                .into(holder.imagen);
                                    }
                                }
                            }

                            holder.nombre.setText(producto.getNombre());
                            holder.tipo.setText(producto.getTipo());
                            holder.monto.setText("Monto: S/" + ordenDetalle.getCantidad()*producto.getCosto());
                            holder.stock.setText(ordenDetalle.getCantidad());

                            holder.delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    listaOrdenesDetalle.remove(listaOrdenesDetalle.get(position));
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,getItemCount());
                                    getCountOfImagesWhenRemoved().clicked(listaOrdenesDetalle.size());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaOrdenesDetalle.remove(listaOrdenesDetalle.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                getCountOfImagesWhenRemoved().clicked(listaOrdenesDetalle.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getListaOrdenesDetalle().size();
    }

    public class CarritoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private RoundedImageView imagen;
        private TextView nombre;
        private TextView tipo;
        private TextView monto;
        private TextView stock;
        private ImageButton add;
        private ImageButton minus;
        private ImageButton delete;
        ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved countOfImagesWhenRemoved;
        ImagenesEdicionProductoAdapter.ItemClickListener itemClickListener;
        public CarritoViewHolder(@NonNull View itemView, ImagenesEdicionProductoAdapter.CountOfImagesWhenRemoved countOfImagesWhenRemoved, ImagenesEdicionProductoAdapter.ItemClickListener itemClickListener) {
            super(itemView);
            this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
            this.imagen = itemView.findViewById(R.id.idImagenProducto_carrito);
            this.nombre = itemView.findViewById(R.id.nombreProducto_carrito);
            this.tipo = itemView.findViewById(R.id.tipoProducto_carrito);
            this.monto = itemView.findViewById(R.id.monto_carrito);
            this.stock = itemView.findViewById(R.id.cantidad);
            this.add = itemView.findViewById(R.id.btn_aumentar);
            this.minus = itemView.findViewById(R.id.btn_disminuir);
            this.delete = itemView.findViewById(R.id.imageButtonEliminar);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.itemClick(getAdapterPosition());
            }
        }
    }

    public interface CountOfImagesWhenRemoved {
        void clicked(int getSize);
    }

    public interface ItemClickListener {
        void itemClick(int position);
    }
}

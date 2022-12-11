package com.example.viverocom.Adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.viverocom.Clases.Home;
import com.example.viverocom.R;

import java.util.ArrayList;

public class ImagenesEdicionProductoAdapter extends RecyclerView.Adapter<ImagenesEdicionProductoAdapter.ImagenesEdicionProductoViewHolder> {

    private ArrayList<Uri> listaImagenes;
    private Context context;
    private CountOfImagesWhenRemoved countOfImagesWhenRemoved;
    private ItemClickListener itemClickListener;

    public ImagenesEdicionProductoAdapter(ArrayList<Uri> listaImagenes, Context context, CountOfImagesWhenRemoved countOfImagesWhenRemoved, ItemClickListener itemClickListener) {
        this.setListaImagenes(listaImagenes);
        this.setContext(context);
        this.setCountOfImagesWhenRemoved(countOfImagesWhenRemoved);
        this.setItemClickListener(itemClickListener);
    }

    public ArrayList<Uri> getListaImagenes() {
        return listaImagenes;
    }

    public void setListaImagenes(ArrayList<Uri> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ImagenesEdicionProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_fotoproducto, parent, false);
        return new ImagenesEdicionProductoAdapter.ImagenesEdicionProductoViewHolder(itemView, getCountOfImagesWhenRemoved(), getItemClickListener());
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenesEdicionProductoViewHolder holder, int position) {
        //holder.imagen.setImageURI(listaImagenes.get(position));
        Log.d("click", String.valueOf(listaImagenes.get(position)));
        Glide.with(getContext())
                .load(listaImagenes.get(position))
                .into(holder.imagen);

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaImagenes.remove(listaImagenes.get(position));
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
                getCountOfImagesWhenRemoved().clicked(listaImagenes.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return getListaImagenes().size();
    }

    public CountOfImagesWhenRemoved getCountOfImagesWhenRemoved() {
        return countOfImagesWhenRemoved;
    }

    public void setCountOfImagesWhenRemoved(CountOfImagesWhenRemoved countOfImagesWhenRemoved) {
        this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
    }

    public ItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ImagenesEdicionProductoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imagen;
        private ImageButton btn;
        CountOfImagesWhenRemoved countOfImagesWhenRemoved;
        ItemClickListener itemClickListener;
        public ImagenesEdicionProductoViewHolder(@NonNull View itemView, CountOfImagesWhenRemoved countOfImagesWhenRemoved, ItemClickListener itemClickListener) {
            super(itemView);
            this.countOfImagesWhenRemoved = countOfImagesWhenRemoved;
            this.imagen = itemView.findViewById(R.id.imageView_FotoSeleccionada);
            this.btn = itemView.findViewById(R.id.imageButton_EliminarFotoSeleccionada);
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

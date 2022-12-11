package com.example.viverocom.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.viverocom.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private ArrayList<Uri> listaImagenes;
    private ViewPager2 viewPager2;

    public SliderAdapter(ArrayList<Uri> listaImagenes, ViewPager2 viewPager2) {
        this.setListaImagenes(listaImagenes);
        this.setViewPager2(viewPager2);
    }

    public ArrayList<Uri> getListaImagenes() {
        return listaImagenes;
    }

    public void setListaImagenes(ArrayList<Uri> listaImagenes) {
        this.listaImagenes = listaImagenes;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container, parent, false);
        return new SliderAdapter.SliderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(getListaImagenes().get(position));
    }

    @Override
    public int getItemCount() {
        return getListaImagenes().size();
    }

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    public void setViewPager2(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView imagen;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imagen = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(Uri uri) {
            Glide.with(getViewPager2().getContext())
                    .load(uri)
                    .into(this.imagen);
        }
    }
}

package com.unse.proyecto.ubicua.interfaz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class ObjetosAdapter extends RecyclerView.Adapter<ObjetosAdapter.ObjetoViewHolder> {

    ArrayList<Objeto3D> mList;
    Context mContext;

    public ObjetosAdapter(ArrayList<Objeto3D> list, Context context) {
        mList = list;
        mContext = context;
    }

    @androidx.annotation.NonNull
    @Override
    public ObjetoViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_objetos, parent, false);
        return new ObjetoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ObjetoViewHolder holder, int position) {
        Objeto3D objeto3D = mList.get(position);
        Glide.with(holder.img.getContext()).load(objeto3D.getUrlImg())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .error(R.drawable.rueda)
                        .placeholder(R.drawable.ic_3d_download)).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ObjetoViewHolder extends RecyclerView.ViewHolder {
        ImageView img;

        public ObjetoViewHolder(@androidx.annotation.NonNull android.view.View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgIcon);
        }
    }
}

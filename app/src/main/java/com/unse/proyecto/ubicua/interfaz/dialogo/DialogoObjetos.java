package com.unse.proyecto.ubicua.interfaz.dialogo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.activity.InfoObjectoActivity;
import com.unse.proyecto.ubicua.interfaz.adapter.ObjetosAdapter;
import com.unse.proyecto.ubicua.principal.database.Objeto3DRepo;
import com.unse.proyecto.ubicua.principal.database.ObjetoCapturaRepo;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.ObjetoCaptura;
import com.unse.proyecto.ubicua.principal.util.RecyclerListener.ItemClickSupport;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DialogoObjetos extends DialogFragment {

    View view;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ObjetosAdapter mObjetosAdapter;
    Objeto3DRepo mDRepo;
    ObjetoCapturaRepo mCapturaRepo;
    Context mContext;
    ArrayList<Objeto3D> mList;

    public DialogoObjetos(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogo_objeto, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Esto es lo nuevoooooooo, evita los bordes cuadrados
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        loadListener();

        return view;
    }

    private void loadViews() {
        mRecyclerView = view.findViewById(R.id.recycler);
    }

    private void loadData() {
        mDRepo = new Objeto3DRepo(mContext);
        mCapturaRepo = new ObjetoCapturaRepo(mContext);
        ArrayList<ObjetoCaptura> capturas = mCapturaRepo.getAll();
        ArrayList<Objeto3D> objeto3DS = mDRepo.getAll();
        mList = new ArrayList<>();
        for (ObjetoCaptura obj : capturas) {
            Objeto3D obj3D = search(objeto3DS, obj);
            if (obj3D != null) {
                obj3D.setObjetoCaptura(obj);
                mList.add(obj3D);
            }
        }
        //mList.addAll(mDRepo.getAll());
        mLayoutManager = new GridLayoutManager(mContext, 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mObjetosAdapter = new ObjetosAdapter(mList, mContext);
        mRecyclerView.setAdapter(mObjetosAdapter);
    }

    private Objeto3D search(ArrayList<Objeto3D> objeto3DS, ObjetoCaptura objetoCaptura) {
        for (Objeto3D obj : objeto3DS) {
            if (obj.getId() == objetoCaptura.getId()) {
                return obj;
            }
        }
        return null;
    }


    private void loadListener() {
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, InfoObjectoActivity.class);
                intent.putExtra(Utils.OBJ_INFO, mList.get(position));
                startActivity(intent);
            }
        });

    }


}

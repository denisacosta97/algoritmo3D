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
import android.widget.LinearLayout;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.activity.InfoObjectoActivity;
import com.unse.proyecto.ubicua.interfaz.adapter.ObjetosAdapter;
import com.unse.proyecto.ubicua.interfaz.controller.DialogoObjetosViewModel;
import com.unse.proyecto.ubicua.interfaz.controller.MainActivityViewModel;
import com.unse.proyecto.ubicua.network.model.response.FoundedObjectResponse;
import com.unse.proyecto.ubicua.principal.modelo.FoundedObject;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.modelo.ObjetoCaptura;
import com.unse.proyecto.ubicua.principal.util.RecyclerListener.ItemClickSupport;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DialogoObjetos extends DialogFragment {

    View view;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    LinearLayout linearLayout;
    ObjetosAdapter mObjetosAdapter;
    Context mContext;
    ArrayList<FoundedObject> mList;
    private DialogoObjetosViewModel viewModel;

    public DialogoObjetos(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogo_objeto, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        viewModel = new ViewModelProvider(this).get(DialogoObjetosViewModel.class);

        loadViews();

        loadObservers();

        loadData();

        loadListener();

        return view;
    }

    private void loadObservers() {
        viewModel.showLoading.observe(this, aBoolean -> {
            if (aBoolean){
                linearLayout.setVisibility(View.VISIBLE);
            }else linearLayout.setVisibility(View.GONE);
        });
        viewModel.object.observe(this, foundedObjectResponses -> {
            linearLayout.setVisibility(View.GONE);
            if (foundedObjectResponses != null){
                mList = new ArrayList<>();
                for (FoundedObjectResponse response : foundedObjectResponses){
                    FoundedObject object = FoundedObject.build(response);
                    mList.add(object);
                }
                mLayoutManager = new GridLayoutManager(mContext, 4);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mObjetosAdapter = new ObjetosAdapter(mList, mContext);
                mRecyclerView.setAdapter(mObjetosAdapter);
            }
        });
    }

    private void loadViews() {
        mRecyclerView = view.findViewById(R.id.recycler);
        linearLayout = view.findViewById(R.id.progress_horizontal);
    }

    private void loadData() {
        viewModel.getObjects();
    }

    private void loadListener() {
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(mContext, InfoObjectoActivity.class);
            intent.putExtra(Utils.OBJ_INFO, mList.get(position));
            startActivity(intent);
        });

    }


}

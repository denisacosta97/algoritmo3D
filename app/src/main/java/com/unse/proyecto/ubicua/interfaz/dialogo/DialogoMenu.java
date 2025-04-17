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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.activity.DatosInicioActivity;
import com.unse.proyecto.ubicua.interfaz.controller.DialogoMenuViewController;
import com.unse.proyecto.ubicua.interfaz.controller.RegisterViewModel;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.network.model.response.LoginResponse;
import com.unse.proyecto.ubicua.obj3d.LevelModule;
import com.unse.proyecto.ubicua.principal.database.ObjetoCapturaRepo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

public class DialogoMenu extends DialogFragment {

    View view;
    Context mContext;
    TextView txtNivel, txtXP, txtCantObj, txtCantPasos, txtCantKM, tvRegisteredDate, tvCarrer, tvFaculty, tvName;
    ProgressBar mProgressBar;
    LevelModule mModule;
    ObjetoCapturaRepo mCapturaRepo;
    Button btnLogOut;

    DialogoMenuViewController viewModel;
    public DialogoMenu(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialogo_menu, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        viewModel = new ViewModelProvider(this).get(DialogoMenuViewController.class);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadViews();

        loadData();

        loadListener();

        return view;
    }

    private void loadViews() {
        mProgressBar = view.findViewById(R.id.progres);
        txtNivel = view.findViewById(R.id.txtNivel);
        txtXP = view.findViewById(R.id.txtXP);
        txtCantKM = view.findViewById(R.id.txtCountKM);
        txtCantObj = view.findViewById(R.id.txtCountObj);
        txtCantPasos = view.findViewById(R.id.txtCountPasos);
        tvRegisteredDate = view.findViewById(R.id.tvRegisteredDate);
        tvCarrer = view.findViewById(R.id.tvCarrer);
        tvFaculty = view.findViewById(R.id.tvFaculty);
        tvName = view.findViewById(R.id.tvName);
        btnLogOut = view.findViewById(R.id.btnLogOut);
    }

    private void loadData() {
        LoginResponse userInfo = viewModel.getSesionInfo();
        mModule = new LevelModule(mContext);
        mCapturaRepo = new ObjetoCapturaRepo(mContext);
        txtNivel.setText(String.format("Nivel %s", userInfo.level));
        txtCantKM.setText(String.format("%.2f KM", 0.3f));
        txtCantPasos.setText(String.valueOf(120));
        txtCantObj.setText(String.valueOf(mCapturaRepo.getAll().size()));
        int xp = mModule.getXP();
        int level = mModule.getLevel();
        int xpLevel = mModule.getXPLevel(xp, level);
        txtXP.setText(String.format("%s / %s XP", xp, xpLevel));
        mProgressBar.setMax(xpLevel);
        mProgressBar.setProgress(xp);
        tvRegisteredDate.setText(userInfo.created);
        tvCarrer.setText(userInfo.career);
        tvFaculty.setText(userInfo.faculty);
        tvName.setText(userInfo.name+" "+userInfo.surname);
    }


    private void loadListener() {
        btnLogOut.setOnClickListener(v -> {
            DialogoGeneral.Builder builder = new DialogoGeneral.Builder(this.mContext)
                    .setTitulo("¡ADVERTENCIA!")
                    .setTipo(DialogoGeneral.TIPO_ACEPTAR_CANCELAR)
                    .setIcono(R.drawable.ic_advertencia)
                    .setDescripcion("¿Estas seguro que deseas cerrar sesión?")
                    .setListener(new YesNoDialogListener() {
                        @Override
                        public void yes() {
                            viewModel.logOut();
                            requireActivity().startActivity(new Intent(requireActivity(), DatosInicioActivity.class));
                            requireActivity().finish();
                        }

                        @Override
                        public void no() {
                        }
                    });
            DialogoGeneral dialogoGeneral = builder.build();
            dialogoGeneral.setCancelable(false);
            dialogoGeneral.show(this.getFragmentManager(), "dialogo");

        });
    }


}

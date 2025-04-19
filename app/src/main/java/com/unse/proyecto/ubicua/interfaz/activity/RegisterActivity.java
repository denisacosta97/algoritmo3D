package com.unse.proyecto.ubicua.interfaz.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.unse.proyecto.ubicua.BaseActivity;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.controller.RegisterViewModel;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.network.model.request.RegisterRequest;
import com.unse.proyecto.ubicua.principal.util.Utils;

public class RegisterActivity extends BaseActivity {

    private EditText edtDNI, edtNombre, edtApellido, edtPsw;
    private Button btnRegister;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        loadViews();
        loadListener();
        loadObservers();

    }

    private void loadObservers(){

        viewModel.showLoading.observe(this, aBoolean -> {
            if(aBoolean){
                showLoadingDialog();
            }else{
                hideLoadingDialog();
            }
        });

        viewModel.registerError.observe(this, aBoolean -> {
            if(aBoolean){
                DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                        .setTitulo("¡Error inesperado!")
                        .setDescripcion("Por favor, vuelve a intentar mas tade")
                        .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                        .setIcono(R.drawable.ic_advertencia)
                        .setListener(new YesNoDialogListener() {
                            @Override
                            public void yes() {
                            }

                            @Override
                            public void no() {

                            }
                        });
                DialogoGeneral dialogoGeneral = builder.build();
                dialogoGeneral.setCancelable(false);
                dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
            }
        });

        viewModel.registerRes.observe(this, aBoolean -> {
            if(aBoolean){
                DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                        .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                        .setTitulo("¡Registro completado!")
                        .setIcono(R.drawable.ic_check)
                        .setDescripcion("El DNI se registro correctamente")
                        .setListener(new YesNoDialogListener() {
                            @Override
                            public void yes() {
                                finish();
                            }

                            @Override
                            public void no() {
                                finish();
                            }
                        });
                DialogoGeneral dialogoGeneral = builder.build();
                dialogoGeneral.setCancelable(false);
                dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
            }else{
                DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                        .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                        .setTitulo("¡Usuario ya registrado!")
                        .setIcono(R.drawable.ic_error)
                        .setDescripcion("El DNI ingresado ya se encuentra registrado")
                        .setListener(new YesNoDialogListener() {
                            @Override
                            public void yes() {
                            }

                            @Override
                            public void no() {
                            }
                        });
                DialogoGeneral dialogoGeneral = builder.build();
                dialogoGeneral.setCancelable(false);
                dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
            }
        });

    }

    private void loadListener() {
        btnRegister.setOnClickListener( V -> {
            if(edtDNI.getText().toString().isEmpty() || edtPsw.getText().toString().isEmpty() ||
                    edtApellido.getText().toString().isEmpty() || edtNombre.getText().toString().isEmpty()){
                Utils.showToast(RegisterActivity.this, "Todos los campos deben estar completos");
            }else{
                viewModel.callRegister(new RegisterRequest(
                        edtDNI.getText().toString(),
                        edtPsw.getText().toString(),
                        edtNombre.getText().toString(),
                        edtApellido.getText().toString()
                ));
            }
        });
    }

    private void loadViews() {
        btnRegister = findViewById(R.id.btnRegister);
        edtDNI = findViewById(R.id.edtDNI);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtPsw = findViewById(R.id.edtPsw);
    }
}

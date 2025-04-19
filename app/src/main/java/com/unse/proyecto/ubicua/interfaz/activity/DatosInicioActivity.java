package com.unse.proyecto.ubicua.interfaz.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.unse.proyecto.ubicua.BaseActivity;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.controller.DatosInicioViewModel;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.principal.util.Utils;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

public class DatosInicioActivity extends BaseActivity implements SensorEventListener {

    EditText edtDNI, getEdtPsw;
    Button btnOn, btnRegister;
    PreferenciasManager mPreferenciasManager;

    private SensorManager sensorManager;
    private Sensor gyroscope;

    private DatosInicioViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferenciasManager = new PreferenciasManager(getApplicationContext());
        viewModel = new ViewModelProvider(this).get(DatosInicioViewModel.class);
        setObservers();

        if (mPreferenciasManager.userLogged()) {
            setContentView(R.layout.activity_datos_inicio);

            loadViews();

            loadData();

            loadListener();

            // Inicializar el SensorManager
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

            // Obtener el sensor de giroscopio
            if (sensorManager != null) {
                Utils.showLog("El acelerometro no está disponible en este dispositivo");
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (gyroscope == null) {
                    Utils.showLog("El giroscopio no está disponible en este dispositivo");
                }
            }

        } else {
            open();
        }
    }

    private void setObservers() {

        viewModel.getUserError.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                            .setTitulo("¡DATOS INCORRECTOS!")
                            .setDescripcion("Por favor, ingresa un usuario y contraseña válidos.")
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
            }
        });

        viewModel.showLoading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    showLoadingDialog();
                }else{
                    hideLoadingDialog();
                }
            }
        });

        viewModel.getUserRes.observe(this, user -> {
            if(user != null){
                DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                        .setTitulo("¡DATOS CORRECTOS!")
                        .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                        .setIcono(R.drawable.ic_check)
                        .setDescripcion("")
                        .setListener(new YesNoDialogListener() {
                            @Override
                            public void yes() {
                                open();
                                /*DialogoDescargarDatos dialogoDescargarDatos = new DialogoDescargarDatos(getApplicationContext(),
                                        new YesNoDialogListener() {
                                            @Override
                                            public void yes() {
                                                open();
                                            }

                                            @Override
                                            public void no() {

                                            }
                                        });
                                dialogoDescargarDatos.setCancelable(false);
                                dialogoDescargarDatos.show(getSupportFragmentManager(), "dialog_descarga");*/
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
        btnOn.setOnClickListener(v -> { checkDNI(); });
        btnRegister.setOnClickListener(v -> { openRegister(); });
    }

    private void openRegister() {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private void checkDNI() {
        String dni = edtDNI.getText().toString();
        String psw = getEdtPsw.getText().toString();
        if (!dni.isEmpty() && TextUtils.isDigitsOnly(dni) && dni.length() >= 6) {
            if(psw.isEmpty()){
                Utils.showToast(this, "La constraseña no debe estar vacia");
            }else{
                viewModel.getUser(dni, psw);
            }
        } else {
            Utils.showToast(this, "El dni ingresado es incorrecto");
        }
    }

    private void open() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void loadData() {
        mPreferenciasManager = new PreferenciasManager(getApplicationContext());
        isSupported();
    }

    private void isSupported() {
        if (Build.VERSION.SDK_INT < 24) {
            DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                    .setTitulo("¡ADVERTENCIA!")
                    .setTipo(DialogoGeneral.TIPO_ACEPTAR)
                    .setIcono(R.drawable.ic_advertencia)
                    .setDescripcion("La versión de tu Android no soporta la funcionalidad de Realidad Aumentada.\nTe recomendamos actualizarlo o cambiar de dispositivo.")
                    .setListener(new YesNoDialogListener() {
                        @Override
                        public void yes() {
                            finishAffinity();
                        }

                        @Override
                        public void no() {

                        }
                    });
            DialogoGeneral dialogoGeneral = builder.build();
            dialogoGeneral.setCancelable(false);
            dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
        }
    }

    private void loadViews() {
        edtDNI = findViewById(R.id.edtDNI);
        getEdtPsw = findViewById(R.id.edtPsw);
        btnOn = findViewById(R.id.btnOn);
        btnRegister = findViewById(R.id.btnRegister);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desregistrar el listener para ahorrar batería
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Capturar los valores del giroscopio
            float x = event.values[0]; // Eje X
            float y = event.values[1]; // Eje Y
            float z = event.values[2]; // Eje Z

            // Imprimir los valores en la consola
            Utils.showLog( "Acelerometro - X: " + x + ", Y: " + y + ", Z: " + z);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}

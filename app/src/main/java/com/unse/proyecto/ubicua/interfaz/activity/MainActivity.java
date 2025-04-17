package com.unse.proyecto.ubicua.interfaz.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unse.proyecto.ubicua.BaseActivity;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.interfaz.controller.MainActivityViewModel;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoBienvenida;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoMenu;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoObjetos;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;
import com.unse.proyecto.ubicua.obj3d.O3DModule;
import com.unse.proyecto.ubicua.obj3d.arcore.ArCoreRenderActivity;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.util.Utils;
import com.unse.proyecto.ubicua.sensores.GPSModule;
import com.unse.proyecto.ubicua.sensores.MapaUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    CardView cardObjetos, cardPerfil, cardOA;
    GoogleMap mGoogleMap;
    SupportMapFragment mMapFragment;
    Marker markerPosition;
    Circle markerCicle;
    GPSModule mGPSModule;
    O3DModule mO3DModule;
    Bitmap markerUbi, markerObject, markerPregunta;
    Marker selected;
    Objeto3D selectedObj;
    boolean isShow = false, isDialog;
    public static List<Integer> IDS = new ArrayList<>();
    private MainActivityViewModel viewModel;

    private final ArrayList<MapObjectResponse> objectList = new ArrayList<>();
    private final List<Marker> objectMarkers = new ArrayList<>();

    static {
        IDS.add(R.raw.secuencia);
        IDS.add(R.raw.condicional);
        IDS.add(R.raw.repeticion);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGPSModule.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mGPSModule.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        loadViews();
        loadData();
        loadObservers();
        loadListener();
        viewModel.getObjectLocations();
    }

    private void loadObservers() {
        viewModel.showLoading.observe(this, aBoolean -> {
            if (aBoolean) showLoadingDialog();
            else hideLoadingDialog();
        });

        viewModel.mapObject.observe(this, mapObjects -> {
            hideLoadingDialog();
            if (mapObjects != null && !mapObjects.isEmpty()) {
                objectList.clear();
                objectList.addAll(mapObjects);
                if (mGoogleMap != null) {
                    agregarModelosAlMapa(objectList);
                }
            }
        });
    }

    private void loadData() {
        mGPSModule = new GPSModule(this);
        DialogoBienvenida dialogoBienvenida = new DialogoBienvenida();
        dialogoBienvenida.show(getSupportFragmentManager(), "dialog_b");
        mMapFragment.getMapAsync(this::onMapReady);
    }

    private void loadListener() {
        cardOA.setOnClickListener(v -> {

        });
        cardOA.setVisibility(View.GONE);
        cardObjetos.setOnClickListener(v -> {
            DialogoObjetos dialogoObjetos = new DialogoObjetos(getApplicationContext());
            dialogoObjetos.show(getSupportFragmentManager(), "dialog_o");
        });
        cardPerfil.setOnClickListener(v -> {
            DialogoMenu dialogoMenu = new DialogoMenu(getApplicationContext());
            dialogoMenu.show(getSupportFragmentManager(), "dialog_v");
        });
    }

    private void loadViews() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cardObjetos = findViewById(R.id.cardObjetos);
        cardPerfil = findViewById(R.id.cardPerfil);
        cardOA = findViewById(R.id.cardOA);
    }

    private void agregarModelosAlMapa(ArrayList<MapObjectResponse> modelos) {
        if (mGoogleMap == null || modelos == null) return;
        for (MapObjectResponse modelo : modelos) {
            if (modelo.lat == 0 || modelo.lon == 0) continue;
            LatLng posicion = new LatLng(modelo.lat, modelo.lon);
            Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(posicion)
                    .title(modelo.nombre)
                    .icon(BitmapDescriptorFactory.fromBitmap(markerObject)));
            objectMarkers.add(marker);
            setMarkerClickListener(marker);
        }
    }

    private void setMarkerClickListener(Marker marker) {
        if (marker == null || marker.getTitle() == null || marker.getTitle().equals("Tú")) return;

        mGoogleMap.setOnMarkerClickListener(clickedMarker -> {
            if (!clickedMarker.getTitle().equals("Tú")) {
                DialogoGeneral.Builder builder = new DialogoGeneral.Builder(getApplicationContext())
                        .setTitulo("¡OBJETO ENCONTRADO!")
                        .setTipo(DialogoGeneral.TIPO_ACEPTAR_CANCELAR)
                        .setIcono(R.drawable.ic_advertencia)
                        .setDescripcion(String.format("ENCONTRASTE UN/A %s\n¡ACEPTÁ PARA UNA EXPERIENCIA EN 3D EN TU ENTORNO!", clickedMarker.getTitle().toUpperCase()))
                        .setListener(new YesNoDialogListener() {
                            @Override
                            public void yes() {
                                open(clickedMarker, getModelName(clickedMarker));
                                isDialog = false;
                            }

                            @Override
                            public void no() {
                                isDialog = false;
                            }
                        });
                DialogoGeneral dialogoGeneral = builder.build();
                dialogoGeneral.setCancelable(false);
                dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
                isDialog = true;
            }
            return true;
        });
    }

    private String getModelName(Marker marker) {
        String modelName = "";
        for(MapObjectResponse object : objectList){
            if(object.nombre.equalsIgnoreCase(marker.getTitle())){
                modelName = object.modelo;
                break;
            }
        }
        return modelName;
    }

    private void open(Marker marker, String modelo) {
        String nombre = modelo;
        if (marker != null) {
            selected = marker;
            isShow = true;
            //mO3DModule.discover(selectedObj);
            Intent intent = new Intent(getApplicationContext(), ArCoreRenderActivity.class);
            intent.putExtra(Utils.FOUND_MODE, true);
            intent.putExtra(Utils.OBJ_INFO, nombre);
            startActivityForResult(intent, Utils.OPEN_DISCOVER);
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.objDesconocido));
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        markerObject = MapaUtils.getDrawable(getApplicationContext(), R.drawable.ic_marker_3d, 150, 150);
        markerPregunta = MapaUtils.getDrawable(getApplicationContext(), R.drawable.ic_marker_question, 150, 150);
        markerUbi = MapaUtils.getDrawable(getApplicationContext(), R.drawable.ic_marker_home, 150, 150);

        LatLng latLng = MapaUtils.init(this, mGoogleMap);
        if (latLng != null) {
            Location location = new Location("");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            updateLocation(location);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));

            if (!objectList.isEmpty()) {
                agregarModelosAlMapa(objectList);
            }
        } else {
            mGPSModule.openGPSDialog();
        }
    }

    public void updateLocation(Location location) {
        if (mGoogleMap == null) return;
        mGoogleMap.clear();
        objectMarkers.clear();

        markerPosition = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Tú")
                .icon(BitmapDescriptorFactory.fromBitmap(markerUbi)));

        markerCicle = mGoogleMap.addCircle(new CircleOptions()
                .center(markerPosition.getPosition())
                .radius(30)
                .strokeWidth(1)
                .fillColor(getResources().getColor(R.color.colorRedCircle)));

        agregarModelosAlMapa(objectList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mGPSModule.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.OPEN_DISCOVER) {
            determineCondition(resultCode, data);
        } else mGPSModule.onActivityResult(requestCode, resultCode, data);
    }

    private void determineCondition(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (selected != null) selected.remove();
            if (mO3DModule != null) mO3DModule.discover(selectedObj);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (selected != null) selected.remove();
        }
        isShow = false;
    }

    private void oa(Integer level) {
        InputStream inputStream = getResources().openRawResource(IDS.get(level - 1));
        File file = new File(getExternalFilesDir(null), "archivo.zip");
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
            inputStream.close();
            Utils.unzip(file.getAbsolutePath(), getExternalFilesDir(null) + "/");
            Intent intent = new Intent(getApplicationContext(), OAActivity.class);
            intent.putExtra(Utils.INT_TYPE, level);
            startActivity(intent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

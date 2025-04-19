package com.unse.proyecto.ubicua.interfaz.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unse.compilador.lenguaje.model.Movement;
import com.unse.proyecto.ubicua.BaseActivity;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.compilador.CompilerModule;
import com.unse.proyecto.ubicua.interfaz.controller.MainActivityViewModel;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoAlgoritmo;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoBienvenida;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoGeneral;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoPerfil;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoObjetos;
import com.unse.proyecto.ubicua.interfaz.listener.OnClickListener;
import com.unse.proyecto.ubicua.interfaz.listener.YesNoDialogListener;
import com.unse.proyecto.ubicua.network.model.response.MapObjectResponse;
import com.unse.proyecto.ubicua.network.model.response.UserResponse;
import com.unse.proyecto.ubicua.obj3d.arcore.ArCoreRenderActivity;
import com.unse.proyecto.ubicua.principal.modelo.LearningObject;
import com.unse.proyecto.ubicua.principal.modelo.Objeto3D;
import com.unse.proyecto.ubicua.principal.util.PreferenciasManager;
import com.unse.proyecto.ubicua.principal.util.Utils;
import com.unse.proyecto.ubicua.sensores.GPSModule;
import com.unse.proyecto.ubicua.sensores.MapaUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    CardView cardObjetos, cardPerfil, cardOA;
    GoogleMap mGoogleMap;
    SupportMapFragment mMapFragment;
    PreferenciasManager preferenciasManager;
    Marker markerPosition;
    Circle markerCicle;
    GPSModule mGPSModule;
    Bitmap markerUbi, markerObject;
    MapObjectResponse selected;
    private MainActivityViewModel viewModel;

    private final ArrayList<MapObjectResponse> objectList = new ArrayList<>();
    private final List<Marker> objectMarkers = new ArrayList<>();

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
    public void onResume() {
        super.onResume();
        if (viewModel != null)
            viewModel.getUser();
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
                agregarModelosAlMapa(objectList);
            }
        });
        viewModel.mapLearningObject.observe(this, learningObjectResponse -> {
            if (learningObjectResponse != null){
                LearningObject learningObject = LearningObject.build(learningObjectResponse);
                Intent intent = new Intent(getApplicationContext(), OAActivity.class);
                intent.putExtra(Utils.OA_DATA, learningObject);
                startActivity(intent);
            }
        });
        viewModel.addObject.observe(this, objeto3D -> {
            if (objeto3D != null){
                DialogoAlgoritmo dialogoAlgoritmo = new DialogoAlgoritmo(getApplicationContext(), objeto3D, closeAlgoritmoListener);
                dialogoAlgoritmo.show(getSupportFragmentManager(), "dialogo_alg");
            }
        });
    }

    private void loadData() {
        mGPSModule = GPSModule.getInstance(this);
        preferenciasManager = new PreferenciasManager(getApplicationContext());
        showDialogoBienvenida();
        mMapFragment.getMapAsync(this::onMapReady);
    }

    private void showDialogoBienvenida() {
        if (preferenciasManager.isFirstTimeLaunch()) {
            DialogoBienvenida dialogoBienvenida = new DialogoBienvenida(preferenciasManager);
            dialogoBienvenida.show(getSupportFragmentManager(), "dialog_b");
        }
    }

    private void loadListener() {
        cardOA.setOnClickListener(v -> {
            showAlgoritmo();
        });
        cardOA.setVisibility(View.VISIBLE);
        cardObjetos.setOnClickListener(v -> {
            DialogoObjetos dialogoObjetos = new DialogoObjetos(getApplicationContext());
            dialogoObjetos.show(getSupportFragmentManager(), "dialog_o");
        });
        cardPerfil.setOnClickListener(v -> {
            DialogoPerfil dialogoPerfil = new DialogoPerfil(getApplicationContext());
            dialogoPerfil.show(getSupportFragmentManager(), "dialog_v");
        });
    }


    OnClickListener<Objeto3D> closeAlgoritmoListener = object -> {
        viewModel.getLearningObject();
    };

    private void showAlgoritmo() {
        CompilerModule compilerModule = new CompilerModule();
        UserResponse userResponse = preferenciasManager.getObject(Utils.USER_DATA, UserResponse.class);
        compilerModule.build(mGPSModule.getHistory(), userResponse.getLevel());
        List<Movement> movements = compilerModule.translate();
        Objeto3D objeto3D = Objeto3D.build(selected, movements, "");
        viewModel.addObject(movements, mGPSModule.getHistory(), objeto3D);
    }

    private void loadViews() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cardObjetos = findViewById(R.id.cardObjetos);
        cardPerfil = findViewById(R.id.cardPerfil);
        cardOA = findViewById(R.id.cardOA);
    }

    private void agregarModelosAlMapa(ArrayList<MapObjectResponse> modelos) {
        if (mGoogleMap == null || modelos == null || modelos.isEmpty()) return;
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
                                open(clickedMarker);
                            }

                            @Override
                            public void no() {

                            }
                        });
                DialogoGeneral dialogoGeneral = builder.build();
                dialogoGeneral.setCancelable(false);
                dialogoGeneral.show(getSupportFragmentManager(), "dialogo");
            }
            return true;
        });
    }

    private void open(Marker marker) {
        MapObjectResponse objectResponse = getObject(marker.getTitle());
        if (objectResponse != null) {
            selected = objectResponse;
            Intent intent = new Intent(getApplicationContext(), ArCoreRenderActivity.class);
            intent.putExtra(Utils.FOUND_MODE, true);
            intent.putExtra(Utils.OBJ_INFO, Objeto3D.build(objectResponse));
            startActivityForResult(intent, Utils.OPEN_DISCOVER);
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.objDesconocido));
        }
    }

    private MapObjectResponse getObject(String nombre) {
        for (MapObjectResponse objectResponse : objectList) {
            if (objectResponse.nombre.equalsIgnoreCase(nombre))
                return objectResponse;
        }
        return null;
    }

    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        markerObject = MapaUtils.getDrawable(getApplicationContext(), R.drawable.ic_marker_3d, 150, 150);
        markerUbi = MapaUtils.getDrawable(getApplicationContext(), R.drawable.ic_marker_home, 150, 150);

        LatLng latLng = MapaUtils.init(this, mGoogleMap);
        initLocation(latLng);
    }

    public void initLocation(LatLng latLng) {
        if (latLng != null) {
            Location location = new Location("");
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);
            updateLocation(location);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
        } else {
            mGPSModule.openGPSDialog();
        }
    }

    public void updateLocation(Location location) {
        if (mGoogleMap == null) return;
        if (markerPosition != null) markerPosition.remove();
        if (markerCicle != null) markerCicle.remove();
        if (objectMarkers.isEmpty()) agregarModelosAlMapa(objectList);

        markerPosition = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Tú")
                .icon(BitmapDescriptorFactory.fromBitmap(markerUbi)));

        markerCicle = mGoogleMap.addCircle(new CircleOptions()
                .center(markerPosition.getPosition())
                .radius(30)
                .strokeWidth(1)
                .fillColor(getResources().getColor(R.color.colorRedCircle)));
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
            //Aqui logica de confirmar objeto
            saveFoundObject();
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //No deberia hacer nada
        }
    }

    private void saveFoundObject() {
        //Guardo objeto
        showAlgoritmo();
    }
}

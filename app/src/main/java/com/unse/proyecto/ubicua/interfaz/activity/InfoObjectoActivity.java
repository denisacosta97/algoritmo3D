package com.unse.proyecto.ubicua.interfaz.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unse.compilador.lenguaje.model.Movement;
import com.unse.proyecto.ubicua.R;
import com.unse.proyecto.ubicua.compilador.CompilerModule;
import com.unse.proyecto.ubicua.interfaz.dialogo.DialogoAlgoritmo;
import com.unse.proyecto.ubicua.obj3d.arcore.ArCoreRenderActivity;
import com.unse.proyecto.ubicua.principal.modelo.FoundedObject;
import com.unse.proyecto.ubicua.principal.util.Utils;
import com.unse.proyecto.ubicua.sensores.MapaUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class InfoObjectoActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtTitulo, txtFecha;
    SupportMapFragment mMapFragment;
    ImageView imgFlecha, imgIcon;
    CardView cardAlgoritmo, cardRA;
    FoundedObject foundedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_object);

        if (getIntent().getParcelableExtra(Utils.OBJ_INFO) != null) {
            foundedObject = getIntent().getParcelableExtra(Utils.OBJ_INFO);
        }

        if (foundedObject != null) {
            loadViews();

            loadData();

            loadListener();
        } else {
            Utils.showToast(getApplicationContext(), getString(R.string.objNoLoad));
            finish();
        }


    }

    private void loadListener() {
        cardRA.setOnClickListener(this);
        imgFlecha.setOnClickListener(this);
        cardAlgoritmo.setOnClickListener(this);
    }

    private void loadData() {
        if (foundedObject.getLocation() != null){
            mMapFragment.getMapAsync(googleMap -> {
                googleMap.setOnCameraMoveListener(null);
                Bitmap markerObject = MapaUtils.getDrawable(getApplicationContext(), R.drawable.ic_marker_3d, 150, 150);
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(foundedObject.getLocation())
                        .title(foundedObject.getObject3D().getNombre())
                        .icon(BitmapDescriptorFactory.fromBitmap(markerObject)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 19));
            });
        }
        txtTitulo.setText(foundedObject.getObject3D().getNombre());
        Glide.with(imgIcon.getContext()).load(foundedObject.getObject3D().getUrlImg())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .error(R.drawable.ic_error)
                        .placeholder(R.drawable.ic_3d_download)).into(imgIcon);
        txtFecha.setText(Utils.getFechaFormat(foundedObject.getCreated()));
    }

    private void loadViews() {
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        cardRA = findViewById(R.id.cardModoRA);
        imgIcon = findViewById(R.id.imgIcon);
        cardAlgoritmo = findViewById(R.id.cardAlgoritmo);
        txtTitulo = findViewById(R.id.txtTitulo);
        imgFlecha = findViewById(R.id.imgFlecha);
        txtFecha = findViewById(R.id.txtFecha);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgFlecha:
                onBackPressed();
                break;
            case R.id.cardAlgoritmo:
                List<Movement> movements = CompilerModule.stringToMovements(foundedObject.getAlgoritmo());
                foundedObject.getObject3D().setMovementList(movements);
                DialogoAlgoritmo dialogoAlgoritmo = new DialogoAlgoritmo(getApplicationContext(), foundedObject.getObject3D(), null);
                dialogoAlgoritmo.show(getSupportFragmentManager(), "dialogo_alg");
                break;
            case R.id.cardModoRA:
                Intent intent = new Intent(getApplicationContext(), ArCoreRenderActivity.class);
                intent.putExtra(Utils.OBJ_INFO, foundedObject.getObject3D());
                startActivity(intent);
                break;
        }
    }
}

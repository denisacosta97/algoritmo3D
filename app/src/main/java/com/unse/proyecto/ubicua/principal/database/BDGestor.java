package com.unse.proyecto.ubicua.principal.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BDGestor extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "Algoritmo.db";
    private static final int DATABASE_VERSION = 1;


    public BDGestor(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = Objeto3DRepo.createTable();
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.execSQL(PistaRepo.createTable());
        sqLiteDatabase.execSQL(ObjetoCapturaRepo.createTable());


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}

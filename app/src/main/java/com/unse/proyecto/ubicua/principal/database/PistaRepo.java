package com.unse.proyecto.ubicua.principal.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.unse.proyecto.ubicua.principal.modelo.Pista;
import com.unse.proyecto.ubicua.principal.util.Utils;

import java.util.ArrayList;

public class PistaRepo {

    static String createTable() {
        return String.format("create table %s(%s %s %s," +
                        "%s %s %s,%s %s %s,%s %s %s, primary key(%s))",
                Pista.TABLA,
                Pista.KEY_ID, Utils.INT_TYPE, Utils.NULL_TYPE,
                Pista.KEY_NOMBRE, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Pista.KEY_LAT, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Pista.KEY_LOG, Utils.STRING_TYPE, Utils.NULL_TYPE,
                Pista.KEY_ID);
    }

    private Pista mLinea;


    public PistaRepo(Context context) {
        Utils.initBD(context);
        mLinea = new Pista();
    }

    private ContentValues loadValues(Pista objeto, int tipo) {
        ContentValues values = new ContentValues();
        if (tipo == 1)
            values.put(Pista.KEY_ID, objeto.getId());
        values.put(Pista.KEY_NOMBRE, objeto.getDescripcion());
        values.put(Pista.KEY_LAT, objeto.getLat());
        values.put(Pista.KEY_LOG, objeto.getLog());

        return values;
    }

    private Pista loadDataFromCursor(Cursor cursor, int tipo) {
        mLinea = new Pista();
        mLinea.setId(cursor.getInt(0));
        mLinea.setDescripcion(cursor.getString(1));
        mLinea.setLat(Double.valueOf(cursor.getString(2)));
        mLinea.setLog(Double.valueOf(cursor.getString(3)));
        return mLinea;
    }

    public int insert(Pista objeto) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        int x = (int) db.insert(Pista.TABLA, null, loadValues(objeto, 1));
        DBManager.getInstance().closeDatabase();
        return (int) x;
    }

    public void update(Pista objeto) {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        String id = String.valueOf(objeto.getId());
        String selection = Pista.KEY_ID + " = " + id;
        db.update(Pista.TABLA, loadValues(objeto, 1), selection, null);
        DBManager.getInstance().closeDatabase();
    }

    public Pista get(int id) {
        mLinea = new Pista();
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + Pista.TABLA + " where " +
                Pista.KEY_ID + " = " + id, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mLinea = loadDataFromCursor(cursor, 1);
            cursor.close();
        }
        DBManager.getInstance().closeDatabase();
        return mLinea;
    }

    public Pista get(String id) {
        mLinea = new Pista();
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        Cursor cursor = db.rawQuery("select * from " + Pista.TABLA + " where " +
                Pista.KEY_NOMBRE + " = '" + id+"'", null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            mLinea = loadDataFromCursor(cursor, 1);
            cursor.close();
        }
        DBManager.getInstance().closeDatabase();
        return mLinea;
    }

    public ArrayList<Pista> getAll() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        ArrayList<Pista> list = new ArrayList<Pista>();
        String query = String.format("select * from %s", Pista.TABLA);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                mLinea = loadDataFromCursor(cursor, 1);
                list.add(mLinea);
            } while (cursor.moveToNext());
        }
        DBManager.getInstance().closeDatabase();
        cursor.close();

        return list;

    }

    public void deleteAll() {
        SQLiteDatabase db = DBManager.getInstance().openDatabase();
        db.delete(Pista.TABLA, null, null);
        DBManager.getInstance().closeDatabase();
    }
}

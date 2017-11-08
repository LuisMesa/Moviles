package com.example.android.guia3.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.guia3.entities.Plato;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LM on 7/11/2017.
 */
public class PlatosDatabase extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Plato.TABLE_NAME + " (" +
                    Plato.KEY_PLATO_ID + " INTEGER PRIMARY KEY," +
                    Plato.KEY_PLATO_NOMBRE + " TEXT," +
                    Plato.KEY_PLATO_PRECIO+ " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Plato.TABLE_NAME;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Platos.db";

    public PlatosDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addPlato(final Plato plato) {

        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).

            ContentValues values = new ContentValues();
            values.put(Plato.KEY_PLATO_NOMBRE, plato.getNombre());
            values.put(Plato.KEY_PLATO_PRECIO, plato.getPrecio());
            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            final long platoId =db.insertOrThrow(Plato.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
            plato.setId(platoId);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DB", "Error while trying to add post to database");
        } finally {

            db.endTransaction();
        }
    }
    public List<Plato> getAllPlatos() {
        List<Plato> platos = new ArrayList<>();
        String PLATO_QUERY =
                String.format("SELECT * FROM %s ",
                        Plato.TABLE_NAME);
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(PLATO_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {

                    long platoId = cursor.getLong(cursor.getColumnIndex(Plato.KEY_PLATO_ID));
                    String nombre = cursor.getString(cursor.getColumnIndex(Plato.KEY_PLATO_NOMBRE));
                    int precio = cursor.getInt(cursor.getColumnIndex(Plato.KEY_PLATO_PRECIO));

                    Plato newPlato = new Plato(platoId,nombre,precio,"");

                    platos.add(newPlato);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("DB", "Error while trying to get populations from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return platos;
    }
}

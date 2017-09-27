package com.example.android.guia3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.guia3.adapters.PlatoAdapter;
import com.example.android.guia3.entities.Plato;
import com.example.android.guia3.rest.RestClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<Plato> platos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Pedidos");

        listView=(ListView)findViewById(R.id.platos_listView);
        platos = new ArrayList();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent i = new Intent(MainActivity.this, PedidosActivity.class);
                i.putExtra("name",platos.get(position).getNombre());
                i.putExtra("id",platos.get(position).getId());
                startActivity(i);
            }
        });
        getPlatos();
    }

    public void getPlatos() {
        RestClient.get("platos", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                try {
                    for (int i = 0; i< array.length();i++) {
                        Gson gson = new GsonBuilder().create();
                        Plato plato = gson.fromJson(array.get(i).toString(), Plato.class);
                        platos.add(plato);
                    }
                    PlatoAdapter itemsAdapter = new PlatoAdapter(MainActivity.this, platos);
                    listView.setAdapter(itemsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable error){
                Toast.makeText(MainActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public AlertDialog createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hello World")
                .setTitle("Dialogo de Alerta")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do something
                    }
                });
        return builder.create();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alert:
                AlertDialog dialog = createAlertDialog();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}


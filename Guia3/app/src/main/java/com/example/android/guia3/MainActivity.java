package com.example.android.guia3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.guia3.adapters.PlatoAdapter;
import com.example.android.guia3.entities.Plato;
import com.example.android.guia3.rest.RestClient;
import com.example.android.guia3.services.GPSIntentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    public static final String GPS_FILTER = "GPSFilter";
    private ListView listView;
    private List<Plato> platos = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog loadingDialog;
    private GPSReceiver receiver;
    private TextView gpsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        listView=(ListView)findViewById(R.id.platos_listView);
        gpsText=(TextView)findViewById(R.id.gps_textview);
        platos = new ArrayList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,PedidosActivity.class);
                i.putExtra("name", ((Plato)platos.get(position)).getNombre());
                i.putExtra("id", ((Plato)platos.get(position)).getId());
                startActivity(i);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPlatos2();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        getPlatos2();
        receiver = new GPSReceiver(gpsText);
        this.registerReceiver(receiver, new IntentFilter(GPS_FILTER));
        Intent intent = new Intent(this, GPSIntentService.class);
        intent.setAction(GPSIntentService.GETPOSITION);
        this.startService(intent);
    }

    public void getPlatos2() {
        RestClient.get("platos", null, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                loadingDialog = new ProgressDialog(MainActivity.this);
                loadingDialog.setMessage("Cargando Platos");
                loadingDialog.setTitle("Espera...");
                loadingDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                platos = new ArrayList();
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
                mSwipeRefreshLayout.setRefreshing(false);
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable error){
                Toast.makeText(MainActivity.this, error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getPlatos() {
        AsyncTask getPlatosWithDialog = new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                loadingDialog = new ProgressDialog(MainActivity.this);
                loadingDialog.setMessage("Cargando Platos");
                loadingDialog.setTitle("Espera...");
                loadingDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                RestClient.get("platos", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray array) {
                        try {
                            for (int i = 0; i < array.length(); i++) {
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
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable error) {
                        Toast.makeText(MainActivity.this, error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                PlatoAdapter itemsAdapter = new PlatoAdapter(MainActivity.this, platos);
                if(listView!=null) {
                    listView.setAdapter(itemsAdapter);
                }if(loadingDialog!=null){
                    loadingDialog.dismiss();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }.execute();
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

    private class GPSReceiver extends BroadcastReceiver {
        private TextView textView;
        private Exception exception;
        public GPSReceiver(TextView view){ textView = view; }
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("position");
            textView.setText(text);
        }
    }
}


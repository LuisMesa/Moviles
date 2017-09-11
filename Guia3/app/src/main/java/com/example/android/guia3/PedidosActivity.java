package com.example.android.guia3;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.guia3.entities.Pedido;
import com.example.android.guia3.rest.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PedidosActivity extends AppCompatActivity {

    private String platoName;
    private int platoId;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().getExtras().getString("name") != null){
            platoName = getIntent().getExtras().getString("name");
            platoId = getIntent().getExtras().getInt("id");
        }
        getSupportActionBar().setTitle(platoName);

        Button btnComment = (Button) findViewById(R.id.pedir_btn);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarPedido();
            }
        });
    }
    public void realizarPedido() {
        String cliente = ((TextView) findViewById(R.id.nombre_cliente)).getText().toString();
        String lugar = ((TextView) findViewById(R.id.lugar_pedido)).getText().toString();
        Pedido pedido = new Pedido(cliente, lugar, platoId);

        RequestParams params = new RequestParams();
        params.put("pedido",pedido);
        RestClient.post("pedidos", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject object) {
                try {
                    LinearLayout mainLayout;

                    // Get your layout set up, this is just an example
                    mainLayout = (LinearLayout)findViewById(R.id.content_pedidos);

                    // Then just use the following:
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

                    Snackbar snackbar = Snackbar.make(coordinatorLayout, object.getString("msg"), Snackbar.LENGTH_LONG);
                    snackbar.show();

                } catch (Exception e) {
                    Log.d("PedidosActivity", e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable error) {
                try {
                    LinearLayout mainLayout;

                    // Get your layout set up, this is just an example
                    mainLayout = (LinearLayout)findViewById(R.id.content_pedidos);

                    // Then just use the following:
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);

                    Snackbar snackbar = Snackbar.make(coordinatorLayout, error.getMessage(), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    Log.d("PedidosActivity", error.getMessage());
                } catch (Exception e) {
                    Log.d("PedidosActivity", e.getMessage());
                }
            }
        });
    }
}


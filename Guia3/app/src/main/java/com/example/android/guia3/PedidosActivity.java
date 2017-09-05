package com.example.android.guia3;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class PedidosActivity extends AppCompatActivity {
    private String platoName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent() !=null && getIntent().getExtras().getString("name")!=null){
            platoName = getIntent().getExtras().getString("name");
        }
        getSupportActionBar().setTitle(platoName);
    }

}

package com.example.android.guia3.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.guia3.R;
import com.example.android.guia3.services.DeliveryManTask;

public class PropinaFragment extends Fragment implements LoaderManager.LoaderCallbacks {
    private int propina;
    private TextView propinaText;
    private static final String TAG = "Propina TAG";
    private TextView domiciliarioText;
    public PropinaFragment() {
        // Required empty public constructor
    }
    public static PropinaFragment newInstance() {
        PropinaFragment fragment = new PropinaFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "On create view fragment");
        return inflater.inflate(R.layout.fragment_propina, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "On view created fragment");
        propinaText =(TextView) view.findViewById(R.id.propina_text);
        domiciliarioText = (TextView)view.findViewById(R.id.domiciliario);
        //domiciliarioText.setText("Domiciliario de Prueba");
        Button menos = (Button) view.findViewById(R.id.menos);
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(propina>0){
                    propina-=1000;
                    propinaText.setText("$"+propina);
                }else{
                    Snackbar.make(propinaText,"Propina no puede ser negativa",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        Button mas = (Button)view.findViewById(R.id.mas);
        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propina+=1000;
                propinaText.setText("$"+propina);
            }
        });
        Loader loader =
                this.getLoaderManager().initLoader(0, null, this);
        loader.forceLoad();

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "On attach fragment");

    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "On detach fragment");
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //DeliveryManTask a = new DeliveryManTask(getActivity());
        //a.loadInBackground();
        //return a;
        return new DeliveryManTask(getActivity());
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        domiciliarioText.setText((String)data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
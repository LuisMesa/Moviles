package com.example.android.guia3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.guia3.entities.Pedido;
import com.example.android.guia3.rest.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class PedidosActivity extends AppCompatActivity implements SensorEventListener{

    private SensorManager mSensorManager;
    private Sensor mAcc;
    private String platoName;
    private int platoId;
    CoordinatorLayout coordinatorLayout;

    public final static int PAYMENT = 1111;
    private static final String TAG = "Pedidos";
    private static final int REQUEST_PHOTO=3;
    private static final int REQUEST_PERMISSION=4;
    private Uri imageToUploadUri1;
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private long mShakeTimestamp;
    private int mShakeCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
        Button btnMetodo = (Button) findViewById(R.id.payment_method);
        btnMetodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarMetodo();
            }
        });
        Button btnGPS = (Button) findViewById(R.id.gps_btn);
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encontrarUbicacion();
            }
        });
        Button btnImagen = (Button) findViewById(R.id.image_btn);
        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagen();
            }
        });

    }
    private void solicitarMetodo() {
        Intent i = new Intent(this, MetodoActivity.class);
        startActivityForResult(i,PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        Log.d(TAG,"Llega resultado de otra actividad");
        if (requestCode==PAYMENT){
            if(resultCode==111){
                Button btnPayment = (Button) findViewById(R.id.payment_method);
                btnPayment.setText(data.getStringExtra("method"));
            }
        }
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            if(data!=null) {
                imageToUploadUri1 = data.getData();
            }
            if(imageToUploadUri1 != null){
                Uri selectedImage = imageToUploadUri1;
                getContentResolver().notifyChange(selectedImage, null);

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION);

                    Log.d(TAG,"No hay permiso");
                }
                Bitmap reducedSizeBitmap = null;
                try {
                    reducedSizeBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageToUploadUri1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(reducedSizeBitmap != null) {
                    Log.d(TAG,"Llega imagen");
                    ImageView clientIv= (ImageView) findViewById(R.id.client_image);
                    clientIv.setImageBitmap(reducedSizeBitmap);
                }
            }else{
                Toast.makeText(this,"Error while capturing Image by Uri",Toast.LENGTH_LONG).show();
            }

        }
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
    private void encontrarUbicacion(){
        LocationManager mlocManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            return;
        }
        Location mLastLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Button btnGPS = (Button) findViewById(R.id.gps_btn);

        if(mLastLocation!=null){
            btnGPS.setText(mLastLocation.getLatitude() + " - "+mLastLocation.getLongitude());
        } else{
            new AlertDialog.Builder(this)
                    .setTitle(this.getResources().getString(R.string.result))
                    .setMessage(R.string.gps_not_found)
                    .setNegativeButton(getResources().getString(R.string.ok), null)
                    .create().show();
        }
    }
    private void selecionarImagen(){
        PackageManager packagemanager= getPackageManager();
        if(packagemanager.hasSystemFeature(PackageManager.FEATURE_CAMERA)==false){
            Toast.makeText(this,"Este dispositivo no tiene camara",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent pickIntent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent2.setType("image/*");
        Intent takePictureIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        File f=new File(Environment.getExternalStorageDirectory(), "monitoria/client.png");
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        String pickTitle = "Toma o selecciona una foto";
        Intent chooserIntent=Intent.createChooser(pickIntent2,pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[ ] {takePictureIntent});
        imageToUploadUri1 =Uri.fromFile(f);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(chooserIntent,REQUEST_PHOTO);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;
        // gForce will be close to 1 when there is no movement.
        double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);
        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }
            // reset the shake count after 3 seconds of no shakes
            if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                mShakeCount = 0;
            }
            mShakeTimestamp = now;
            mShakeCount++;
            realizarPedido();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}


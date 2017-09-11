package com.example.android.guia3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.guia3.MainActivity;
import com.example.android.guia3.R;
import com.example.android.guia3.entities.Plato;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlatoAdapter extends BaseAdapter {

    private List<Plato> platos;
    private Context context;
    private static LayoutInflater inflater=null;

    public PlatoAdapter(MainActivity mainActivity, List<Plato> platosList) {
        this.platos = platosList;
        this.context = mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return platos.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        Plato plato = platos.get(position);
        if (view == null)
            view = inflater.inflate(R.layout.platos_item, null);

        TextView nombreTextView = (TextView) view.findViewById(R.id.nombre_plato);
        TextView precioTextView = (TextView) view.findViewById(R.id.precio_plato);
        ImageView imageView = (ImageView) view.findViewById(R.id.imagen_plato);

        nombreTextView.setText(plato.getNombre());
        precioTextView.setText("$" + plato.getPrecio());
        System.out.print("Syso:"+plato.getImagen());
        Picasso.with(context)
                .load(plato.getImagen())
                .resize(50, 50)
                .centerCrop()
                .into(imageView);

        return view;
    }
}

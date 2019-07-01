package cl.cesar.er.evaluacion;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetalleActivity extends AppCompatActivity {

    private TextView txt_desc;
    private ImageView img_pel;
    private String URL = "http://image.tmdb.org/t/p/w500/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_desc = (TextView)findViewById(R.id.txt_desc);
        img_pel = (ImageView)findViewById(R.id.img_pel);

        txt_desc.setText(Global.overview);

        Picasso.get()
                .load(URL+Global.poster_path)
                .into(img_pel);


    }
}

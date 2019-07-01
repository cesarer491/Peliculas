package cl.cesar.er.evaluacion;


import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StyleableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Clases.Pelicula;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    public static String URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?api_key=34738023d27013e6d1b995443764da44";
    public static String URL_TOP = "https://api.themoviedb.org/3/movie/top_rated?api_key=34738023d27013e6d1b995443764da44";
    private TextView TvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TvError = (TextView)findViewById(R.id.TvError);

        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);

    }


    public void ClickMenu(View v) {

        switch (v.getId()) {

            case R.id.btnpopular:

                TvError.setVisibility(View.GONE);
                if (isConnected()) {
                    CargaDetallePelicula(MainActivity.URL_POPULAR);
                }

                break;

            case R.id.btn_2:
                TvError.setVisibility(View.GONE);
                if (isConnected()) {
                    CargaDetallePelicula(MainActivity.URL_TOP);
                }
                break;
        }

    }




    public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PeliculaViewHolder>
                                   implements View.OnClickListener{

        private View.OnClickListener listener;

        public void setOnClickListener(View.OnClickListener listener){

            this.listener = listener;

        }

        @Override
        public void onClick(View view) {

            if (listener != null){
                listener.onClick(view);
            }

        }

        public class PeliculaViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView NPelicula;


            PeliculaViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv);
                NPelicula = (TextView)itemView.findViewById(R.id.txt_titulo);

            }


        }


        ArrayList<Pelicula> peliculas;
        RVAdapter(ArrayList<Pelicula> peliculas){
            this.peliculas = peliculas;
        }

        @Override
        public int getItemCount() {
            return peliculas.size();
        }

        @Override
        public PeliculaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);

            v.setOnClickListener(this);

            PeliculaViewHolder pvh = new PeliculaViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(PeliculaViewHolder peliculaViewHolder, int i) {
            peliculaViewHolder.NPelicula.setText(peliculas.get(i).getTitle());

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }


    }

    private void CargaDetallePelicula(String URL){

        try{

            LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
            rv.setLayoutManager(llm);


            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                    URL,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            final ArrayList<Pelicula> arr_pelicula = new ArrayList<>();
                            try {
                                JSONArray Detalle = response.getJSONArray("results");
                                for (int i = 0; i < Detalle.length(); i++) {

                                    Pelicula pelicula = new Pelicula();

                                    JSONObject c = Detalle.getJSONObject(i);
                                    String title = c.getString("title");
                                    String poster_path = c.getString("poster_path");
                                    String vote_average = c.getString("vote_average");
                                    String popularity = c.getString("popularity");
                                    String original_language = c.getString("original_language");
                                    String original_title = c.getString("original_title");
                                    String backdrop_path = c.getString("backdrop_path");
                                    String overview = c.getString("overview");

                                    pelicula.setTitle(title);
                                    pelicula.setPoster_path(poster_path);
                                    pelicula.setVote_average(vote_average);
                                    pelicula.setPopularity(popularity);
                                    pelicula.setOriginal_language(original_language);
                                    pelicula.setOriginal_title(original_title);
                                    pelicula.setBackdrop_path(backdrop_path);
                                    pelicula.setOverview(overview);


                                    arr_pelicula.add(pelicula);


                                }

                                RVAdapter adapter = new RVAdapter(arr_pelicula);

                                adapter.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View view) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Global.poster_path = arr_pelicula.get(rv.getChildLayoutPosition(view)).getPoster_path();
                                                Global.overview = arr_pelicula.get(rv.getChildLayoutPosition(view)).getOverview();
                                                Intent in = new Intent(MainActivity.this,DetalleActivity.class);
                                                startActivity(in);
                                            }
                                        });
                                    }
                                });

                                rv.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            TvError.setVisibility(View.VISIBLE);
                            if (error instanceof NetworkError) {
                            } else if (error instanceof ServerError) {
                                TvError.setText("ServerError!");

                            } else if (error instanceof AuthFailureError) {
                                TvError.setText("AuthFailureError!");

                            } else if (error instanceof ParseError) {
                                TvError.setText("ParseError!");

                            } else if (error instanceof NoConnectionError) {
                                TvError.setText("NoConnectionError!");

                            } else if (error instanceof TimeoutError) {

                                TvError.setText("Timeout error!");

                            }

                        }
                    }

            )
            {

                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse res) {
                   // Code = res.statusCode;
                    return super.parseNetworkResponse(res);
                }

            };

            objectRequest.setRetryPolicy(new DefaultRetryPolicy(9000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(objectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }



}

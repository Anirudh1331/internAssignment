package com.example.intern;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
public static MyAppDatabase myAppDatabase;
ArrayList<Country> cnList;
RecyclerView recyclerView;
TextView txt;
int ct=0;
AlertDialog.Builder builder;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        builder = new AlertDialog.Builder(this);
        recyclerView=findViewById(R.id.recyclerView);
        txt=findViewById(R.id.txt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cnList=new ArrayList<>();
        myAppDatabase = Room.databaseBuilder(MainActivity.this, MyAppDatabase.class, "countrydb").allowMainThreadQueries().build();
        displayData();
    }
    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {


            return false;
        }
        return false;
    }


    private void displayData() {
        List<Country> ls=myAppDatabase.myDao().getCountry();
        for(Country country: ls){
            Country cn=new Country(country.getName(),country.getCapital(),country.getFlag(),country.getRegion(),country.getSubregion(),country.getPopulation(),country.getLanguages(),country.getBorders());
            cnList.add(cn);
        }
        CountryAdapter adapter=new CountryAdapter(this,cnList);
        recyclerView.setAdapter(adapter);
    }

    private void getData(){
        builder.setMessage("Please Wait!! Your data is being fetched").setTitle("Wait").setCancelable(true);
        AlertDialog alert=builder.create();
        alert.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL.URL_SPECIFIC, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name=jsonObject.getString("name");
                        String capital=jsonObject.getString("capital");
                        String flag=jsonObject.getString("flag");
                        String region=jsonObject.getString("region");
                        String subregion=jsonObject.getString("subregion");
                        String population=jsonObject.getString("population");
                        String languages = " ",oldborders="";
                        JSONArray js=jsonObject.getJSONArray("languages");
                        for(int j=0;j<js.length();j++){
                            JSONObject jo=js.getJSONObject(j);
                            languages=languages+jo.getString("name")+",";
                        }
                        oldborders=jsonObject.getString("borders");
                        String borders="";
                        for(int j=0;j<oldborders.length();j++){
                            if((oldborders.charAt(j)>='a' && oldborders.charAt(j)<='z') || (oldborders.charAt(j)>='A' && oldborders.charAt(j)<='Z') || oldborders.charAt(j)==','){
                                borders+=oldborders.charAt(j);
                            }
                        }
                        languages=languages.substring(0,languages.length()-1);
                        Country cn=new Country(name,capital,flag,region,subregion,population,languages,borders);
                        myAppDatabase.myDao().addCountry(cn);
                    }
                    Log.d("abcdefgh",response);
                    alert.cancel();
                    finish();
                    startActivity(getIntent());
                    ct=1;
//                    Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "error"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    alert.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "error  "+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                alert.cancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi=new MenuInflater(this);
        mi.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.deleteAll){
            cnList.clear();
            ct=0;
            finish();
            startActivity(getIntent());
            myAppDatabase.myDao().deleteAllCountry();
            Toast.makeText(this, "All items deleted", Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.fetchData) {

            if (isInternetOn() == false) {
                Toast.makeText(this, "Please Open Internet", Toast.LENGTH_SHORT).show();
            } else {
                getData();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
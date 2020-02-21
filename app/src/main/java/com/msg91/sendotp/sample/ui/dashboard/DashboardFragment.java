package com.msg91.sendotp.sample.ui.dashboard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.msg91.sendotp.sample.Hompage;
import com.msg91.sendotp.sample.R;
import com.msg91.sendotp.sample.Registration;
import com.msg91.sendotp.sample.Signin;
import com.msg91.sendotp.sample.ui.home.HomeFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {
ImageView image;
SharedPreferences sh,sho,shh;
EditText vechile,addres;
Button sent;
    Location location;

    String address, city, state, country, postalCode, knownName;
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        shh=getActivity().getSharedPreferences("rel",MODE_PRIVATE);
        sh=getActivity().getSharedPreferences("data",MODE_PRIVATE);
        sent=root.findViewById(R.id.sent);
        vechile=root.findViewById(R.id.vechile);
        addres=root.findViewById(R.id.waddress22);

image=root.findViewById(R.id.img2);
        Picasso.get().load(sh.getString("image",null)).into(image);

        sho = getActivity().getSharedPreferences("loc", MODE_PRIVATE);
        SharedPreferences.Editor ed = sho.edit();


        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new DashboardFragment.Listener());
        // Have another for GPS provider just in case.
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new DashboardFragment.Listener());
        // Try to request the location immediately
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (location != null) {
            handleLatLng(location.getLatitude(), location.getLongitude());
            ed.putString("la", String.valueOf(location.getLatitude()));
            ed.putString("lo", String.valueOf(location.getLongitude()));
            ed.apply();

        }




        sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(vechile.getText().toString().isEmpty()){

                  vechile.setError("Enter Vechile number");
              }
else if(addres.getText().toString().isEmpty()){


    addres.setError("null");

                }

else{

                  StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://androidprojectstechsays.000webhostapp.com/Women_Safty/sms1.php",
                          new Response.Listener<String>() {
                              @Override
                              public void onResponse(String response) {
//If we are getting success from server


                                  if(response.equals("ok"))
                                  {

                                      new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                              .setTitleText("Infermation share completed")
                                              .setContentText("Back To hom!")
                                              .setConfirmText("Yes")
                                              .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                  @Override
                                                  public void onClick(SweetAlertDialog sDialog) {
                                                      sDialog
                                                              .setTitleText("Logining...!")

                                                              .setConfirmText("OK")

                                                              .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                  @Override
                                                                  public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                      Intent in=new Intent(getActivity(), Hompage.class);
                                                                      startActivity(in);
                                                                      vechile.getText().clear();
                                                                      addres.getText().clear();
                                                                  }
                                                              })
                                                              .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                  }
                                              })
                                              .show();




//
                                  }


                              }
                          },

                          new Response.ErrorListener() {
                              @Override
                              public void onErrorResponse(VolleyError error) {
//You can handle error here if you want
                              }

                          }) {
                      @Override
                      protected Map<String, String> getParams() throws AuthFailureError {
                          Map<String, String> params = new HashMap<>();



                          params.put("add", addres.getText().toString());
                          params.put("vec", vechile.getText().toString());
                          params.put("ph",shh.getString("parentph",null));
                          params.put("pho",shh.getString("relativeph",null));
//returning parameter
                          return params;
                      }

                  };

// m = Integer.parseInt(ba) - Integer.parseInt(result.getContents());
// balance.setText(m+"");


//Adding the string request to the queue
                  RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                  requestQueue.add(stringRequest);





              }

            }
        });

        return root;
    }

    private void handleLatLng(final double latitude, final double longitude) {
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        city = addresses.get(0).getLocality();
        state = addresses.get(0).getAdminArea();
        country = addresses.get(0).getCountryName();
        postalCode = addresses.get(0).getPostalCode();
        knownName = addresses.get(0).getFeatureName();

addres.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {


        addres.setText(address);

    }
});






    }
    class Listener implements LocationListener {
        public void onLocationChanged(Location location) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }}
}
package com.example.mainproject.fragment;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mainproject.OpenHelper;
import com.example.android.multidex.mainproject.R;
import com.example.mainproject.domain.Organization;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListResourceBundle;

public class MapFragment extends Fragment {

    private static boolean isTouch = false;
    private static LatLng firstLatLng = new LatLng(0, 0);
    private AppCompatButton bt_fav, bt_list, bt_chat, bt_prof;
    private static final int request_Code = 1;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        bt_prof = view.findViewById(R.id.bt_map_prof);
        bt_fav = view.findViewById(R.id.bt_map_fav);
        bt_list = view.findViewById(R.id.bt_map_list);
        bt_chat = view.findViewById(R.id.bt_map_chat);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&ActivityCompat.checkSelfPermission
                        (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED
                ){

                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, request_Code );
                }
                LocationManager mLocationManager = (LocationManager)
                        getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
                List<String> providers = mLocationManager.getProviders(true);
                Location bestLocation = null;
                for (String provider : providers) {
                    Location l = mLocationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        // Found best last known location: %s", l);
                        bestLocation = l;
                    }
                }
                Location myLocation = bestLocation;
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(myLocation.getLatitude(), myLocation.getLongitude()),
                        15));

                if(isTouch) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            firstLatLng, 10));
                }
                try {
                    googleMap.setMyLocationEnabled(true);
                }catch (Exception e){
                    Log.e("CannotSetMyLocation", e.getMessage());
                }
                OpenHelper openHelper = new OpenHelper(getContext(), "OpenHelper", null,
                        OpenHelper.VERSION);
                ArrayList<Organization> arrayOrgList = openHelper.findAllOrganizations();
                try {
                    for (int i = 0; i < arrayOrgList.size(); i++) {
                        try {
                            LatLng latLng = getLocationFromAddress(arrayOrgList.get(i).getAddress(), googleMap);
                            googleMap.addMarker(new MarkerOptions().position(latLng).
                                    title(arrayOrgList.get(i).getAddress()));
                        }catch (Exception e){
                            Log.e("ADD_MARKER", e.getMessage());
                        }
                    }
                    googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker) {
                            Organization organization = openHelper.findOrgByAddress(marker.getTitle());
                            Bundle bundle = new Bundle();
                            bundle.putString("NameOrg", organization.getName());
                            bundle.putString("LOG", getArguments().getString("LOG"));
                            bundle.putString("PrevFragment", "map");
                            firstLatLng = marker.getPosition();
                            AppCompatButton appCompatButton = new AppCompatButton(getContext());
                            appCompatButton.setOnClickListener((view1) -> {
                                NavHostFragment.
                                        findNavController(MapFragment.this).navigate(
                                        R.id.action_mapFragment_to_fullInfoFragment, bundle);
                            });
                            appCompatButton.performClick();
                            isTouch = true;
                            return true;
                        }
                    });
                }catch (Exception e){
                    Log.e("MAP LATLNG", e.getMessage());
                }
            }
        });
        Bundle bundleLog = new Bundle();
        bundleLog.putString("LOG", getArguments().getString("LOG"));
        bt_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_chat.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(MapFragment.this).navigate(
                            R.id.action_mapFragment_to_listOfChatsFragment, bundleLog);
                });
                bt_chat.performClick();
            }
        });
        bt_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_fav.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(MapFragment.this).navigate(
                            R.id.action_mapFragment_to_favouritesFragment, bundleLog);
                });
                bt_fav.performClick();
            }
        });
        bt_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_list.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(MapFragment.this).navigate(
                            R.id.action_mapFragment_to_listFragment, bundleLog);
                });
                bt_list.performClick();
            }
        });
        bt_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_prof.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(MapFragment.this).navigate(
                            R.id.action_mapFragment_to_mainFragment, bundleLog);
                });
                bt_prof.performClick();
            }
        });
        return view;
    }


    public LatLng getLocationFromAddress(String strAddress, GoogleMap mMap) {
        Geocoder coder = new Geocoder(getContext());
        LatLng latLng = null;
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return latLng;
            }
            Address location = address.get(0);
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latLng;
    }

}
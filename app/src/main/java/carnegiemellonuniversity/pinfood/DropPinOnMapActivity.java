package carnegiemellonuniversity.pinfood;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;

public class DropPinOnMapActivity extends Fragment
        implements
        AdapterView.OnItemSelectedListener,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        OnMyLocationButtonClickListener,
        OnMapReadyCallback{

    private boolean focused;
    private static View view;
    private GoogleMap mMap;
    private CheckBox mTrafficCheckbox;
    private GoogleApiClient mGoogleApiClient;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getActivity().getSystemService(getActivity().
                        getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();

        if(network == null){
            AlertDialog alert = new AlertDialog.Builder(getActivity()).create();
            alert.setCancelable(false);

            alert.setMessage("This function needs your Internet access."
                    + " Change your settings?");

            alert.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                    getActivity().finish();
                    MainActivity.goBackFromDropPinFragment = true;
                }
            });

            alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Ignore",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent().
                            setClass(getActivity().getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            });

            alert.show();
        }

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.drop_pin_on_map_activity, container, false);
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }

        Spinner spinner = (Spinner) view.findViewById(R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CheckBox) view.findViewById(R.id.traffic);
        mTrafficCheckbox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateTraffic();
            }
        });

        /* GET CURRENT LOCATION */
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if(mMap == null) {
            mMap = map;
            updateTraffic();
            updateMyLocation();
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(this);
        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            //Toast.makeText(getActivity(), R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        mMap.setTrafficEnabled(mTrafficCheckbox.isChecked());
    }

    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // This is also called by the Android framework in onResume(). The map may not be created at
        // this stage yet.
        if (mMap != null) {
            setLayer((String) parent.getItemAtPosition(position));
        }
    }

    private void setLayer(String layerName) {
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapType(MAP_TYPE_HYBRID);
        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapType(MAP_TYPE_TERRAIN);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

    /*
     * get my location
     */
    private void cameraFocused(){
        if (mMap == null) {
            return;
        }

        LatLng ll = new LatLng(MainActivity.lati, MainActivity.longi);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
        mMap.animateCamera(update);
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    /**
     * Implementation of {@link LocationListener}.
     */
    @Override
    public void onLocationChanged(Location location) {
        if(mGoogleApiClient.isConnected()) {
            MainActivity.lati = location.getLatitude();
            MainActivity.longi = location.getLongitude();
            //Toast.makeText(getActivity().getApplicationContext(), "focus true"+MainActivity.longi, Toast.LENGTH_SHORT).show();
            if(!focused) {
                cameraFocused();
                focused = true;
            }
        }
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);  // LocationListener
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (mGoogleApiClient.isConnected()) {
            //Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            //String msg = MainActivity.lati + " " + MainActivity.longi;
            //Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

            LatLng ll = new LatLng(MainActivity.lati, MainActivity.longi);
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 16);
            mMap.animateCamera(update);
            return true;
        }
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}

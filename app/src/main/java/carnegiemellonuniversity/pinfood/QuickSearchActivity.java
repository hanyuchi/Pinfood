package carnegiemellonuniversity.pinfood;

import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class QuickSearchActivity extends Fragment
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener{

    private static View view;
    private GoogleApiClient mGoogleApiClient;
    private boolean afterSearchClick;

    // These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.quick_search_activity, container, false);

        /* GET CURRENT LOCATION */
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /* Listeners */
        Button searchButton = (Button) view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //search all restaurants in database
                afterSearchClick = true;
                onResume();//start locating and update location
            }
        });

        /*
         * NEED TO CHECK INTERNET ACCESS TO CHANGE UI AND FUNCTIONS
         */

        return view;
    }

    private void afterSearchClickAction(){
        Toast.makeText(getActivity().getApplicationContext(), "focus true"+MainActivity.longi, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), DisplaySearchResultActivity.class);
        intent.putExtra("latitude", MainActivity.lati);
        intent.putExtra("longitude", MainActivity.longi);
        startActivity(intent);
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

            // after search on click
            if(afterSearchClick){
                afterSearchClickAction();
                afterSearchClick = false;
            }
            onPause();
        }
    }

    /**
     * Callback called when connected to GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // LocationListener
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, REQUEST, this);
    }

    /**
     * Callback called when disconnected from GCore. Implementation of {@link GoogleApiClient.ConnectionCallbacks}.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // Do nothing
    }

    /**
     * Implementation of {@link GoogleApiClient.OnConnectionFailedListener}.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Do nothing
    }
}

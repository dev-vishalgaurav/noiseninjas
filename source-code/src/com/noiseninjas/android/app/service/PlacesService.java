package com.noiseninjas.android.app.service;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.noiseninjas.android.app.R;
import com.noiseninjas.android.app.engine.NoisePlace;
import com.noiseninjas.android.app.engine.PlaceEngine;
import com.noiseninjas.android.app.engine.PlaceIntesity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;

public class PlacesService extends IntentService {
    public static final String TAG = "PlaceService";

    public static final int RESULT_OK = 1;
    public static final int RESULT_CANCEL = 0;
    public static final int QUERY_GET_PLACES = 1;
    public static final int QUERY_NONE = 0;

    public static final String EXTRA_RESULT = "result";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_MAX_INTENSITY = "max_intensity";
    public static final String EXTRA_PLACES = "place_results";
    public static final String EXTRA_INTENSITY = "intensity";
    public static final String EXTRA_QUERY_TYPE = "query_type";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_RESULT_RECEIVER = "receiver";

    public PlacesService() {
        super(TAG);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onHandleIntent(Intent queryIntent) {
        Bundle result = null;
        switch (getQueryTypeFromIntent(queryIntent)) {
            case QUERY_GET_PLACES: {
                result = onQueryPlaces(queryIntent);
            }
                break;
            case QUERY_NONE:
            default:
                result = onQueryImproper(queryIntent, getBaseContext().getString(R.string.improper_query));
                break;
        }
        deliverResultToReceiver(queryIntent, result);

    }

    private void deliverResultToReceiver(Intent queryIntent, Bundle bundle) {
        ResultReceiver resultReceiver = getResultReceiverFromIntent(queryIntent);
        if (resultReceiver != null) {
            resultReceiver.send(bundle.getInt(EXTRA_RESULT), bundle);
        }
    }

    private ResultReceiver getResultReceiverFromIntent(Intent intent) {

        return intent.getParcelableExtra(EXTRA_RESULT_RECEIVER);
    }

    private Bundle onQueryPlaces(Intent queryIntent) {
        Bundle resultBundle = null;
        if (queryIntent.hasExtra(EXTRA_LOCATION)) {
            resultBundle = new Bundle();
            resultBundle.putInt(EXTRA_RESULT, RESULT_OK);
            LatLng location = queryIntent.getParcelableExtra(EXTRA_LOCATION);
            ArrayList<NoisePlace> places = (ArrayList<NoisePlace>) PlaceEngine.getPlacesAt(location);
            PlaceIntesity intensity = PlaceEngine.getMaxIntensityFromPlaces(places);
            resultBundle.putInt(EXTRA_INTENSITY, intensity.getLevel());
            resultBundle.putParcelableArrayList(EXTRA_PLACES, places);
        } else {
            resultBundle = onQueryImproper(queryIntent, getBaseContext().getString(R.string.no_location_input));
        }
        return resultBundle;
    }

    private Bundle onQueryImproper(Intent queryIntent, String errorMessage) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_RESULT, RESULT_CANCEL);
        bundle.putString(EXTRA_MESSAGE, getBaseContext().getString(R.string.improper_query));
        return bundle;

    }

    private int getQueryTypeFromIntent(Intent intent) {

        return intent.getIntExtra(EXTRA_QUERY_TYPE, QUERY_NONE);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}

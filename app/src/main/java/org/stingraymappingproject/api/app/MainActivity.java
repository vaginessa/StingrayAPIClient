package org.stingraymappingproject.api.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;
import org.stingraymappingproject.api.clientandroid.RecurringRequest;
import org.stingraymappingproject.api.clientandroid.models.Factoid;
import org.stingraymappingproject.api.clientandroid.models.StingrayReading;
import org.stingraymappingproject.api.clientandroid.requesters.FactoidsRequester;
import org.stingraymappingproject.api.clientandroid.requesters.NearbyRequester;
import org.stingraymappingproject.api.clientandroid.requesters.PostStingrayReadingRequester;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppBaseStringrayActivity {
    private final String TAG = "MainActivity";
    private static final int FREQUENCY_VALUE = 6;
    private static final TimeUnit FREQUENCY_UNIT = TimeUnit.SECONDS;

    @Override
    protected void scheduleRequesters() {
        scheduleNearbyRequester();
//        scheduleFactoidsRequester();
//        schedulePostStingrayReadingRequester();
    }


    public void scheduleFactoidsRequester() {
        FactoidsRequester factoidsRequester = new FactoidsRequester(mStingrayAPIService) {
            @Override
            public void onResponse(Factoid[] response) {
                List<Factoid> factoids = Arrays.asList(response);
//                Log.d(TAG, "onResponse");
                for(Factoid f : factoids) {
//                    Log.d(TAG, "Factoid: " + f.getFact());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "onErrorResponse");
            }
        };
        RecurringRequest recurringRequest = new RecurringRequest(FREQUENCY_VALUE, FREQUENCY_UNIT, factoidsRequester);
        mStingrayAPIService.addRecurringRequest(recurringRequest);
    }


    private void scheduleNearbyRequester() {
        NearbyRequester nearbyRequester = new NearbyRequester(mStingrayAPIService) {
            @Override
            protected String getRequestParams() {
                JSONObject nearbyFields = new JSONObject();
                JSONObject timeAndSpaceField = new JSONObject();
                try {
                    //:lat,:long,:since)
                    nearbyFields.put("lat", 29.94229);
                    nearbyFields.put("long", 29.94229);
                    nearbyFields.put("since", "Sun Jul 26 20:07:51 CDT 2015");
                    timeAndSpaceField.put("time_and_space", nearbyFields);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Log.d(TAG, "scheduleNearbyRequester: " + timeAndSpaceField.toString());
                   return timeAndSpaceField.toString();
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(StingrayReading[] response) {

//                Log.d(TAG, "scheduleNearbyRequester:onResponse");
                if(response.length > 0) {
                    if(mBoundToStingrayAPIService) {
                        mStingrayAPIService.setStingrayReadings(response);
                    }
                }
            }

        };
        RecurringRequest recurringRequest = new RecurringRequest(FREQUENCY_VALUE, FREQUENCY_UNIT, nearbyRequester);
        mStingrayAPIService.addRecurringRequest(recurringRequest);
    }

    private void schedulePostStingrayReadingRequester() {
//        Log.d(TAG, "schedulePostStingrayReadingRequester");
        PostStingrayReadingRequester postStingrayReadingRequester = new PostStingrayReadingRequester(mStingrayAPIService) {
            @Override
            protected String getRequestParams() {
                // Attributes
                int _threat_level = 20;
                double _lat = 17.214;
                double _long = 9.32;

                return getReqParamsAndAddNewReading(_threat_level, _lat, _long);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onResponse(StingrayReading response) {
//                Log.d(TAG, "schedulePostStingrayReadingRequester:onResponse");
                if(mBoundToStingrayAPIService) {
                    mStingrayAPIService.addStingrayReading(response);
                }
            }
        };
        RecurringRequest recurringRequest = new RecurringRequest(FREQUENCY_VALUE, FREQUENCY_UNIT, postStingrayReadingRequester);
        mStingrayAPIService.addRecurringRequest(recurringRequest);
    }

    private String getReqParamsAndAddNewReading(int _threat_level, double _lat, double _long) {
        JSONObject attributeFields = new JSONObject();
        JSONObject stingrayJSON = new JSONObject();

        Date _observed_at = new Date();
        String _version = getVersion();
        StingrayReading stingrayReading = new StingrayReading(_threat_level, _observed_at, _lat, _long, null, _version);


        if(mBoundToStingrayAPIService) {
            mStingrayAPIService.addStingrayReading(stingrayReading);
        }

        try {
            //:lat,:long,:since)
            attributeFields.put("threat_level", _threat_level);
            attributeFields.put("lat", _lat);
            attributeFields.put("long", _long);
            attributeFields.put("observed_at", _observed_at);
            attributeFields.put("unique_token", stingrayReading.getUniqueToken());
            attributeFields.put("version", _version);
            stingrayJSON.put("stingray_reading", attributeFields);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Log.d(TAG, "schedulePostStingrayReadingRequester: " + stingrayJSON);
        return stingrayJSON.toString();
    }

    public String getVersion() {
        PackageManager pm = getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return "App could not detect version number.";
        }
        return pInfo.versionName;
    }

}

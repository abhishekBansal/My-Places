package rrapps.myplaces.services;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import rrapps.myplaces.MyPlacesApplication;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.model.MPLocationDao;
import timber.log.Timber;

/**
 *
 * Created by abhishek on 10/12/15.
 */
public class FetchAddressIntentService extends IntentService {

    public static final String LOCATION_LATITUDE_DATA_EXTRA = "location_latitude_extra_key";
    public static final String LOCATION_LONGITUDE_DATA_EXTRA = "location_longitude_extra_key";
    public static final String LOCATION_PARCELABLE_ID_KEY = "location_parcelable_id_key";
    public static final String SUCCESS_ACTION_STRING = "rrapps.myplaces.address_fetch_success";

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage = "";
        String response;

        double latitude = intent.getDoubleExtra(LOCATION_LATITUDE_DATA_EXTRA, 0);
        double longitude = intent.getDoubleExtra(LOCATION_LONGITUDE_DATA_EXTRA, 0);
        long locationId = intent.getLongExtra(LOCATION_PARCELABLE_ID_KEY, -1);

        Timber.i("Starting reverse geo coding service %d, %f, %f", locationId, latitude, longitude);
        try {
            String query = "" + latitude + "," +longitude;
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + query;
            Timber.d("URL: %s", url);
            response = getAddressesByURL(url);

            if (response == null) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "Not Found";
                    Timber.e(errorMessage);
                }
                deliverResultToReceiver();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String address = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getString("formatted_address");
                    MPLocationDao dao = MyPlacesApplication.getInstance().getDaoSession().getMPLocationDao();
                    MPLocation location =
                            dao.queryBuilder().where(MPLocationDao.Properties.Id.eq(locationId)).unique();
                    location.setAddress(address);
                    dao.insertOrReplace(location);

                    deliverResultToReceiver();
                } catch (JSONException e) {
                    Timber.e(e, "Error fetching addresses for location id %l", locationId);
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Exception while trying to fetch address from google apis");
        }
    }

    public String getAddressesByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                br.close();
            } else {
                Timber.e("Response Code: %d", responseCode);
                response = "";
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private void deliverResultToReceiver() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SUCCESS_ACTION_STRING);
        sendBroadcast(broadcastIntent);
    }
}

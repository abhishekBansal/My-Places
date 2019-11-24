package rrapps.myplaces.services;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import rrapps.myplaces.R;
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
            String address = getAddressByLatLon(latitude, longitude);

            if ((address == null) || address.isEmpty()) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "Not Found";
                    Timber.e(errorMessage);
                }
                deliverResultToReceiver();
            } else {
                try {
                    MPLocationDao dao = MyPlacesApplication.getInstance().getDaoSession().getMPLocationDao();
                    MPLocation location =
                            dao.queryBuilder().where(MPLocationDao.Properties.Id.eq(locationId)).unique();
                    location.setAddress(address);
                    dao.insertOrReplace(location);

                    deliverResultToReceiver();
                } catch (Exception e) {
                    Timber.e(e, "Error fetching address for location id %l", locationId);
                }
            }
        } catch (Exception e) {
            Timber.e(e, "Error while trying to fetch address from 'developer.here.com' API");
        }
    }

    private String getAddressByLatLon(double latitude, double longitude) {
        String address = "";

        String requestURL = String.format(
            "https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox=%1$s,%2$s&mode=retrieveAddresses&maxresults=1&gen=9&app_id=%3$s&app_code=%4$s",
            latitude,
            longitude,
            FetchAddressIntentService.this.getString(R.string.HERE_API_APP_ID),
            FetchAddressIntentService.this.getString(R.string.HERE_API_APP_CODE)
        );
        Timber.d("URL: %s", requestURL);

        String response = getHttpResponseByUrl(requestURL);

        if ((response == null) || response.isEmpty()) {
            return address;
        }

        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(response);
            jsonObject = (JSONObject) jsonObject.get("Response");
            jsonObject = ((JSONArray) jsonObject.get("View")).getJSONObject(0);
            jsonObject = ((JSONArray) jsonObject.get("Result")).getJSONObject(0);
            jsonObject = (JSONObject) jsonObject.get("Location");
            jsonObject = (JSONObject) jsonObject.get("Address");

            address = jsonObject.getString("Label");
        } catch (JSONException e) {
            Timber.e(e, "Error fetching address from JSON server response %s", response);

            address = "";
        }
        return address;
    }

    private String getHttpResponseByUrl(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

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

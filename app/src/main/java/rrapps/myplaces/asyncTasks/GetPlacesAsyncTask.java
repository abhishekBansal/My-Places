package rrapps.myplaces.asyncTasks;


import android.os.AsyncTask;

import org.greenrobot.greendao.query.Query;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rrapps.myplaces.MyPlacesApplication;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.model.MPLocationDao;

/**
 * Created by abhishek on 08/08/15.
 *
 */
public class GetPlacesAsyncTask extends AsyncTask<Void, Void, List<MPLocation>> {

    private final LocationsFetchedListener mLocationsFetchedListener;

    public interface LocationsFetchedListener {
        void onLocationsFetched(List<MPLocation> MPLocations);
    }

    public GetPlacesAsyncTask(LocationsFetchedListener locationsFetchedListener) {
        mLocationsFetchedListener = locationsFetchedListener;
    }

    @Override
    protected List<MPLocation> doInBackground(Void... voids) {
        MPLocationDao dao = MyPlacesApplication.getInstance().getDaoSession().getMPLocationDao();
        Query<MPLocation> query = dao.queryBuilder().build();
        List<MPLocation> locationList = query.list();
        Collections.sort(locationList, new Comparator<MPLocation>() {
            @Override
            public int compare(MPLocation mpLocation, MPLocation t1) {
                if(mpLocation.isParkedCar()) {
                    return -1;
                } else if(t1.isParkedCar()) {
                    return 1;
                } else {
                    return mpLocation.getName().toUpperCase().compareTo(t1.getName().toUpperCase());
                }
            }
        });

        return locationList;
    }

    @Override
    protected void onPostExecute(List<MPLocation> dbLocations) {
        if(mLocationsFetchedListener != null && dbLocations.size() > 0) {
            mLocationsFetchedListener.onLocationsFetched(dbLocations);
        }
    }
}

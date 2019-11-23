package rrapps.myplaces.asyncTasks;

import android.os.AsyncTask;

import rrapps.myplaces.MyPlacesApplication;
import rrapps.myplaces.model.MPLocation;
import rrapps.myplaces.model.MPLocationDao;

/**
 * Created by
 * abhishek on 17/12/16.
 */

public class SaveLocationAsyncTask extends AsyncTask<MPLocation, Void, Void> {
    @Override
    protected Void doInBackground(MPLocation... mpLocations) {
        MPLocationDao dao = MyPlacesApplication.getInstance().getDaoSession().getMPLocationDao();
        for(MPLocation location : mpLocations) {
            dao.insertOrReplace(location);
        }
        return null;
    }
}

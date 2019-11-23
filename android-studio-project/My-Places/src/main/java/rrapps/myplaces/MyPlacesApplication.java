package rrapps.myplaces;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.os.Debug;

import rrapps.myplaces.model.DaoMaster;
import rrapps.myplaces.model.DaoSession;
import timber.log.Timber;


/**
 *
 * Created by abhishek on 27/06/15.
 */
public class MyPlacesApplication extends Application {

    private static MyPlacesApplication _instance;

    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;

        // initialize database
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "rrapps-myplaces-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mDaoSession = daoMaster.newSession();

        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static MyPlacesApplication getInstance() {
        return _instance;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
}

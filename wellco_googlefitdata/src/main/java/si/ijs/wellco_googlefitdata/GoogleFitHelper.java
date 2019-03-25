package si.ijs.wellco_googlefitdata;


import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.CharArrayReader;
import java.util.Calendar;

import si.ijs.wellco_googlefitdata.exceptions.GoogleApiClientNotConnectedException;
import si.ijs.wellco_googlefitdata.listeners.OnActiveMinutesResult;
import si.ijs.wellco_googlefitdata.listeners.OnCaloriesBurnedResult;
import si.ijs.wellco_googlefitdata.listeners.OnDistanceDataResult;
import si.ijs.wellco_googlefitdata.listeners.OnSleepDataResult;
import si.ijs.wellco_googlefitdata.listeners.OnStepCountResult;
import si.ijs.wellco_googlefitdata.tasks.ReadActiveMinutesTask;
import si.ijs.wellco_googlefitdata.tasks.ReadBurnedCaloriesTask;
import si.ijs.wellco_googlefitdata.tasks.ReadDistanceTask;
import si.ijs.wellco_googlefitdata.tasks.ReadSleepRequestTask;
import si.ijs.wellco_googlefitdata.tasks.ReadStepCountTask;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

public class GoogleFitHelper {

    //class variables
    private static final String TAG = GoogleFitHelper.class.getName();

    //object variables
    private static GoogleApiClient googleApiClient;

    //listeners
    private OnSleepDataResult sleepListener;
    private OnActiveMinutesResult activeMinuteListener;
    private OnCaloriesBurnedResult burnedCaloriesListener;
    private OnStepCountResult stepCountListener;
    private OnDistanceDataResult distanceListener;

    /**
     * @param googleApiClient
     * @param sleepListener
     * @param activeMinuteListener
     * @param burnedCaloriesListener
     */
    public GoogleFitHelper(GoogleApiClient googleApiClient, OnSleepDataResult sleepListener, OnActiveMinutesResult activeMinuteListener, OnCaloriesBurnedResult burnedCaloriesListener, OnStepCountResult stepCountListener, OnDistanceDataResult distanceListener) {
        this.googleApiClient = googleApiClient;
        this.sleepListener = sleepListener;
        this.activeMinuteListener = activeMinuteListener;
        this.burnedCaloriesListener = burnedCaloriesListener;
        this.stepCountListener = stepCountListener;
        this.distanceListener = distanceListener;
    }

    public void getSleepData() throws GoogleApiClientNotConnectedException {

        //check for valid google api
        if (googleApiClient == null)
            throw new GoogleApiClientNotConnectedException();


        //get start, end timestamp
        Calendar calendar = Calendar.getInstance();
        long endTimestamp = calendar.getTimeInMillis();

        //end
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long startTimestamp = calendar.getTimeInMillis();

        //execute async task in background thread
        Log.d(TAG, "getSleepData: async task executed");
        ReadSleepRequestTask task = new ReadSleepRequestTask();
        task.googleApiClient = googleApiClient;
        task.onDataResult = this.sleepListener;
        task.execute(startTimestamp, endTimestamp);
    }

    public void getActiveMinutes() throws GoogleApiClientNotConnectedException {

        //check for valid google api
        if (googleApiClient == null)
            throw new GoogleApiClientNotConnectedException();


        //start timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTimestamp = calendar.getTimeInMillis();


        // end timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endTimestamp = calendar.getTimeInMillis();


        //execute async task in background thread
        Log.d(TAG, "getSleepData: async task executed");
        ReadActiveMinutesTask task = new ReadActiveMinutesTask();
        task.googleApiClient = googleApiClient;
        task.listener = activeMinuteListener;
        task.execute(startTimestamp, endTimestamp);
    }

    public void getBurnedCalories() throws GoogleApiClientNotConnectedException {

        //check for valid google api
        if (googleApiClient == null)
            throw new GoogleApiClientNotConnectedException();

        //start timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTimestamp = calendar.getTimeInMillis();


        // end timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endTimestamp = calendar.getTimeInMillis();


        //execute async task in background thread
        Log.d(TAG, "getSleepData: async task executed");
        ReadBurnedCaloriesTask task = new ReadBurnedCaloriesTask();
        task.googleApiClient = googleApiClient;
        task.listener = burnedCaloriesListener;
        task.execute(startTimestamp, endTimestamp);
    }

    public void getStepsCount() throws GoogleApiClientNotConnectedException {

        //check for valid google api
        if (googleApiClient == null)
            throw new GoogleApiClientNotConnectedException();

        //start timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTimestamp = calendar.getTimeInMillis();


        // end timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endTimestamp = calendar.getTimeInMillis();


        //execute async task in background thread
        Log.d(TAG, "getSleepData: async task executed");
        ReadStepCountTask task = new ReadStepCountTask();
        task.googleApiClient = googleApiClient;
        task.listener = stepCountListener;
        task.execute(startTimestamp, endTimestamp);
    }

    public void getDistance() throws GoogleApiClientNotConnectedException {

        //check for valid google api
        if (googleApiClient == null)
            throw new GoogleApiClientNotConnectedException();

        //start timestamp
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long startTimestamp = calendar.getTimeInMillis();


        // end timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        long endTimestamp = calendar.getTimeInMillis();


        //execute async task in background thread
        Log.d(TAG, "getSleepData: async task executed");
        ReadDistanceTask task = new ReadDistanceTask();
        task.googleApiClient = googleApiClient;
        task.listener = distanceListener;
        task.execute(startTimestamp, endTimestamp);
    }
}
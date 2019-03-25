package si.ijs.wellco_googlefitdata.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import si.ijs.wellco_googlefitdata.exceptions.GoogleApiClientNotConnectedException;
import si.ijs.wellco_googlefitdata.listeners.OnCaloriesBurnedResult;

import static java.text.DateFormat.getDateInstance;


public class ReadBurnedCaloriesTask extends AsyncTask<Long, Void, Void> {


    private final String TAG = ReadActiveMinutesTask.class.getName();

    public OnCaloriesBurnedResult listener;
    public GoogleApiClient googleApiClient;


    @Override
    protected Void doInBackground(Long... longs) {
        if (longs.length != 2) {
            Log.e(TAG, "doInBackground: parameters not equals 2");
            return null;
        }


        long start = longs[0]; //start timestamp
        long end = longs[1]; //end timestamp

        // Begin by creating the query.
        DataReadRequest readRequest = null;
        try {
            readRequest = query(start, end);
        } catch (GoogleApiClientNotConnectedException e) {
            e.printStackTrace();
        }

        // [START read_dataset]
        // Invoke the History API to fetch the data with the query and await the result of
        // the read request.
        DataReadResult dataReadResult =
                Fitness.HistoryApi.readData(googleApiClient, readRequest).await(1, TimeUnit.MINUTES);



        // [END read_dataset]

        //read result
        readDataResult(dataReadResult);

        return null;
    }


    /**
     * Return a {@link DataReadRequest} for all activity data in given time (start, end time)
     */
    private DataReadRequest query(long startTime, long endTime) throws GoogleApiClientNotConnectedException {

        if (!googleApiClient.isConnected())
            throw new GoogleApiClientNotConnectedException();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));


        // [START build_read_data_request]


        //build read request
        DataReadRequest readRequest = new DataReadRequest.Builder()
                //.aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        // [END build_read_data_request]

        return readRequest;
    }

    /**
     * Method that read data result and notify on
     * @param dataReadResult DataReadResult
     */
    private void readDataResult(DataReadResult dataReadResult) {
        int totalCalories = 0;

        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                for (DataSet dataSet : bucket.getDataSets()) {
                    totalCalories += dataSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                }
            }
        }

        Log.d(TAG, "readDataResult: Total active time: " + totalCalories);

        listener.onCaloriesDataResult(totalCalories);
    }

}

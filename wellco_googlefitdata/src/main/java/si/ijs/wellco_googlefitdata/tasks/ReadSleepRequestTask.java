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
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import java.util.List;
import java.util.concurrent.TimeUnit;
import si.ijs.wellco_googlefitdata.dummy.GoogleFitSleepData;
import si.ijs.wellco_googlefitdata.exceptions.GoogleApiClientNotConnectedException;
import si.ijs.wellco_googlefitdata.listeners.OnSleepDataResult;
import static java.text.DateFormat.getDateInstance;
import static si.ijs.wellco_googlefitdata.utils.GoogleActivityTypeCodes.AWAKE;
import static si.ijs.wellco_googlefitdata.utils.GoogleActivityTypeCodes.DEEP_SLEEP;
import static si.ijs.wellco_googlefitdata.utils.GoogleActivityTypeCodes.LIGHT_SLEEP;
import static si.ijs.wellco_googlefitdata.utils.GoogleActivityTypeCodes.REM_SLEEP;
import static si.ijs.wellco_googlefitdata.utils.GoogleActivityTypeCodes.SLEEPING;

/**
 *  Create a {@link DataSet} to insert data into the History API, and
 *  then create and execute a {@link DataReadRequest} to verify the insertion succeeded.
 *  By using an {@link AsyncTask}, we can schedule synchronous calls, so that we can query for
 *  data after confirming that our insert was successful. Using asynchronous calls and callbacks
 *  would not guarantee that the insertion had concluded before the read request was made.
 *  An example of an asynchronous call using a callback can be found in the example
 *  on deleting data below.
 */
public class ReadSleepRequestTask extends AsyncTask<Long, Long, Void> {

    private final String TAG = ReadSleepRequestTask.class.getName();

    public OnSleepDataResult onDataResult;
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
            readRequest = querySleepData(start, end);
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
    private DataReadRequest querySleepData(long startTime, long endTime) throws GoogleApiClientNotConnectedException {

        if (!googleApiClient.isConnected())
            throw new GoogleApiClientNotConnectedException();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));


        // [START build_read_data_request]


        //build read request
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
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

        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    parseDataSet(dataSet);
                }
            }
        } /*else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                parseDataSet(dataSet);
            }
        }*/
        // [END parse_read_data_result]

    }


    private void parseDataSet(DataSet dataSet) {

        int sleepDuration = 0;
        int lightSleepDuration = 0;
        int deepSleepDuration = 0;
        int remSleepDuration = 0;
        int awakeTimeDuration = 0;

        String events = "";


        for (DataPoint dp : dataSet.getDataPoints()) {

            List<Field> filedList = dp.getDataType().getFields();

            int activityValue = dp.getValue(filedList.get(0)).asInt();

            switch (activityValue) {
                case SLEEPING:
                    events += "SLEEPING<" + dp.getStartTime(TimeUnit.MILLISECONDS) + ", " + dp.getEndTime(TimeUnit.MILLISECONDS) + ">;";
                    sleepDuration = getDuration(dp);
                    break;
                case LIGHT_SLEEP:
                    events += "LIGHT_SLEEP<" + dp.getStartTime(TimeUnit.MILLISECONDS) + ", " + dp.getEndTime(TimeUnit.MILLISECONDS) + ">;";
                    lightSleepDuration = getDuration(dp);
                    break;
                case DEEP_SLEEP:
                    events += "DEEP_SLEEP<" + dp.getStartTime(TimeUnit.MILLISECONDS) + ", " + dp.getEndTime(TimeUnit.MILLISECONDS) + ">;";
                    deepSleepDuration = getDuration(dp);
                    break;
                case REM_SLEEP:
                    events += "REM_SLEEP<" + dp.getStartTime(TimeUnit.MILLISECONDS) + ", " + dp.getEndTime(TimeUnit.MILLISECONDS) + ">;";
                    remSleepDuration = getDuration(dp);
                    break;
                case AWAKE:
                    events += "AWAKE<" + dp.getStartTime(TimeUnit.MILLISECONDS) + ", " + dp.getEndTime(TimeUnit.MILLISECONDS) + ">;";
                    awakeTimeDuration = getDuration(dp);
                    break;

            }
        }

        //end of query


        //create sleep data object and notify listener
        GoogleFitSleepData data = new GoogleFitSleepData(sleepDuration, sleepDuration-awakeTimeDuration, deepSleepDuration, lightSleepDuration, remSleepDuration, 0, 0, events);
        onDataResult.onDataResult(data);
    }


    private int getDuration(DataPoint dp) {
        int duration = 0;
        float sleepHours;

        for (Field field : dp.getDataType().getFields()) {
            if (dp.getOriginalDataSource().getAppPackageName() != null) {
                if (dp.getOriginalDataSource().getAppPackageName().toLowerCase().contains("sleep") && field.getName().toLowerCase().contains("duration")) {
                    Value value = dp.getValue(field);
                    sleepHours = (float) (Math.round((value.asInt() * 2.778 * 0.0000001 * 10.0)) / 10.0);
                    float sleepHour = sleepHours * 60; // minutes
                    duration += sleepHour;
                }
            }
        }
        return duration;
    }
}


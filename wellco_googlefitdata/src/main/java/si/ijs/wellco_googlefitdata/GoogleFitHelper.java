package si.ijs.wellco_googlefitdata;


import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import java.util.concurrent.TimeUnit;
import si.ijs.wellco_googlefitdata.exceptions.GoogleApiClientNotConnectedException;
import si.ijs.wellco_googlefitdata.listeners.OnSleepDataResult;
import static java.text.DateFormat.getDateInstance;

public class GoogleFitHelper {

    //class variables
    private static final String TAG = GoogleFitHelper.class.getName();

    //object variables
    private GoogleApiClient googleApiClient;
    private OnSleepDataResult listener;

    /**
     * Constructor
     * @param googleApiClient
     * @param listener
     */
    public GoogleFitHelper(GoogleApiClient googleApiClient, OnSleepDataResult listener) {
        this.googleApiClient = googleApiClient;
        this.listener = listener;
    }

    public void getSleepData(long startTimestamp, long endTimestamp) throws GoogleApiClientNotConnectedException {

        //check for valid google api
        if (googleApiClient == null)
            throw new GoogleApiClientNotConnectedException();


        //execute async task in background thread
        Log.d(TAG, "getSleepData: async task executed");
        new ReadRequestTask().execute(startTimestamp, endTimestamp);
    }




    /**
     *  Create a {@link DataSet} to insert data into the History API, and
     *  then create and execute a {@link DataReadRequest} to verify the insertion succeeded.
     *  By using an {@link AsyncTask}, we can schedule synchronous calls, so that we can query for
     *  data after confirming that our insert was successful. Using asynchronous calls and callbacks
     *  would not guarantee that the insertion had concluded before the read request was made.
     *  An example of an asynchronous call using a callback can be found in the example
     *  on deleting data below.
     */
    class ReadRequestTask extends AsyncTask<Long, Long, Void> {

        public OnSleepDataResult onDataResult;

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
    }

    /**
     * Return a {@link DataReadRequest} for all activity data in given time (start, end time)
     */
    public DataReadRequest querySleepData(long startTime, long endTime) throws GoogleApiClientNotConnectedException {

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
        // TODO: 1.3.2019 go trough readResult and notify listener


    }
}

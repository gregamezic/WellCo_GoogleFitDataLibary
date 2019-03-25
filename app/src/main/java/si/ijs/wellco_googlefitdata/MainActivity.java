package si.ijs.wellco_googlefitdata;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import java.util.Calendar;
import si.ijs.wellco_googlefitdata.dummy.GoogleFitSleepData;
import si.ijs.wellco_googlefitdata.exceptions.GoogleApiClientNotConnectedException;
import si.ijs.wellco_googlefitdata.listeners.OnActiveMinutesResult;
import si.ijs.wellco_googlefitdata.listeners.OnCaloriesBurnedResult;
import si.ijs.wellco_googlefitdata.listeners.OnDistanceDataResult;
import si.ijs.wellco_googlefitdata.listeners.OnSleepDataResult;
import si.ijs.wellco_googlefitdata.listeners.OnStepCountResult;

public class MainActivity extends AppCompatActivity implements OnSleepDataResult, OnCaloriesBurnedResult, OnActiveMinutesResult, OnStepCountResult, OnDistanceDataResult {


    private final String TAG = MainActivity.class.getName();

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;

    public static GoogleApiClient mClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }


        buildFitnessClient();
    }


    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or
     *  having multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");

                                // Now you can make calls to the Fitness APIs.  What to do?
                                GoogleFitHelper helper = new GoogleFitHelper(mClient, MainActivity.this, MainActivity.this,
                                        MainActivity.this, MainActivity.this, MainActivity.this);
                                try {
                                    // start query and wait in listener for result

                                    helper.getSleepData();
                                    helper.getActiveMinutes();
                                    helper.getBurnedCalories();
                                    helper.getStepsCount();
                                    helper.getDistance();


                                } catch (GoogleApiClientNotConnectedException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        /*Snackbar.make(
                                MainActivity.this.findViewById(R.id.main_activity_view),
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage(),
                                Snackbar.LENGTH_INDEFINITE).show();*/
                    }
                })
                .build();
    }

    @Override
    public void onDataResult(GoogleFitSleepData googleFitSleepData) {
        int a = googleFitSleepData.getSleepDuration();
    }

    @Override
    public void onActiveMinutesDataResult(long activeMinutes) {
        Log.d(TAG, "onActiveMinutesDataResult: " + activeMinutes);
    }

    @Override
    public void onCaloriesDataResult(long calories) {
        Log.d(TAG, "onCaloriesDataResult: " + calories);
    }

    @Override
    public void onStepCountResult(int steps) {
        Log.d(TAG, "onStepCountResult: " + steps);
    }

    @Override
    public void onDistanceResult(int distance) {
        Log.d(TAG, "onDistanceResult: " + distance);
    }
}

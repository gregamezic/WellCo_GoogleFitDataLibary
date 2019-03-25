package si.ijs.wellco_googlefitdata.dummy;

import com.google.android.gms.common.api.GoogleApiClient;

import si.ijs.wellco_googlefitdata.listeners.OnSleepDataResult;
import static java.text.DateFormat.getDateInstance;

public class GoogleFitSleepData {


    private static final String TAG = GoogleFitSleepData.class.getName();

    //object variables values
    private int sleepDuration;
    private int sleepDurationWoSleep;
    private int deepSleep;
    private int lightSleep;
    private int REMSleep;
    private long startSleep;
    private long endSleep;
    private String sleepCycles;



    public GoogleFitSleepData(int sleepDuration, int sleepDurationWoSleep, int deepSleep, int lightSleep, int REMSleep, long startSleep, long endSleep, String sleepCycles) {
        this.sleepDuration = sleepDuration;
        this.sleepDurationWoSleep = sleepDurationWoSleep;
        this.deepSleep = deepSleep;
        this.lightSleep = lightSleep;
        this.REMSleep = REMSleep;
        this.startSleep = startSleep;
        this.endSleep = endSleep;
        this.sleepCycles = sleepCycles;
    }

    public int getSleepDuration() {
        return sleepDuration;
    }

    public int getSleepDurationWoSleep() {
        return sleepDurationWoSleep;
    }

    public int getDeepSleep() {
        return deepSleep;
    }

    public int getLightSleep() {
        return lightSleep;
    }

    public int getREMSleep() {
        return REMSleep;
    }

    public long getStartSleep() {
        return startSleep;
    }

    public long getEndSleep() {
        return endSleep;
    }

    public String getSleepCycles() {
        return sleepCycles;
    }
}

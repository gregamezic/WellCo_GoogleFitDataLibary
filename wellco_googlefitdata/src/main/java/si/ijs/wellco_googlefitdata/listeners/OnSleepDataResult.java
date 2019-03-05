package si.ijs.wellco_googlefitdata.listeners;

import si.ijs.wellco_googlefitdata.dummy.GoogleFitSleepData;

public interface OnSleepDataResult {

    /**
     * Interface callback with GoogleFitSleepData custom object
     * @param googleFitSleepData {@link GoogleFitSleepData}
     */
    void onDataResult(GoogleFitSleepData googleFitSleepData);

}

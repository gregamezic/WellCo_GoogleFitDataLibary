package si.ijs.wellco_googlefitdata.exceptions;

public class GoogleFitDataIsNullException extends Exception {

    @Override
    public String toString() {
        return "GoogleFitSleepData object is null. Did you call getGoogleFitSleepData method?";
    }
}

package si.ijs.wellco_googlefitdata.exceptions;

public class GoogleApiClientNotConnectedException extends Exception {

    @Override
    public String toString() {
        return "GoogleApiClient is not connected!";
    }
}

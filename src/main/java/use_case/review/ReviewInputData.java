package use_case.review;

public class ReviewInputData {
    private final boolean gotIt;

    public ReviewInputData(boolean gotIt) {
        this.gotIt = gotIt;
    }

    public boolean isGotIt() {
        return gotIt;
    }
}

package use_case.review;

public class ReviewSummaryOutputData {
    private final int gotItCount;
    private final int againCount;

    public ReviewSummaryOutputData(int gotItCount, int againCount) {
        this.gotItCount = gotItCount;
        this.againCount = againCount;
    }

    public int getGotItCount() {
        return gotItCount;
    }

    public int getAgainCount() {
        return againCount;
    }
}

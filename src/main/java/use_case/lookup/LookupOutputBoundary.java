package use_case.lookup;

import java.util.List;

public interface LookupOutputBoundary {
    void prepareSuccessView(List<LookupOutputData> meanings);

    void prepareFailView(String error);
}

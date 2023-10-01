package splitter;

import java.util.ArrayList;
import java.util.List;

public class ResultsHolder {
    private static ResultsHolder instance;
    private List<String> results;

    private ResultsHolder() {
        results = new ArrayList<>();
    }

    public static ResultsHolder getInstance() {
        if (instance == null) {
            instance = new ResultsHolder();
        }
        return instance;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public void clearResults() {
        this.results.clear();
    }
}

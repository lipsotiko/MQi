package io.egia.mqi.measure;

public class MeasureResults {

    private boolean continueProcessing = true;

    private boolean denominator;

    public boolean getContinueProcessing() {
        return this.continueProcessing;
    }

    public void setContinueProcessing(boolean continueProcessing) {
        this.continueProcessing = continueProcessing;
    }

    public void setResults(boolean denominator) {
        this.denominator = denominator;
    }

    public boolean getResult() {
        return denominator;
    }
}

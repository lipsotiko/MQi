package io.egia.mqi.measure;

public class MeasureProcessorException extends Exception {
    private static final long serialVersionUID = 1L;

    public MeasureProcessorException(String msg) {
        super(msg);
        System.err.println(msg);
    }
}
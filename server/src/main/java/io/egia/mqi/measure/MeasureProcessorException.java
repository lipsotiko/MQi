package io.egia.mqi.measure;

public class MeasureProcessorException extends Exception {
    private static final long serialVersionUID = 1L;

    MeasureProcessorException(String msg, Exception e) {
        super(msg, e);
        e.printStackTrace();
        System.err.println("Error: " + msg);
    }

    MeasureProcessorException(String msg) {
        super(msg);
        System.err.println("Error: " + msg);
    }
}
package io.egia.mqi.measure;

public class MeasureServiceException extends Exception {

    MeasureServiceException(String msg) {
        super(msg);
        System.err.println("Error: " + msg);
    }
}
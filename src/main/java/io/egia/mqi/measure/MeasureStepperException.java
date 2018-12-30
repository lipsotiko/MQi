package io.egia.mqi.measure;

public class MeasureStepperException extends Exception {

    MeasureStepperException(String msg, Exception e) {
        super(msg, e);
        e.printStackTrace();
        System.err.println("Error: " + msg);
    }

    MeasureStepperException(String msg) {
        super(msg);
        System.err.println("Error: " + msg);
    }
}
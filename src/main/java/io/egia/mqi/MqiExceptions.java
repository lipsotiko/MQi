package io.egia.mqi;

public class MqiExceptions extends Exception {

	public MqiExceptions(String msg) {
		super(msg);
		System.err.println(msg);
	}
}

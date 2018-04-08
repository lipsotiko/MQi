package io.egia.mqi;

public class MqiExceptions extends Exception {
	private static final long serialVersionUID = 1L;

	public MqiExceptions(String msg) {
		super(msg);
		System.err.println(msg);
	}
}

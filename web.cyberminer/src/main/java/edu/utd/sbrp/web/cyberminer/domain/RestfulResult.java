package edu.utd.sbrp.web.cyberminer.domain;

import java.util.concurrent.TimeUnit;

public class RestfulResult {

	private RestfulResultStatus status;
	private Object data;
	private String message;
	private final long startTime;
	private long serverTime;
	
	public RestfulResult() {
		startTime = System.nanoTime();
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getServerTime() {
		this.serverTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
		return serverTime;
	}
	public RestfulResultStatus getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void addMessage(String message) {
		if (this.message == null) {
			this.message = "";
		} else if (this.message.length() > 0) {
			this.message += " ";
		}
		this.message += message;
	}
	
	public void success(Object data) {
		status = RestfulResultStatus.success;
		setData(data);
		getServerTime(); // way to initiate serverTime
	}
	public void fail(String message) {
		status = RestfulResultStatus.fail;
		addMessage(message);
	}
	public void error(String message) {
		status = RestfulResultStatus.error;
		addMessage(message);
	}

	
}

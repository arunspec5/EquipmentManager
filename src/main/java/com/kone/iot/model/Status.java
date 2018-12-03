package com.kone.iot.model;

public enum Status {
	Running("Running"), Stopped("Stopped");
	private String status;

	Status(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}
}

package de.adesso.alarm.entity;

public enum AlarmStatus {

	ACTIVE (0), INACTIVE (1), TRIGGERED (2);
	
	private int status;	
	private AlarmStatus(int status) {
		this.status = status;
	}
}

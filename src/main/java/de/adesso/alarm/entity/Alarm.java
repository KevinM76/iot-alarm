package de.adesso.alarm.entity;

import java.util.Date;

/**
 * Simple model class.
 * 
 */
public class Alarm {
	

	private String item;
	private AlarmStatus status = AlarmStatus.INACTIVE;
    private Date activationDate = null;
    private Date alarmDate = null;
    private String reason = "";

	public Alarm() {
    }

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public AlarmStatus getStatus() {
		return status;
	}

	public void setStatus(AlarmStatus status) {
		this.status = status;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}

	public Date getAlarmDate() {
		return alarmDate;
	}

	public void setAlarmDate(Date alarmDate) {
		this.alarmDate = alarmDate;
	}
	
    public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}

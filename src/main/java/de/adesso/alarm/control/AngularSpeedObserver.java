package de.adesso.alarm.control;

import java.util.Map;

import io.relayr.java.model.action.Reading;
import rx.Observer;

public class AngularSpeedObserver implements Observer<Reading> {

	private static final int THRESHOLD = 700;
	private AlarmManager alarmManager;

	/**
	 * Observe the readings of an acceleration device.
	 * @param alarmManager used to trigger an alarm.
	 */
	public AngularSpeedObserver(AlarmManager alarmManager) {
		this.alarmManager = alarmManager;
	}

	@Override
	public void onNext(Reading reading) {
		//relayr: angularSpeed
		if (reading.meaning.equals("gyro")) {
			System.out.println("Gyroskop speed: " + reading.value);
			Map<String, Double> values = (Map<String, Double>) reading.value;
			Double x = values.get("x");
			Double y = values.get("y");
			Double z = values.get("z");
			Double acceleration = x * x + y * y + z * z;
			System.out.println("Gyroskop angularSpeed:" + acceleration);
			if (acceleration > THRESHOLD) {
				alarmManager.triggerAlarm("Device moved.");
			}
		}
	}

	@Override
	public void onCompleted() {
	}

	@Override
	public void onError(Throwable arg0) {
		alarmManager.triggerAlarm("Timeout");
	}

}

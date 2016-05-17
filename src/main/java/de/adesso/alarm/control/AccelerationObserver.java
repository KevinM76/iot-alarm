package de.adesso.alarm.control;

import java.util.Map;

import io.relayr.java.model.action.Reading;
import rx.Observer;

public class AccelerationObserver implements Observer<Reading> {

	private static final double THRESHOLD = 0.05;
	private AlarmManager alarmManager;
	private Map<String, Double> last = null;

	/**
	 * Observe the readings of an acceleration device.
	 * 
	 * @param alarmManager
	 *            used to trigger an alarm.
	 */
	public AccelerationObserver(AlarmManager alarmManager) {
		this.alarmManager = alarmManager;
	}

	@Override
	public void onNext(Reading reading) {

		if (reading.meaning.equals("acceleration")) {
			System.out.println("Gyroskop acceleration: " + reading.value);
			if (altered((Map<String, Double>) reading.value)) {
				alarmManager.triggerAlarm("Device rotated.");
			}
		}
	}

	private boolean altered(Map<String, Double> values) {
		if (last != null) {
			for (String key : values.keySet()) {
				Double double1 = values.get(key);
				Double double2 = last.get(key);
				if ((double1 != null) && (double2 != null)) {
					if (Math.abs(double1 - double2) > THRESHOLD) {
						last = null;
						return true;
					}
				}
			}
		}
		last = values;
		return false;
	}

	@Override
	public void onCompleted() {
		last = null;
	}

	@Override
	public void onError(Throwable arg0) {
		last = null;
		alarmManager.triggerAlarm("Timeout");
	}

}

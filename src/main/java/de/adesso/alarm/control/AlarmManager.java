package de.adesso.alarm.control;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;

import com.sun.mail.util.LogOutputStream;

import de.adesso.alarm.entity.Alarm;
import de.adesso.alarm.entity.AlarmStatus;
import io.relayr.java.RelayrJavaSdk;
import io.relayr.java.model.Device;
import io.relayr.java.model.User;
import io.relayr.java.model.action.Reading;
import retrofit.RetrofitError;
import rx.Observable;

/**
 * The alarm manager interacts with the IoT-Cloud and knows the state of the wunderbar.
 * 
 *
 */
@ApplicationScoped
public class AlarmManager {
	
	private static final String BEARER_TOKEN = "Bearer JOVmSFDeTWGa6XzRaPML0l0QDX.QG_no";
	//private static final String DEVICE_ID = "78e0f7bd-18b7-492e-96cd-43000cfba258";//wunderbar
	private static final String DEVICE_ID = "73de73af-2bdf-4512-8ae9-e53f9f6d173f";//xdk
	private static final Logger LOG = Logger.getLogger(AlarmManager.class.getName());
	
	private Alarm alarm = new Alarm();

	private Device device;
	private Observable<Reading> readings;

	
	@PostConstruct
	public void init() {
		try {
			//cacheModels(true).
			new RelayrJavaSdk.Builder().setToken(BEARER_TOKEN).cacheModels(true).build();
		} catch (Exception e) {
			LOG.warning("Enabling mock mode as an error occured: " + e.getMessage());
			new RelayrJavaSdk.Builder().inMockMode(true).setToken(BEARER_TOKEN).cacheModels(true).build();
		}
		try {
			device = findDevice(DEVICE_ID);
		} catch (IllegalArgumentException e) {
			LOG.severe(e.getMessage());
		}
		alarm.setItem(device.getName());
	}
		
	/**
	 * Get the current alarm.
	 * TODO: replace logic to circle through the different status.
	 * @return current alarm
	 */
	public Alarm getAlarm() {
		return alarm;
	}
	
	/**
	 * Only the alarm status is read and the alarm is then set accordingly.
	 * @param alarmStatus to set
	 * @return the updated alarm
	 */
	public Alarm setStatus(final Alarm alarmStatus) {
		switch (alarmStatus.getStatus()) {
		case INACTIVE:
			inactivate(alarmStatus);
			break;

		case ACTIVE:
			activate();
			break;

		default:
			break;
		}
		return alarm;
	}

	private void activate() {
		alarm.setStatus(AlarmStatus.ACTIVE);
		alarm.setAlarmDate(null);
		alarm.setActivationDate(new Date());
		alarm.setReason("");
		readings = device.subscribeToCloudReadings().timeout(30, TimeUnit.SECONDS);
		readings.subscribe(new AngularSpeedObserver(this));
		//readings.subscribe(new AccelerationObserver(this));
	}

	private void inactivate(final Alarm alarmStatus) {
		alarm.setStatus(AlarmStatus.INACTIVE);
		alarm.setAlarmDate(null);
		alarmStatus.setActivationDate(null);
		alarm.setReason("");
		device.unSubscribeToCloudReadings();
	}

	public void triggerAlarm(final String reason) {
		alarm.setStatus(AlarmStatus.TRIGGERED);
		alarm.setAlarmDate(new Date());
		alarm.setReason(reason);
		device.unSubscribeToCloudReadings();
	}
	
	private Device findDevice(String deviceId) throws IllegalArgumentException {
		User user = null;
		String id = DEVICE_ID;
		try {
			user = RelayrJavaSdk.getUser().toBlocking().single();
		} catch (RuntimeException e) {
			new RelayrJavaSdk.Builder().inMockMode(true).setToken(BEARER_TOKEN).cacheModels(true).build();
			user = RelayrJavaSdk.getUser().toBlocking().single();
			//xdk: 24399746-42dc-431b-aefe-b41debfe5b1b
			id = "d4650445-d85f-4c4a-bbb3-2da7ccd9c28d"; //Gyroskop
		}
		List<Device> devices = user.getDevices().toBlocking().first();
		for (Device device : devices) {
			if (device.getId().equals(id)) {
				return device;
			}
		}
		throw new IllegalArgumentException("Device with id '" + deviceId + "' not found.");
	}

}

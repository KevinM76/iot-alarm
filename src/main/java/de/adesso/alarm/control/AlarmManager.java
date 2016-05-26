package de.adesso.alarm.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import de.adesso.alarm.entity.Alarm;
import de.adesso.alarm.entity.AlarmStatus;
import io.relayr.java.RelayrJavaSdk;
import io.relayr.java.model.Device;
import io.relayr.java.model.User;
import io.relayr.java.model.action.Reading;
import rx.Observable;

/**
 * The alarm manager interacts with the IoT-Cloud and knows the state of the wunderbar.
 * 
 *
 */
@ApplicationScoped
public class AlarmManager {
	
	private static final String MOCK_DEVICE_ID = "d4650445-d85f-4c4a-bbb3-2da7ccd9c28d";
	private static final Logger LOG = Logger.getLogger(AlarmManager.class.getName());
	private static final String KEY_DEVICE_ID = "DEVICE_ID";
	private static final String KEY_BEARER_TOKEN = "BEARER_TOKEN";
	private static final String KEY_TIMEOUT = "TIMEOUT";
	private static final String DEFAULT_TIMEOUT = "10000";
	
	private Alarm alarm = new Alarm();

	private Device device;
	private Observable<Reading> readings;
	
	private String deviceId;
	private String bearerToken;
	private long timeout;

	
	@PostConstruct
	public void init() {
		
		try {
			readConfiguration();
		} catch (IOException e) {
			LOG.severe("Could not read the configuration. " + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);
		}
		
		try {
			//cacheModels(true).
			new RelayrJavaSdk.Builder().setToken(bearerToken).cacheModels(true).build();
		} catch (Exception e) {
			LOG.warning("Enabling mock mode as an error occured: " + e.getMessage());
			new RelayrJavaSdk.Builder().inMockMode(true).setToken(bearerToken).cacheModels(true).build();
		}
		try {
			device = findDevice(deviceId);
		} catch (IllegalArgumentException e) {
			LOG.severe(e.getMessage());
		}
		alarm.setItem(device.getName());
	}
		
	private void readConfiguration() throws IOException {
		//TODO: Try to read from system environment variables first
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("alarm_manager.properties");
		Properties config = new Properties();
		config.load(input);
		this.bearerToken = config.getProperty(KEY_BEARER_TOKEN);
		this.deviceId = config.getProperty(KEY_DEVICE_ID);
		this.timeout = Long.parseLong(config.getProperty(KEY_TIMEOUT, DEFAULT_TIMEOUT));
		
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
		readings = device.subscribeToCloudReadings().timeout(timeout, TimeUnit.MILLISECONDS);
		readings.subscribe(new AngularSpeedObserver(this));
		readings.subscribe(new AccelerationObserver(this));
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
		String id = deviceId;
		try {
			user = RelayrJavaSdk.getUser().toBlocking().single();
		} catch (RuntimeException e) {
			new RelayrJavaSdk.Builder().inMockMode(true).setToken(bearerToken).cacheModels(true).build();
			LOG.warning("Trying mock mode. " + e.getMessage());
			user = RelayrJavaSdk.getUser().toBlocking().single();
			id = MOCK_DEVICE_ID;
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

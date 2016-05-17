package de.adesso.alarm.boundary;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.adesso.alarm.control.AlarmManager;
import de.adesso.alarm.entity.Alarm;


/**
 * Front end controller.
 *
 */
@Path("/alarmstatus")
@RequestScoped
public class AlarmFacade {
	@Inject
	AlarmManager manager;

    /**
     * Retrieve the current alarm status from the alarm manager.
     * @return Alarm status
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Alarm getAlarmStatus() {
        return manager.getAlarm();
    }

    /**
     * Retrieve the current alarm status from the alarm manager.
     * @return Alarm status
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Alarm setAlarmStatus(Alarm alarm) {
    	manager.setStatus(alarm);
        return manager.getAlarm();
    }

}

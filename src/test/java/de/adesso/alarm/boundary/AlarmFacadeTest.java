package de.adesso.alarm.boundary;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.adesso.alarm.control.AlarmManager;
import de.adesso.alarm.entity.Alarm;

public class AlarmFacadeTest {

    
    @Mock
    private AlarmManager manager;
    
    @InjectMocks 
    private AlarmFacade alarmFacade;

    @Before
    public void setUp() throws Exception {
    	MockitoAnnotations.initMocks(this);
    	Alarm alarm = new Alarm();
    	alarm.setItem("Testalarm");
		when(manager.getAlarm()).thenReturn(alarm);
    }

    @Test
    public void testGet() throws Exception {
        final Alarm result = this.alarmFacade.getAlarmStatus();
        assertThat("the result message is world", result.getItem(), is("Testalarm"));
    }

    @Test
    public void testPathAnnotation() throws Exception {
        assertNotNull(this.alarmFacade.getClass()
                                    .getAnnotations());
        assertThat("The controller has the annotation Path", this.alarmFacade.getClass()
                                                                           .isAnnotationPresent(Path.class));

        final Path path = this.alarmFacade.getClass()
                                        .getAnnotation(Path.class);
        assertThat("The path is /alarmstatus", path.value(), is("/alarmstatus"));
    }

    @Test
    public void testScope() throws Exception {
        assertNotNull(this.alarmFacade.getClass()
                                    .getAnnotations());
        assertThat("The controller has the annotation RequestScoped", this.alarmFacade.getClass()
                                                                                    .isAnnotationPresent(
                                                                                            RequestScoped.class));
    }
}
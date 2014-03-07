package edu.msu.nscl.olog;

import com.intellij.mock.Mock;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.time.DateUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


/**
 * Created by eschuhmacher on 2/11/14.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(AttachmentManager.class)
public class CompareVersionsTest {


    @BeforeClass
    public static void setup() {
        System.setProperty("persistenceUnit", "olog_test");
    }


    @Test
    public void findLogByAttribute() throws OlogException {
        mockStatic(AttachmentManager.class);
        PowerMockito.when(AttachmentManager.findAll(Mockito.anyLong())).thenReturn(new XmlAttachments());
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("sweep.crystal_name", "ECF_229");
        map.add("limit", "20");
        map.add("page", "1");
        Logs newLogs = LogManager.findLog(map);
        Logs oldLogs = LogManagerTest.findLog(map);
        assertEquals(newLogs.size(), oldLogs.size());
        for(int i =0 ; i< newLogs.size() ; i++) {
            compareLogs(newLogs.get(i), oldLogs.get(i));
        }
    }

    @Test
    public void createLogTest() throws OlogException {
        Log log = LogManager.findLog(2313l);
        log.setId(null);
        log.setEntry(null);
        log.setEntryId(null);
        log.setVersion(null);
        log.setOwner("testLog");
        Log newLog = LogManager.create(log);
        Log oldLog = LogManager.create(log);
        assertEquals(newLog.getAttributes(), oldLog.getAttributes());
        assertEquals(newLog.getDescription(), oldLog.getDescription());
        assertEquals(newLog.getOwner(), oldLog.getOwner());
        assertEquals(newLog.getSource(), oldLog.getSource());
    }

    @Test
    public void findLogByDate() throws OlogException {
        mockStatic(AttachmentManager.class);
        PowerMockito.when(AttachmentManager.findAll(Mockito.anyLong())).thenReturn(new XmlAttachments());
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("start", String.valueOf(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() / 1000));
        Logs newLogs = LogManager.findLog(map);
        Logs oldLogs = LogManagerTest.findLog(map);
        assertEquals(newLogs.size(), oldLogs.size());
        for(int i =0 ; i< newLogs.size() ; i++) {
            compareLogs(newLogs.get(i), oldLogs.get(i));
        }
    }

    private void compareLogs(final Log firstLog, final Log secondLog) {
        assertEquals(firstLog, secondLog);
    }
}

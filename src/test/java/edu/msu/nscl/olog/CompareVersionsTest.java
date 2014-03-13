package edu.msu.nscl.olog;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.time.DateUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MultivaluedMap;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


/**
 * Created by eschuhmacher on 2/11/14.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({AttachmentManager.class, JPAUtil.class})
@SuppressStaticInitializationFor("edu.msu.nscl.olog.JPAUtil")
public class CompareVersionsTest {


    @BeforeClass
    public static void setup() throws OlogException {
        mockStatic(AttachmentManager.class);
        PowerMockito.when(AttachmentManager.findAll(Mockito.anyLong())).thenReturn(new XmlAttachments());
        PowerMockito.when(AttachmentManager.findAll(Mockito.anyString())).thenReturn(new LinkedList<Long>());
    }

    @Test
    public void compareTest() throws OlogException {
        mockPersistance();
        findLogByAttribute();
        createLogTest();
        findLogByDate();
    }

    public void findLogByAttribute() throws OlogException {
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


    public void createLogTest() throws OlogException {
        Log log = LogManager.findLog(2006252l);
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


    public void findLogByDate() throws OlogException {
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
        assertEquals(firstLog.getAttributes(), secondLog.getAttributes());
        assertEquals(firstLog.getXmlProperties(), secondLog.getXmlProperties());
    }

    private void mockPersistance() {
        PowerMockito.spy(JPAUtil.class);
        PowerMockito.when(JPAUtil.getEntityManagerFactory()).thenReturn(JPAUtilTest.getEntityManagerFactory());
    }
}

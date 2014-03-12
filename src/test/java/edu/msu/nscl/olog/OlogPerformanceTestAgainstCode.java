package edu.msu.nscl.olog;


import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.commons.lang.time.DateUtils;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.jcr.*;

import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AttachmentManager.class)
public class OlogPerformanceTestAgainstCode {

    private static BufferedWriter out;
    private static long logId;

    @BeforeClass
    public static void prepare() throws IOException, OlogException {
        System.setProperty("persistenceUnit", "olog_test");
        String filePath = OlogPerformanceTestAgainstCode.class.getResource("OlogPerformanceTestAgainstCode.class").getPath();
        File outputFile = new File(filePath.substring(0,filePath.indexOf("target")) + "src/test/java/edu/msu/nscl/olog/OlogPerformanceTestAgainstCodeResult.txt");
        out = new BufferedWriter(new FileWriter(outputFile));
        mockStatic(AttachmentManager.class);
        PowerMockito.when(AttachmentManager.findAll(Mockito.anyLong())).thenReturn(new XmlAttachments());
        PowerMockito.when(AttachmentManager.findAll(Mockito.anyString())).thenReturn(new LinkedList<Long>());

    }

    @AfterClass
    public static void close() throws IOException {
        out.close();
    }


    private void runTests(final String dbTypeText) throws IOException, OlogException, RepositoryException {
        findLogByAttribute(dbTypeText);
        findLogByDescription(dbTypeText);
        insertLog(dbTypeText);
        insertLog2(dbTypeText);
        findLogByDate(dbTypeText);
        findLogById(dbTypeText);
        removeLog(dbTypeText);
        findAllProperties(dbTypeText);
        findPropertyByName(dbTypeText);
        createProperty(dbTypeText);
        removeProperty(dbTypeText);
        findAllLogbooks(dbTypeText);
        findLogbookByName(dbTypeText);
        createLogbook(dbTypeText);
        removeLogbook(dbTypeText);
        findAllTags(dbTypeText);
        findTagByName(dbTypeText);
        createTag(dbTypeText);
        removeTag(dbTypeText);
    }


    @Test
    public void runTest() throws RepositoryException, OlogException, IOException {
        Runtime.getRuntime().exec("mysql -u olog -p olog < /home/eschuhmacher/ologBig.sql");
        runTests("Psql db- ");
    }


    public void findLogByAttribute(String dbTypeText) throws RepositoryException, OlogException, IOException {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("sweep.crystal_name", "ECF_229");
        long startTime = System.nanoTime();
        Logs logs = LogManager.findLog(map);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        logId = logs.getLogList().size() > 0 ? logs.get(0).getEntryId(): 1l;
        out.write(dbTypeText + " Time consume to find a log by attribute is: " + totalTime + "(s)");
        out.newLine();
    }

    public void findLogByDescription(String dbTypeText) throws RepositoryException, OlogException, IOException {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("search", "Energy");
        long startTime = System.nanoTime();
        Logs logs = LogManager.findLog(map);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a log by description is: " + totalTime + "(s)");
        out.newLine();
    }


    public void insertLog(String dbTypeText) throws RepositoryException, OlogException, IOException {
        Log logOld = LogManager.findLog(logId);
        logOld.setOwner("Log");
        logOld.setEntryId(null);
        long startTime = System.nanoTime();
        Log log = LogManager.create(logOld);
        logId = log.getEntryId();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to insert a log  is: " + totalTime + "(s)");
        out.newLine();
    }

    public void insertLog2(String dbTypeText) throws RepositoryException, OlogException, IOException {
        Log logOld = LogManager.findLog(logId);
        logOld.setOwner("Log2");
        logOld.setEntryId(null);
        long startTime = System.nanoTime();
        Log log = LogManager.create(logOld);
        logId = log.getEntryId();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to insert a log2  is: " + totalTime + "(s)" );
        out.newLine();

    }

    public void findLogByDate(String dbTypeText) throws RepositoryException, OlogException, IOException {
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.add("start", String.valueOf(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() / 1000));
        long startTime = System.nanoTime();
        Logs logs = LogManager.findLog(map);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a log by date is: " + totalTime + "(s)");
        out.newLine();
    }

    public void findLogById(String dbTypeText) throws OlogException, IOException {
        long startTime = System.nanoTime();
        Log log = LogManager.findLog(logId);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a log by id is: " + totalTime + "(s)");
        out.newLine();
    }

    public void removeLog(String dbTypeText) throws RepositoryException, OlogException, IOException {
        long startTime = System.nanoTime();
        LogManager.remove(logId);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to delete a log  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findAllProperties(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Set<Property> properties = PropertyManager.findAll();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find all properties  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findPropertyByName(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Property property = PropertyManager.findProperty("johnprop");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a property by name  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void createProperty(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Property property = PropertyManager.create("Prop");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to create a property  is: " + totalTime + "(s)");
        out.newLine();

    }


    public void removeProperty(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        PropertyManager.remove("Prop");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to remove a property  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findAllLogbooks(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Logbooks logbooks = LogbookManager.findAll();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find all logbooks  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findLogbookByName(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Logbook logbook = LogbookManager.findLogbook("johnLog");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a logbook by name  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void createLogbook(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Logbook logbook = LogbookManager.create("Log", "User");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to create a logbook  is: " + totalTime + "(s)");
        out.newLine();

    }


    public void removeLogbook(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        LogbookManager.remove("Log");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to remove a logbook  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findAllTags(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Tags tags = TagManager.findAll();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find all tags  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findTagByName(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Tag tag = TagManager.findTag("Tag");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a tag by name  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void createTag(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Tag Tag = TagManager.create("Tag2");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to create a tag  is: " + totalTime + "(s)");
        out.newLine();

    }


    public void removeTag(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        TagManager.remove("Tag2");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to remove a tag  is: " + totalTime + "(s)");
        out.newLine();

    }
}


package edu.msu.nscl.olog;


import edu.msu.nscl.olog.entity.BitemporalLog;
import edu.msu.nscl.olog.entity.Logbooks;
import edu.msu.nscl.olog.entity.Property;
import edu.msu.nscl.olog.entity.Tag;
import edu.msu.nscl.olog.entity.Tags;
import edu.msu.nscl.olog.entity.Logbook;
import edu.msu.nscl.olog.entity.XmlLogs;
import edu.msu.nscl.olog.entity.LogAttribute;
import edu.msu.nscl.olog.entity.Log;
import org.apache.commons.lang.time.DateUtils;
import org.apache.jackrabbit.JcrConstants;
import org.junit.*;

import javax.jcr.*;

import javax.ws.rs.core.MultivaluedMap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.apache.cxf.jaxrs.impl.MetadataMap;

public class OlogPerformanceTest {

    private static BufferedWriter out;
    private static long logId;

    @BeforeClass
    public static void prepareTest() throws IOException {
        System.setProperty("persistenceUnit", "olog_test");
        String filePath = OlogPerformanceTest.class.getResource("OlogPerformanceTest.class").getPath();
        File outputFile = new File(filePath.substring(0,filePath.indexOf("target")) + "src/test/java/edu/msu/nscl/olog/OlogPerformanceTestResult.txt");
        out = new BufferedWriter(new FileWriter(outputFile));
    }

    @AfterClass
    public static void close() throws IOException {
        out.close();
    }


    private void runTests(final String dbTypeText) throws IOException, OlogException, RepositoryException {
        findLogByAttribute(dbTypeText);
        insertLog(dbTypeText);
        insertLog2(dbTypeText);
        insertLog2(dbTypeText);
        findLogByDate(dbTypeText);
        findLogByAttribute(dbTypeText);
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


    @Ignore
    @Test
    public void runTest() throws RepositoryException, OlogException, IOException {
        runTests("psql db - ");
    }

    public void findLogByAttribute(String dbTypeText) throws RepositoryException, OlogException, IOException {
        MultivaluedMap<String, String> map = new MetadataMap();
        map.add("sweep.crystal_name", "ECF_229");
        long startTime = System.nanoTime();
        List<BitemporalLog> logs = LogManagerTest.findLog(map);
        long endTime = System.nanoTime();
        logId = logs.size() > 0 ? logs.get(0).getLog().getId(): 1l;
         double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a log by attribute is: " + totalTime + "(s)");
        out.newLine();
    }

    public void insertLog(final String dbTypeText) throws RepositoryException, OlogException, IOException {
        Log logOld = LogManagerTest.findLog(logId);
        logOld.setOwner("testLog");
        logOld.setId(null);
        logOld.setState(null);
        logOld.setState(null);
        logOld.setEntry(null);
        logOld.getLogbooks().iterator().next().setLogs(new HashSet<Log>());
        logOld.getLogbooks().iterator().next().setOwner("testOwner");
        logOld.getLogbooks().iterator().next().setName("testLog");

        logOld.getLogbooks().iterator().next().setId(null);
        logOld.setAttributes(new HashSet<LogAttribute>());
        long startTime = System.nanoTime();
        Log log = LogManagerTest.create(logOld);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to insert a log  is: " + totalTime + "(s)");
        out.newLine();
    }

    public void insertLog2(String dbTypeText) throws RepositoryException, OlogException, IOException {
        Log logOld = LogManagerTest.findLog(logId);
        logOld.setOwner("testLog2");
        logOld.getLogbooks().iterator().next().setLogs(null);
        long startTime = System.nanoTime();
        Log log = LogManagerTest.create(logOld);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to insert a log2  is: " + totalTime + "(s)" );
        out.newLine();

    }

    public void findLogByDate(String dbTypeText) throws RepositoryException, OlogException, IOException {
        MultivaluedMap<String, String> map = new MetadataMap();
        map.add("start", String.valueOf(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime() / 1000));
        long startTime = System.nanoTime();
        List<BitemporalLog> logs = LogManagerTest.findLog(map);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a log by date is: " + totalTime + "(s)");
        out.newLine();
    }

    public void findLogById(String dbTypeText) throws OlogException, IOException {
        long startTime = System.nanoTime();
        Log log = LogManagerTest.findLog(logId);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a log by id is: " + totalTime + "(s)");
        out.newLine();
    }

    public void removeLog(String dbTypeText) throws RepositoryException, OlogException, IOException {
        long startTime = System.nanoTime();
        LogManagerTest.remove(logId);
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to delete a log  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findAllProperties(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Set<Property> properties = PropertyManagerTest.findAll();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find all properties  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findPropertyByName(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Property property = PropertyManagerTest.findProperty("johnprop");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a property by name  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void createProperty(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Property property = PropertyManagerTest.create("testProp");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to create a property  is: " + totalTime + "(s)");
        out.newLine();

    }


    public void removeProperty(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        PropertyManagerTest.remove("testProp");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to remove a property  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findAllLogbooks(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Logbooks logbooks = LogbookManagerTest.findAll();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find all logbooks  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findLogbookByName(final String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Logbook logbook = LogbookManagerTest.findLogbook("johnLog");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a logbook by name  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void createLogbook(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Logbook logbook = LogbookManagerTest.create("testLog", "testUser");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to create a logbook  is: " + totalTime + "(s)");
        out.newLine();

    }


    public void removeLogbook(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        LogbookManagerTest.remove("testLog");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to remove a logbook  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findAllTags(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Tags tags = TagManagerTest.findAll();
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find all tags  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void findTagByName(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Tag tag = TagManagerTest.findTag("testTag");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to find a tag by name  is: " + totalTime + "(s)");
        out.newLine();

    }

    public void createTag(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        Tag Tag = TagManagerTest.create("testTag2");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to create a tag  is: " + totalTime + "(s)");
        out.newLine();

    }


    public void removeTag(String dbTypeText) throws IOException, OlogException {
        long startTime = System.nanoTime();
        TagManagerTest.remove("testTag2");
        long endTime = System.nanoTime();
        double totalTime =(endTime - startTime) / 1000000000.0;
        out.write(dbTypeText + " Time consume to remove a tag  is: " + totalTime + "(s)");
        out.newLine();

    }
}


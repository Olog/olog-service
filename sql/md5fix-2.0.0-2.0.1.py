#! /usr/bin/env jython -Dpython.path=md5lib/mysql-connector-java-5.1.18-bin.jar;md5lib/OlogAPI_2-1-0.jar

# jython script for updating md5, must have the olog api (for error checking) and mysql connector jar in path
import sys
from java.io import File

def addToClasspath(dirPath):
     rootDir = File(dirPath)
     if rootDir.isFile():
         sys.path.append(dirPath)
         return
     if rootDir.isDirectory():
         sys.path.append(dirPath)
         # recursively add the subdirectories and the jar files
         for i in rootDir.listFiles():
             if i.isDirectory():
                 addToClasspath(i.getPath())
             if i.isFile():
                 if i.getName().endswith(".jar"):
                     sys.path.append(i.getAbsolutePath())

addToClasspath('md5lib')
from java.lang import *
from java.sql import *
from java.math import BigInteger
from java.security import MessageDigest
from java.util import Hashtable
from edu.msu.nscl.olog.api import *
import com.mysql.jdbc.Driver

from time import strftime, strptime
import getpass

server = raw_input("Enter database server: ")
database = raw_input("Enter database: ")
user = raw_input("Enter database user: ")
p = getpass.getpass()

def getmd5Recent(logId):
    try:
        md5Recent = ''
        url = 'jdbc:mysql://'+server+'/'+database+'?user='+user+'&password='+p
        con = DriverManager.getConnection(url)
        stmt = con.createStatement()
        sql = 'SELECT id as entry_no, md5entry FROM logs WHERE id < %s ORDER BY id DESC LIMIT 10' % logId
        rs = stmt.executeQuery(sql)
        while rs.next() :
            md5Recent += rs.getString('entry_no') + ' ' + rs.getString('md5entry') + '\n'
        rs.close()
        stmt.close()
        con.close()
        return md5Recent;
    except Exception:
        print 'Exception: '

        
def getmd5Entry(log):
    explodeRecent = ''
    explodeRecentArray = getmd5Recent(log.getString('id')).split('\n')

    for line in explodeRecentArray : 
        if (line == None or line == '\n' or line == ''):
            continue
        explodeRecent += 'md5_recent:' + line + '\n'

    entry = 'id:' + unicode(log.getString('id'),'utf-8') + '\n' \
                  + 'level:' + unicode(log.getString('level_name'),'utf-8') + '\n'
    if  log.getString('description') == None:
        entry += 'description:null'  + '\n'
    else:
        entry += 'description:' + unicode(log.getString('description'),'utf-8') + '\n'
    
    entry += 'created:' + mktime(strptime(log.getString('createdDate'),'%Y-%m-%d %H:%M:%S')) + '\n'
    
    if log.getString('modifiedDate') == None:
        entry += 'modified:null' + '\n' 
    else:
        entry += 'modified:' + mktime(strptime(log.getString('modifiedDate'),'%Y-%m-%d %H:%M:%S')) + '\n'
        
    entry += 'source:' + unicode(log.getString('source'),'utf-8') + '\n' \
                + 'owner:' + unicode(log.getString('owner'),'utf-8') + '\n' \
                + explodeRecent

    bytesOfMessage = String(entry).getBytes('UTF-8')
    md = MessageDigest.getInstance('MD5')
    md5Entry = md.digest(bytesOfMessage)
    md5Number = BigInteger(1, md5Entry)
    md5EntryString = str(md5Number.toString(16))

    return md5EntryString

client = OlogClientImpl.OlogClientBuilder.serviceURL().create()

map = Hashtable()
map.put("search", "*")
logs = client.findLogs(map)
beforeCount = logs.size()

driverName='com.mysql.jdbc.Driver'
Class.forName(driverName)

url = 'jdbc:mysql://'+server+'/'+database+'?user='+user+'&password='+p
con = DriverManager.getConnection(url)
stmt = con.createStatement()
sql = 'SELECT DISTINCT log.*, level.name as level_name, ' \
    + '(SELECT logs.created FROM logs WHERE log.parent_id=logs.id) as parent_created, ' \
    + 'ifnull((SELECT logs.created FROM logs WHERE log.parent_id=logs.id), log.created) as createdDate, ' \
    + 'if((SELECT logs.created FROM logs WHERE log.parent_id=logs.id), log.created, null) as modifiedDate, ' \
    + 'ifnull(log.parent_id,log.id) as entry_no, ' \
    + '(SELECT COUNT(id) FROM logs WHERE parent_id=log.parent_id GROUP BY parent_id) as children FROM `logs` as log ' \
    + 'LEFT JOIN `logs` as parent ON log.id = parent.parent_id ' \
    + 'LEFT JOIN levels as level ON log.level_id = level.id '

rs = stmt.executeQuery(sql)
con2 = DriverManager.getConnection(url)
    
while (rs.next()):

    stmt2 = con2.createStatement()
    recent = str(getmd5Recent(rs.getString('id')))
    entry = str(getmd5Entry(rs))
    sql2 = "UPDATE logs SET md5entry='%(entry)s',md5recent='%(recent)s' WHERE id='%(id)s'"%{"entry":entry,"recent":recent,"id":rs.getString('id')}
    rs2 = stmt2.executeUpdate(sql2)
    stmt2.close()
    print 'updated: '+rs.getString('id')

con2.close()
rs.close()
stmt.close()
con.close()

logs = client.findLogs(map)
afterCount = logs.size()

print 'before count: '+str(beforeCount)+' | after count: '+str(afterCount)
print 'Done'
    
quit()

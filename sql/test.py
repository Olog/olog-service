

from java.lang import *
from java.sql import *
from java.math import BigInteger
from java.security import MessageDigest


file=open("md5.txt")
entry=''

for line in file:
	entry += unicode(line,'utf-8') 

bytesOfMessage = String(entry).getBytes('UTF-8')
md = MessageDigest.getInstance('MD5')
md5Entry = md.digest(bytesOfMessage)
md5Number = BigInteger(1, md5Entry)
md5EntryString = str(md5Number.toString(16))

print md5EntryString

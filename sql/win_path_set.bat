@echo off
setLocal EnableDelayedExpansion

set CLASSPATH="
for %%a in (md5lib/*.jar) do (
 set CLASSPATH=!CLASSPATH!;md5lib/%%a
)
set CLASSPATH=!CLASSPATH!"
echo !CLASSPATH!
set LANG=en_US

echo "Usage -- run as .\script\convert.bat"
echo JAVA_HOME =  %JAVA_HOME%
echo See README_convert.txt  for more detail.
@echo off

REM Find current path to install
set scriptdir=%~dp0
set scriptdir=%scriptdir:~0,-1%
set basedir=%scriptdir%\..
set logconf=%scriptdir:\=/%
set logfile=%basedir%\logs\xtext-stderr.log
logging_opts="-Dlogback.configurationFile=%basedir%\etc\logback.xml  -Dtika.config=%basedir%\etc\tika-config.xml"

java %logging_opts% -Dxtext.home="%basedir%"  -Xmx512m  -classpath "%basedir%\lib\*;%basedir%\etc" org.opensextant.xtext.XText  --input=%1 --output=%2 --export=%3 
REM >%logfile% 2>&1 

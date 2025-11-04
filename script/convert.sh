export LANG=en_US

script=`dirname $0;`
basedir=`cd -P $script/..; echo $PWD`

echo JAVA_HOME =  $JAVA_HOME
echo See README_convert.txt  for more detail.
echo $*
logging_opts="-Dlogback.configurationFile=$basedir/etc/logback.xml -Djava.util.logging.config.file=$basedir/etc/logging.properties"
tika_opts=" -Dtika.config=file://${basedir}/etc/tika-config.xml "
# ALT: TIKA_CONFIG=file://${basedir}/etc/tika-config.xml
java  $logging_opts $tika_opts -Dxtext.home="${basedir}" -Xmx512m  -classpath "$basedir/etc:$basedir/lib/*:$basedir/lib/logging/*" ExtractText "$@"



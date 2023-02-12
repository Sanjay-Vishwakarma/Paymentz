TOMCAT_HOME=/usr/local/tomcat
PHP_HOME=/var/www/html
echo "Tomcat Home :  " $TOMCAT_HOME
echo "PHP Home :  " $PHP_HOME

cp -f $PHP_HOME/transecuteicici/icicicredit/payprocess_maintenance.php3 $PHP_HOME/transecuteicici/icicicredit/payprocess.php3
cp -f $TOMCAT_HOME/webapps/merchant/index_maintenance.jsp $TOMCAT_HOME/webapps/merchant/index.jsp
cp -f $TOMCAT_HOME/webapps/icici/WEB-INF/web_maintenance.xml $TOMCAT_HOME/webapps/icici/WEB-INF/web.xml
cp -f $TOMCAT_HOME/webapps/merchant/WEB-INF/web_maintenance.xml $TOMCAT_HOME/webapps/merchant/WEB-INF/web.xml
cp -f $TOMCAT_HOME/webapps/newfinance/WEB-INF/web_maintenance.xml $TOMCAT_HOME/webapps/newfinance/WEB-INF/web.xml
cp -f $TOMCAT_HOME/webapps/order/WEB-INF/web_maintenance.xml $TOMCAT_HOME/webapps/order/WEB-INF/web.xml
cp -f $TOMCAT_HOME/webapps/support/WEB-INF/web_maintenance.xml $TOMCAT_HOME/webapps/support/WEB-INF/web.xml

echo " Putting Site on maintenance.."
echo " Restarting Tomcat........"
$TOMCAT_HOME/bin/restart.sh

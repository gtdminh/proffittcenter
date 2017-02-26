
 
 

<?php

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

 if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 





mysql_query("INSERT INTO salelines 
(Quantity, Product, Price, Sale, Descrip, charge) VALUES('$qty', '$id', '$price', '$SALESIDck', '$des', '$charg' ) ") 
or die(mysql_error());  

?>

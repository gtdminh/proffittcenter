
 
 

<?php
$saletax=$_POST['saletax'];

$saletotal=$_POST['saletotal'];

$saledebit=$_POST['saledebit'];

$salecoupon=$_POST['salecoupon'];

$salecash=$_POST['salecash'];

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

 if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 

$finissale=mysql_query(" UPDATE sales  SET
Tax='$saletax' ,
Total='$saletotal' ,
Debit='$saledebit' ,
Coupon='$salecoupon' ,
Cash='$salecash' WHERE ID='$SALESIDck'");



mysql_close($con);



?>


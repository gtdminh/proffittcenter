<?
include "../setting/dbCONECT/mysql_connection.php"; 
//----------------------------------------
if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 
$total = "SELECT SUM(charge) FROM salelines WHERE Sale='$SALESIDck'"; 
$totalresult = mysql_query($total) or die(mysql_error());
while($rowtotal = mysql_fetch_array($totalresult)){
	$total1=$rowtotal['SUM(charge)'];
$total2=$FrontPrice.sprintf("%01.2f", $total1/100);
}
?>
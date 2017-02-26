<?

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

//----------------------------------------

$quantity = "SELECT SUM(Quantity) FROM salelines WHERE Sale='$SALESIDck'"; 
	 
$quantityresult = mysql_query($quantity) or die(mysql_error());


while($rowquantity = mysql_fetch_array($quantityresult)){
	$quantity1=$rowquantity['SUM(Quantity)'];

if(isset($_COOKIE['Operator']))
	$Operator = $_COOKIE['Operator']; 
if(isset($_COOKIE['Customer']))
	$Customer = $_COOKIE['Customer']; 

if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 

if(isset($_COOKIE['Tillid1']))
	$Tillid1 = $_COOKIE['Tillid1']; 
if ($Tillid1) {

echo "  TILL:". $Tillid1  ;
echo "  OPERATOR: ". $Operator  ;
echo "  SALESID: ". $SALESIDck  ;
echo "  CUSTOMER: ". $Customer  ;
echo " <u>QTY:$quantity1"  ;
}
else {



}
	
}
?>
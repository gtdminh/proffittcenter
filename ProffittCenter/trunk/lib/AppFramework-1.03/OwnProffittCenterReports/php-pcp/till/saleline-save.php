<?php
$id=$_POST['id'];
$qtyb=$_POST['qty'];
include "../setting/dbCONECT/mysql_connection.php"; 
if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 

$querysalelines=" SELECT * FROM salelines WHERE Product='$id' AND Sale='$SALESIDck'";
$resultsalelines=mysql_query($querysalelines);
$numsalelines=mysql_num_rows($resultsalelines);

if ($numsalelines) {
$qtya=mysql_result($resultsalelines,$i,"Quantity");
$qty=($qtya+$qtyb);
$Pricec=mysql_result($resultsalelines,$i,"Price");
$charg=($Pricec*$qty);
mysql_query(" UPDATE salelines  SET Quantity='$qty' , charge='$charg' WHERE Product='$id' AND Sale='$SALESIDck'");

}
else {



$insert=mysql_query("INSERT INTO salelines 
(Quantity, Product, Price, Sale, Descrip, charge) VALUES('$qtyb', '$id', '$price', '$SALESIDck', '$des', '$charg' ) ") 
or die(mysql_error());  
//mysql_close($con);

}

?>


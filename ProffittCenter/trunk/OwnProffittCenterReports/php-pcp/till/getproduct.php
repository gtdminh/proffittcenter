<?php
include "../setting/dbCONECT/mysql_connection.php"; 
if(isset($_POST['id']))
{
    // Handle POST-data.
    header("location:" . $_SERVER['PHP_SELF']);
$qty=$_POST['qty'];
$id=$_POST['id'];
if ($id!=0)
{
//------------offer---------------------------------------------------------------
$offerresult = mysql_query("SELECT * FROM offers Products WHERE Product='$id'AND ID='$id' ")
or die(mysql_error());  
$offerrow = mysql_fetch_array( $offerresult );
if ($offerrow) {
$price1=$row['Price'];
$des=$row['Description'];
$barcod=$row['ID'];
if($price!=0)
{
$price=$row['Price'];
$charg=$qty*$price;
$offerqty=$row['X'];
$price=$row['Y'];
if ($offerqty=$qty) {
$price=$row['Y'];
require("saleline-save.php");
}
}
}
else {
//--------------normal----------------------------------------------------------------
$result = mysql_query("SELECT * FROM Products WHERE ID='$id' ")
or die(mysql_error());  
$row = mysql_fetch_array( $result );
if ($row) {
$price1=$row['Price'];
$des=$row['Description'];
$barcod=$row['ID'];
if($price!=0)
{ 
$price=$row['Price'];
$charg=$qty*$price;
}
else {
$price=$row['Price'];
$charg=$qty*$price;
require("saleline-save.php");
}
}
}
}
}
?>
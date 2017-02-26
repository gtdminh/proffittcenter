








<?php

 




include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";


if(isset($_POST['id']))
{
    // Handle POST-data.
    header("location:" . $_SERVER['PHP_SELF']);

$id=$_POST['id'];
if ($id!=0)
{




$result = mysql_query("SELECT * FROM Products WHERE ID='$id' ")
or die(mysql_error());  


$row = mysql_fetch_array( $result );
if ($row) {

 

$price1=$row['Price'];
$des=$row['Description'];
$barcod=$row['ID'];
$qty=$_POST['qty'];


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
?>


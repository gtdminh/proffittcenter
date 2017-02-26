<html><head><title>Change Record form</title>
<style type="text/css">
td {font-family: tahoma, arial, verdana; font-size: 10pt }
</style>


</head>
<body>
<?php

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";




$id=$_POST['id'];
$offtex=$_POST['offtex'];
$offtex2=$_POST['offtex2'];

//OFFER LABEL __________________________________________

$query=" SELECT * FROM Products , Offers WHERE Products.id='$id' AND Offers.Product='$id'";
$result=mysql_query($query);
$num=mysql_num_rows($result);
if ($num) {

$ID=mysql_result($result,$i,"ID");
$Description=mysql_result($result,$i,"Description");
$Price1=mysql_result($result,$i,"Price");
$Price=sprintf("%01.2f", $Price1/100); 
$x=mysql_result($result,$i,"X");
$ix=mysql_result($result,$i,"Ix");
$y1=mysql_result($result,$i,"Y");
$y=sprintf("%01.2f", $y1/100);
$enddate=mysql_result($result,$i,"EndDate");
$startdate=mysql_result($result,$i,"StartDate");

$x2=$x==1; 
if ($x2) {
$x1=$offqty1;
$offqtysize1=$offqtytexsize;

}
else {

$x1=$x;
$offqtysize1=$offqtysize;

}
?>
 
 


<?


// NORMAL LABEL______________________________________________________


}
else {

$query1=" SELECT * FROM Products WHERE Products.id='$id'";
$result1=mysql_query($query1);
$num1=mysql_num_rows($result1);
if ($num1) {



$ID=mysql_result($result1,$i,"ID");
$Description=mysql_result($result1,$i,"Description");
$Price1=mysql_result($result1,$i,"Price");

$Price=sprintf("%01.2f", $Price1/100); 



?>

 

<?
   


  
  exit;     




mysql_close($con);
 

}
else {


?>
no product find




<?

}



?>

<?php ++$i;
}
?>

</body>
</html>







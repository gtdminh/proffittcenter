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



$query=" SELECT * FROM Products , offers WHERE Products.id=$id AND Offers.Product=Products.id";
$result=mysql_query($query);
$num=mysql_num_rows($result);



if ($num) {

$ix1=mysql_result($result,$i,"Ix");


?>

<form method="POST" action="offer-change-form.php">
<?

$resultselect = @mysql_query("select* FROM Products , offers WHERE Products.id=Offers.Product AND Offers.Ix=$ix1");

print "test gowry:\n";
print "<select name=\"id\">\n";

while ($rowsele =mysql_fetch_assoc($resultselect)){
$seliy = $rowsele['Y'];
$seldes = $rowsele['Description'];
$seltyp = $rowsele['OfferType'];
$selbarcod = $rowsele['Product'];

print "<option value=$selbarcod>$seldes , $selbarcod , $seltyp\n";


}
print "</select>\n";
print "</p>\n";

?>
<input type="submit" value="EDIT SELLECT OFFER"><br></form>


<?
}
else {
$querypro=" SELECT * FROM Products WHERE ID=$id ";
$resultpro=mysql_query($querypro);
$IDpro=mysql_result($resultpro,$i,"ID");
$Descriptionpro=mysql_result($resultpro,$i,"Description");
$Pricepro=mysql_result($resultpro,$i,"Price");

echo "$IDpro <BR> $Descriptionpro <BR>   $Pricepro "

?>
<form action="offer-new.php" method="post">
 barcod: <input type="text" name="barcod" value="<? echo "$IDpro"?>"/>
<BR>
 OFFER TYPE (1=pack, 2=QTY): <input type="text" name="type" />
<BR>
 OTY: <input type="text" name="xqty" />
<BR>
 OFFER PRICE: <input type="text" name="yprice" />
<BR>
 START DATE: <input type="text" name="startdate" />
<BR>
 END DATE: <input type="text" name="enddate" />
<BR>
 <input type="submit" value="Update" />
 </form>
 




<?
 
  exit;     
Echo "<a href=offer-barcod.php>CHECK ANATHER PRODUCT</a>" ;
mysql_close($con);
} 



$i=0;
while ($i < $num) {
$ID=mysql_result($result,$i,"ID");
$Description=mysql_result($result,$i,"Description");
$Price=mysql_result($result,$i,"Price");
$ix=mysql_result($result,$i,"Ix");
$x=mysql_result($result,$i,"X");
$y=mysql_result($result,$i,"Y");
$ix=mysql_result($result,$i,"Ix");
$ix=mysql_result($result,$i,"Ix");
$ix=mysql_result($result,$i,"Ix");

//------------------------------------------



?>

<table width="400" cellpadding="25" cellspacing="0" border="3">
<tr align="center" valign="top">
<td align="center" colspan="3" rowspan="2" bgcolor="#64b1ff">
<h3>Edit and Submit</h3>
<form action="offer-change.php" method="post">
<input type="hidden" name="udid" value="<? echo "$id" ?>">
working on it
<br>
<? echo "$Description2"?>



BARCOD:<? echo "$ID"?>   
 <br>
Description: <? echo "$Description"?>
<br>
RR PRICE:<? echo "$Price"?>
<br>
<br>
OFFER ID:    <input type="text" name="udix" value="<? echo "$ix"?>">
<br>
OFFER QTYE:    <input type="text" name="udx" value="<? echo "$x"?>">
<br>
OFFER PRICE:    <input type="text" name="udy" value="<? echo "$y"?>">

<br>



<input type="Submit" value="Update">
</form>
</td></tr></table>

<?php
++$i;
}
?>

</body>
</html>







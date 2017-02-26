


<html><head><title>Change qty Record form</title>
<style type="text/css">
td {font-family: tahoma, arial, verdana; font-size: 10pt }
.style1 {font-size: 14pt}
</style>


</head>
<body>
<?php

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";


$id=$_POST['id'];



$query=" SELECT * FROM Products , skus WHERE Products.id='$id' AND skus.ID=Sku";
$result=mysql_query($query);
$num=mysql_num_rows($result);
if ($num) {
print " ";

}
else {
print "PRODUCTS NOT Found";
echo "<p>";
echo "$id ";
echo "<p>";
print  "add the PRODUCTS ON THE";
echo "<p> ";
echo " MAIN OR BACKOFFIC TILL ";
echo "<p> ";
Echo "<a href=qt-barcod.php>CHECK ANATHER PRODUCT</a>" ;
mysql_close($con);
} 



$i=0;
while ($i < $num) {
$ID=mysql_result($result,$i,"Products.ID");
$Description=mysql_result($result,$i,"Description");
$Price=mysql_result($result,$i,"Price");
$Price1=$FrontPrice.sprintf("%01.2f", $Price/100); 
$sku=mysql_result($result,$i,"Sku");
$qtyc=mysql_result($result,$i,"Quantity");



?>
<table width="400" cellpadding="25" cellspacing="0" border="3">
<tr align="center" valign="top">
<td align="center" colspan="3" rowspan="2" bgcolor="#64b1ff">
<h3>CHANG THE QTY</h3>
<form action="qt-change.php" method="post">
  <p>
    BARCOD: <? echo "$ID"?>   
    <input name="ud_ID" type="hidden" value="<? echo "$ID"?>">  
    <br>
    sku:<? echo "$sku"?>    
  <input name="ud_sku" type="hidden" value="<? echo "$sku"?>" size="0" maxlength="0">
  <br>
    Description: <? echo "$Description"?>   
    <input name="ud_Description" type="hidden" value="<? echo "$Description"?>">  
   
<br>
    RR PRICE:<b> <? echo "$Price1"?>   
  <input name="ud_Price" type="hidden" value="<? echo "$Price"?>">
  <br>

<br>
    <font color="#0000FF" size="3"> <B>
    QTY :</font><b> <font color="#ff6000" size="5px"> <B> <? echo "$qtyc"?> +  
  
      <input type="text" name="ud_qty1" value=" ">
       <input type="text" name="ud_qty" value=" <? echo "$qtyc"?>">
        <br>
        <input type="Submit" value="Update">
    </p>
  </form></b></b></td></tr></table>

<?php
++$i;
}
?>

</body>
</html>







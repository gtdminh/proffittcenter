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




$id=$_GET['id'];





$query=" SELECT * FROM Products WHERE id='$id'";
$result=mysql_query($query);
$num=mysql_num_rows($result);
if ($num) {
echo '<META HTTP-EQUIV="Refresh" Content="7; URL=label-selfform.php?id=&id">';

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
Echo "<a href=offer-label-selfform.php>PRINT ANATHER PRODUCT</a>" ;
echo '<META HTTP-EQUIV="Refresh" Content="7; URL=label-selfform.php?id=&id">';     
  exit;     
mysql_close($con);
} 



$i=0;
while ($i < $num) {
$ID=mysql_result($result,$i,"ID");
$Description=mysql_result($result,$i,"Description");
$Price1=mysql_result($result,$i,"Price");

$Price=sprintf("%01.2f", $Price1/100); 




?>
 <div align="left">
  <table border="1" width="220" height="120">
    <tr>
      <td width="220" height="24" colspan="2">
<p align="center"><font size="3">
<? echo "$Description"?></font></p>      </td>
    </tr>
    <tr>
      <td width="220" height="30">
<p align="center"><b><font color="#000000" size="<? echo "$pricesize"?>" face="Arial">
<? echo "$FrontPrice"?>

<? echo 
 "$Price"?></font><? echo "$EndPrice"?></b></p>      </td>
    </tr>
    <tr>
      <td width="220" height="7"><div align="center"><font face="<? echo "$barcod"?>" size="<? echo "$barsize"?>"><? echo "$ID"?></font><font size="1"> <? echo "$ShopName"?></font> </div></td>
    </tr>
  </table>
</div>
 

<?php ++$i;
}
?>

</body>
</html>







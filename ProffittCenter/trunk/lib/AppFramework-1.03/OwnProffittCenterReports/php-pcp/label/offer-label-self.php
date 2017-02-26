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



$query=" SELECT * FROM Products , Offers WHERE Products.id='$id' AND Offers.Product='$id'";
$result=mysql_query($query);
$num=mysql_num_rows($result);
if ($num) {

echo '<META HTTP-EQUIV="Refresh" Content="20; URL=label-selfform.php?id=&id">';
print " ";

}
else {




   
echo "<p> ";
echo "<p> ";
print "<b>OFFER  NOT Found";
echo "<p> ";
echo "<p>FOR Product $id <BR> $Description ";
echo "<a href=label-self.php?id=$id><b>PRINT NORMAL Product Details</a>"; 

echo "<p>";
echo "<p> ";
Echo "<a href=label-selfform.php><b>PRINT ANATHER offer PRODUCT</a>" ;
  
  exit;     



echo " <p>";

mysql_close($con);
} 




$i=0;
while ($i < $num) {
$ID=mysql_result($result,$i,"ID");
$Description=mysql_result($result,$i,"Description");
$Price1=mysql_result($result,$i,"Price");
$Price=sprintf("%01.2f", $Price1/100); 
$x=mysql_result($result,$i,"X");
$ix=mysql_result($result,$i,"Ix");
$y1=mysql_result($result,$i,"Y");
$y=sprintf("%01.2f", $y1/100); 

?>
 
 
<div align="left">
  <table border="1" width="220" height="40">
    <tr>
      <td width="220" height="20" colspan="2">
      <p align="center"><font face="Arial" size="4" color="#0000FF">

<? echo "$Description"?>
</font>

      
      </td>
    </tr>
    <tr>
      <td width="150" height="40">
      
      <p align="center"><font face="Arial" size="<? echo "$pricesize"?>" color="#0000FF">
<b>
<? echo "$FrontPrice"?>
<? echo "$Price"?>
</font>
<font face="Arial" size="2" color="#0000FF">

<? echo "$EndPrice"?>
</b>
</font>

      
      
      
      </td>
      <td width="110" height="60" rowspan="2" bgcolor="#ffffff">
      <p align="center">

<font face="Arial" size="1" color="#0000FF">
<b>
<? echo "$offtex2"?></font>
<font face="Arial" size="<? echo "$offqtysize"?>" color="#0000FF">
<b>
<? echo "$x"?>
</b>
</font>




<font face="Arial" size="2" color="#0000FF">

<? echo "$offtex"?>
<BR>
<font face="Arial" size="<? echo "$offpricesize"?>" color="#0000FF">
<b>
<? echo "$FrontPrice"?>
<? echo "$y"?>
</font>
<font face="Arial" size="2" color="#0000FF">

<? echo "$EndPrice"?>

</font>


      
      </font>
      
      </td>
    </tr>
    <tr>
      <td width="150" height="10">
      <p align="center"><font face="<? echo "$barcod"?>" color="#0000FF" size="<? echo "$barsize"?>">
<? echo "$ID"?>
</font>
<font face="Arial" size="1" color="#0000FF">
  OID:<? echo "$ix"?>       
<BR>

<align="center"><font face="Arial" color="#0000FF" size="1">
<? echo "$ShopName"?>
</font>

  
      
      
      </td>
    </tr>
  </table>
</div>


<?php ++$i;
}
?>

</body>
</html>







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
print " ";

}
else {
print "offer PRODUCTS NOT Found";
echo "<p>";
echo "$id ";
echo "<p>";
print  "add the PRODUCTS ON THE";
echo "<p> ";
echo " MAIN OR BACKOFFIC TILL ";
echo "<p> ";
echo '<META HTTP-EQUIV="Refresh" Content="3; URL=pro-barcod.php">';     
  exit;     
Echo "<a href=pro-barcod.php>CHECK ANATHER PRODUCT</a>" ;
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

$query2=" SELECT * FROM Products , offers WHERE Products.id=Offers.Product AND Offers.Ix=$ix";
$result2=mysql_query($query2);
$num2=mysql_num_rows($result2);


//------------


<form method="post" action="selectedValue.jsp">
Select Programming Language:
<select name="lang">
<option value="C/C++"><? echo "$ix2"?></option>
<option value="C#">C#</option><option value="Java">Java</option>
<option value="Perl">Perl</option>
<option value="Python">Python</option>
</select><br>
<input type="submit" value="Submit"><br></form>







//--------------------





echo "<table border='1'>
<tr>
<th>BARCOD  </th>
<th>OFFER<BR> QTY   </th>
<th>OFFER<BR> PRICE   </th>
<th>OFFER<BR> TYPE </th>
<th>description</th>
<th>price</th>
<th>END<BR>DATE</th>
</tr>";
while($row = mysql_fetch_array($result2))
{

$x2=$row['X'];
$y2=$row['Y'];
$ix2=$row['IX'];
$des2=$row['Description'];
$pric2=$row['Price'];
$enddat2=$row['EndDate'];
$barcod2=$row['Product'];

$yprice2=$FrontPrice.sprintf("%01.2f", $y2/100); 
$nprice2=$FrontPrice.sprintf("%01.2f", $pric2/100);

echo "<tr>";
echo "<td><B>" . $barcod2 . "</td>";
echo "<td><B>" . $x2 . "</td>";
echo "<td><B>" . $yprice2 . "</td>";
echo "<td><B>" . $ix2 . "</td>";
echo "<td><B>" . $des2 . "</td>";
echo "<td><B>" . $nprice2 . "</td>";
echo "<td><B>" . $enddat2 . "</td>";
echo "</tr>";
}
echo "
 </form> </table>";


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







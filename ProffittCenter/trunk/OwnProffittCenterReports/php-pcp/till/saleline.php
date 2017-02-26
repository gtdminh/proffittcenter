<style>
body {
	background-color: #8f8f8f;
}

</style><meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></head>

<body text="#000000" link="#0000FF" vlink="#0000FF" alink="#0000FF">
  <table border="1" width="400" height="424">
    
 


<?php

include "../setting/dbCONECT/mysql_connection.php"; 

if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 

$salelines = mysql_query("SELECT * FROM salelines WHERE Sale='$SALESIDck'");



echo "<table id='theTable' border='1'>
<tr>


<th>QUANTITY </th>
<th>DESCRIPTION----  </th>
<th>PRICE</th>
<th>CHARGE</th>
<th></th>

</tr>";

while($salelinesrow = mysql_fetch_array($salelines))
{

$Price1=$salelinesrow['Price'];

$Price=$FrontPrice.sprintf("%01.2f", $Price1/100); 
$qty=$salelinesrow['Quantity'];
$Descrip=$salelinesrow['Descrip'];
$charg=$salelinesrow['charge'];
$charg1=$FrontPrice.sprintf("%01.2f", $charg/100); 



echo "<tr>";

echo "<td>" . $qty . "</td>";
echo "<td>" . $Descrip . "</td>";
echo "<td><B>" . $Price . "</td>";
echo "<td><B>" . $charg1 . "</td>";





}


echo "</table>";




  ?>

</table>

</body>






 

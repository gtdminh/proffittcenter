
 
<head>
<link rel="stylesheet" type="text/css" href="mystyle.css" />
</head> 	 

<?php

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";




$result = mysql_query("SELECT * FROM Products  ");




echo "<table border='1'>
<tr>
<th>sku   </th>
<th>BARCOD----   </th>
<th>DESCRIPTION----  </th>
<th>PRICE</th>
<th></th>

</tr>";

while($row = mysql_fetch_array($result))
{

$Price1=$row['Price'];

$Price=$FrontPrice.sprintf("%01.2f", $Price1/100); 



echo "<tr>";
echo "<td>" . $row['Sku'] . "</td>";
echo "<td>" . $row['ID'] . "</td>";

echo "<td>" . $row['Description'] . "</td>";

echo "<td><B>" . $Price . "</td>";




echo "</tr>";
}
echo "</table>";

mysql_close($con);
?> 

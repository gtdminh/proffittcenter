<html><head><title>Change Record</title></head>
<body>
<?php
$udid=$_POST['udid'];
$udcod=$_POST['udix'];
$udqty=$_POST['udx'];
$udprice=$_POST['udy'];
$udsd=$_POST['udsd'];
$uded=$_POST['uded'];
$udtype=$_POST['udtype'];

$link=include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

mysql_query(" UPDATE offers SET
Ix='$udcod' ,
X='$udqty' ,
Y='$udprice' ,
OfferType='$udtype' ,
EndDate='$uded' ,
StartDate='$udsd' WHERE Product='$udid'");
//
$pr=$udprice/100;
echo "$udqty ";
echo "<p>for";
echo " $FrontPrice$pr "; 
echo "<BR>$udcod<p>$udsd and end $uded";
echo "PRODUCTS Record Updated";


?>
<form method="POST" action="pro-barcod.php">
<input type="submit" value="Change Another Product">
</form><br>
<td width="300"><form name="form1" method="post" action="../index.php">
      <p>
        <input type="submit" name="Submit" value="main" style="height: 59px; width: 300px;font-size: large; font-weight: 80; color: #64b1ff">
        <span class="style5">
          </span>        </p>
      </form>   

</body>
</html>







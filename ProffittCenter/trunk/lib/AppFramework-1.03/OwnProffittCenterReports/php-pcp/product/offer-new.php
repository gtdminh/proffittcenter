<html><head><title>Change Record</title></head>
<body>
<?php
$type=$_POST['type'];
$xqty=$_POST['xqty'];
$yprice=$_POST['yprice'];
$startdate=$_POST['startdate'];
$enddate=$_POST['enddate'];
$product=$_POST['barcod'];


$link=include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

$sql="INSERT INTO offers (Product, OfferType, X, Y, StartDate, EndDate)
 VALUES
 ('$product','$type','$xqty','$yprice','$startdate','$enddate')";
 
if (!mysql_query($sql,$con))
   {
   die('Error: ' . mysql_error());
   }
 echo "1 record added";
 
mysql_close($con)
 






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







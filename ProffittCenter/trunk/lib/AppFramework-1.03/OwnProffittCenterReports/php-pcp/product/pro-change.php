<html><head><title>Change Record</title></head>
<body>
<?php
$ud_id=$_POST['ud_id'];
$ud_Description=$_POST['ud_Description'];
$ud_Price=$_POST['ud_Price'];
$link=include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

mysql_query(" UPDATE Products  SET
ID='$ud_id' ,
Description='$ud_Description' ,
Price='$ud_Price' WHERE id='$ud_id'");
//
$pr=$ud_Price/100;
echo "$ud_Description ";
echo "<p>";
echo "and PRICE  $FrontPrice$pr "; 
echo "<p>";
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







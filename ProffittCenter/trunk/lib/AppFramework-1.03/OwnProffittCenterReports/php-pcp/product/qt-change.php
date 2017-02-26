


<html><head><title>Change Record</title></head>
<body>
<?php
$ud_id=$_POST['ud_id'];
$ud_Description=$_POST['ud_Description'];
$ud_Price=$_POST['ud_Price'];
$ud_sku=$_POST['ud_sku'];
$ud_qty1=$_POST['ud_qty'];
$ud_qty2=$_POST['ud_qty1'];
$ud_qty=$ud_qty1+$ud_qty2;
$link=include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";



mysql_query(" UPDATE Skus  SET Quantity='$ud_qty' WHERE ID='$ud_sku'");



//
$pr=$ud_Price/100;
echo "$ud_Description ";
echo "<p>";
echo "and PRICE  $FrontPrice$pr "; 
echo "<p>";
echo "and QTY  $ud_qty "; 
echo "<p>";
echo "PRODUCTS QTY Record ONLY Updated";


?>
<form method="POST" action="qt-barcod.php">
<input type="submit" value="Change Another Product QTY">
</form><br>
<td width="300"><form name="form1" method="post" action="../index.php">
      <p>
        <input type="submit" name="Submit" value="main" style="height: 59px; width: 300px;font-size: large; font-weight: 80; color: #64b1ff">
        <span class="style5">
          </span>        </p>
      </form>   

</body>
</html>







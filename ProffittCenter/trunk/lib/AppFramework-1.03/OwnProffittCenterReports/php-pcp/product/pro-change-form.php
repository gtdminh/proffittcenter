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



$query=" SELECT * FROM Products WHERE id='$id'";
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





?>
<table width="400" cellpadding="25" cellspacing="0" border="3">
<tr align="center" valign="top">
<td align="center" colspan="3" rowspan="2" bgcolor="#64b1ff">
<h3>Edit and Submit</h3>
<form action="pro-change.php" method="post">
<input type="hidden" name="ud_id" value="<? echo "$id" ?>">
BARCOD:<? echo "$ID"?>    <input type="hidden" name="ud_ID" value="<? echo "$ID"?>"><br>
Description:    
<textarea name="ud_Description" cols="" rows="3"><? echo "$Description"?></textarea>
<br>
RR PRICE:    <input type="text" name="ud_Price" value="<? echo "$Price"?>"><br>


<input type="Submit" value="Update">
</form>
</td></tr></table>

<?php
++$i;
}
?>

</body>
</html>







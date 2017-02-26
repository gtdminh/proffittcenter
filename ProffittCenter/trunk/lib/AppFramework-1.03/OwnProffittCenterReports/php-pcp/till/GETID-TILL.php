<?php
$Tillid=$_POST['Tillid'];
$Operator=$_POST['Operator'];
$Customer=$_POST['Customer'];
$sales='sales';
require_once('../setting/dbCONECT/mysql_connection.php');
//-----------SALES TABEL
$SALES=mysql_query("INSERT INTO $sales 
(Customer, TillId, Operator) VALUES('$Customer', '$Tillid', '$Operator' ) ") 
or die(mysql_error());
if ($SALES) {
$result = mysql_query("SELECT * FROM sales ORDER BY ID DESC LIMIT 0,1 ")
or die(mysql_error());  
$row = mysql_fetch_array( $result );
if ($row) {
$SALESID1=$row['ID'];
$Tillid1=$row['TillId'];
$Operator1=$row['Operator'];
$Customer1=$row['Customer'];
//----------set cookie----------------------------------
 setcookie('SALESID',$SALESID1); 
setcookie('Tillid1',$Tillid1); 
setcookie('Operator',$Operator); 
setcookie('Customer',$Customer1); 
echo $SALESID1 ;
}
}
?>
<form name="myform" method="POST" action="till.php">
<script language="JavaScript">document.myform.submit();</script>
</form>




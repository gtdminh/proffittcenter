<?php  
setcookie("SALESID", "", time()-3600); 
setcookie("Tillid1", "", time()-3600);
setcookie("Operator", "", time()-3600);
setcookie("Customer", "", time()-3600);

$Tillid1=$_POST['Tillid'];
if ($Tillid1) {
$Tillid=$Tillid1;

}
else {
$Tillid="1";
}
$Operator1=$_POST['Operator'];
if ($Operator1) {
$Operator=$Operator1;

}
else {
$Operator="1234";
}
$Customer1=$_POST['Customer'];
if ($Customer1) {
$Customer=$Customer1;

}
else {
$Customer="9999";
}



 
?>  






<form name="myform" method="POST" action="GETID-TILL.php">
<input type="hidden" name="Operator" value="<? echo "$Operator"?>">
<input type="hidden" name="Tillid" value="<? echo "$Tillid"?> ">
<input type="hidden" name="Customer" value="<? echo "$Customer"?>">
<script language="JavaScript">document.myform.submit();</script>
</form>


<?php
require_once('../mysql-config.php');

$mypassword=$_POST['mypassword']; 

$Authority=$_POST['autho']; 
$con = mysql_connect($server, $username, $password);     
$db= mysql_select_db($database); 

$sql="SELECT * FROM operators WHERE ID='$mypassword' AND Authority='$Authority'";
$result=mysql_query($sql);

$count=mysql_num_rows($result);

if($count==1){
$myusername=mysql_result($result,0,"Description"); 
session_register("mypassword");
session_register("myusername"); 
header("location:index.php");
}
else {
echo "<h1><strong><font color=#FF0000>Wrong Password</font></strong></h1>";
echo '<meta http-equiv=Refresh content="1;url=login.php?reload=1">';
}
?>


 
 
 
 
 




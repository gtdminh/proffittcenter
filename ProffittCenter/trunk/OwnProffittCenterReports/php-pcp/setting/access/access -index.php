<?php 
session_start();
if(!session_is_registered(mypassword)){
header("location:login.php");
}
?>


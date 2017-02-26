<html>
<head>


</head>


<body>

<?
if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 



include "total.php"; 

$CASH=$_POST['cash'];
$CASH1=$FrontPrice.sprintf("%01.2f", $CASH/100);

$CHANG1=($CASH-$total1);
$CHANG=$FrontPrice.sprintf("%01.2f", $CHANG1/100);
?>







  <table border="1" width="715" height="483">
    <tr>
      <td width="213" height="103" align="center">



  <table border="1" width="706" height="407">
    <tr>
      <td width="354" height="160" align="center">




tx1</td>
      <td width="336" height="169" rowspan="2" align="center">



<? include "keybord/payekey.php" ?>





</td>
    </tr>
    <tr>
      <td width="354" height="4" align="center">tx2</td>
    </tr>
    <tr>
      <td width="706" height="226" colspan="2" align="center">
<font size="6" color="#000080">
PAYE = <? echo "$total2"; ?>
    
</font>
<font size="6" color="#00FF00">
<br>
    
</font>
<font size="6" color="#0000FF">
CASH: <? echo "$CASH1"; ?>
    
</font>
<font size="6" color="#00FF00">
<br>
    
</font>
<font size="6" color="#FF00FF">
CHANG:
    
</font><b><font color="#FF00FF" size="7"> <? echo "$CHANG"; ?></font></b>
<font size="6" color="#00FF00">

<form name="form3" method="post" action="START-TILL.php">
       <div align="center">
         <input type="submit" name="next" value="NEXT" style="height: 74; 
         width: 159; font-size: 14; font-weight: 100; color: #000000">
    
</font></td>
    </tr>
  </table>




      </table>




</body>

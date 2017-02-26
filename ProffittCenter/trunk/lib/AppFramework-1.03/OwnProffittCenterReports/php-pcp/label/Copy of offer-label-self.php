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
$offtex=$_POST['offtex'];
$offtex2=$_POST['offtex2'];

//OFFER LABEL __________________________________________

$query=" SELECT * FROM Products , Offers WHERE Products.id='$id' AND Offers.Product='$id'";
$result=mysql_query($query);
$num=mysql_num_rows($result);
if ($num) {

$ID=mysql_result($result,$i,"ID");
$Description=mysql_result($result,$i,"Description");
$Price1=mysql_result($result,$i,"Price");
$Price=sprintf("%01.2f", $Price1/100); 
$x=mysql_result($result,$i,"X");
$ix=mysql_result($result,$i,"Ix");
$y1=mysql_result($result,$i,"Y");
$y=sprintf("%01.2f", $y1/100);
$enddate=mysql_result($result,$i,"EndDate");
$startdate=mysql_result($result,$i,"StartDate");

$x2=$x==1; 
if ($x2) {
$x1=$offqty1;
$cut=$cut1;
$offqtysize1=$offqtytexsize;

}
else {

$x1=$x;
$offqtysize1=$offqtysize;

}
?>
 
<div id="Div1">
<form id="Form1" method="post" runat="server"> 





<div align="left">
  <table border="1" width="220" height="40">
    <tr>
      <td width="220" height="20" colspan="2">
      <p align="center"><font face="Arial" size="4" color="#0000FF">

<? echo "$Description"?>
</font>

      
      </td>
    </tr>
    <tr>
      <td width="150" height="40">
      
      <p align="center"><font face="Arial" size="<? echo "$pricesize"?>" color="#0000FF">
<b>
<? echo "$FrontPrice"?>
<? echo "$Price"?>
</font>
<font face="Arial" size="2" color="#0000FF">

<? echo "$EndPrice"?>
</b>
</font>

      
      
      
      </td>
      <td width="110" height="60" rowspan="2" bgcolor="#ffffff">
      <p align="center">

<font face="Arial" size="1" color="#0000FF">
<b>
<? echo "$offtex2"?></font>
<font face="Arial" size="<? echo "$offqtysize1"?>" color="#0000FF">
<b>
<? echo "$x1"?>
</b>
</font>




<font face="Arial" size="2" color="#0000FF">

<? echo "$offtex"?>
<BR>
<font face="Arial" size="<? echo "$offpricesize"?>" color="#0000FF">
<b>
<? echo "$FrontPrice"?>
<? echo "$y"?>
</font>
<font face="Arial" size="2" color="#0000FF">

<? echo "$EndPrice"?>

<BR>

</font>
<font face="Arial" size="1" color="#0000FF">

END: <? echo "$enddate"?>



</font>



      
     
      
      </td>
    </tr>
    <tr>
      <td width="150" height="10">
      <p align="center"><font face="<? echo "$barcod"?>" color="#0000FF" size="<? echo "$barsize"?>">
<? echo "$ID"?>
</font>


<BR>

<align="center"><font face="Arial" color="#0000FF" size="1">
<? echo "$ShopName"?>
</font>

  
      
      
      </td>
    </tr>
  </table>

<font face="Arial" color="#0000FF" size="1">

#  START DATE: <? echo "$startdate"?> 
<BR>  
##
OFFER ID: <? echo "$ix"?>
</font>
</div>

<form id="Form1" method="post" runat="server">
  
  
  </div>
  <input type="button" value="Print" onclick="JavaScript:printPartOfPage('Div1');">
 
<script type="text/javascript">
 <!--
 function printPartOfPage(elementId)
 {
  var printContent = document.getElementById(elementId);
  var windowUrl = 'about:blank';
  var uniqueName = new Date();
  var windowName = 'Print' + uniqueName.getTime();
  var printWindow = window.open(windowUrl, windowName, 'left=50000,top=50000,width=0,height=0');
 
 printWindow.document.write(printContent.innerHTML);
  printWindow.document.close();
  printWindow.focus();
  printWindow.print();
  printWindow.close();
 }
 // -->
 </script>
 
</form>


<form name="form1" method="GET" action="../label/label-selfform.php">
     
        <input type="submit" name="Submit" value="main" style="height: 59px; width: 300px;font-size: large; font-weight: 80; color: #64b1ff">
        
      </form>   









<?


// NORMAL LABEL______________________________________________________


}
else {

$query1=" SELECT * FROM Products WHERE Products.id='$id'";
$result1=mysql_query($query1);
$num1=mysql_num_rows($result1);
if ($num1) {



$ID=mysql_result($result1,$i,"ID");
$Description=mysql_result($result1,$i,"Description");
$Price1=mysql_result($result1,$i,"Price");

$Price=sprintf("%01.2f", $Price1/100); 



?>

<div id="Div1">
<form id="Form1" method="post" runat="server">

 <p align="left">
  <table border="1" width="220" height="120">
    <tr>
      <td width="220" height="24" colspan="2">
<p align="center"><font size="3">
<? echo "$Description"?></font></p>      </td>
    </tr>
    <tr>
      <td width="220" height="30">
<p align="center"><b><font color="#000000" size="<? echo "$npricesize"?>" face="Arial">
<? echo "$FrontPrice"?>

<? echo 
 "$Price"?></font><? echo "$EndPrice"?></b></p>      </td>
    </tr>
    <tr>
      <td width="220" height="7"><div align="center"><font face="<? echo "$barcod"?>" size="<? echo "$barsize"?>"> <? echo "$ID"?></font>
<br>

 <font size="1"> <? echo "$ShopName"?></font> </div></td>
    </tr>
  </table>




  
  
  </div>
  <input type="button" value="Print" onclick="JavaScript:printPartOfPage('Div1');">
 
<script type="text/javascript">
 <!--
 function printPartOfPage(elementId)
 {
  var printContent = document.getElementById(elementId);
  var windowUrl = 'about:blank';
  var uniqueName = new Date();
  var windowName = 'Print' + uniqueName.getTime();
  var printWindow = window.open(windowUrl, windowName, 'left=50000,top=50000,width=0,height=0');
 
 printWindow.document.write(printContent.innerHTML);
  printWindow.document.close();
  printWindow.focus();
  printWindow.print();
  printWindow.close();
 }
 // -->
 </script>
 
</form>


<form name="form1" method="GET" action="../label/label-selfform.php">
     
        <input type="submit" name="Submit" value="main" style="height: 59px; width: 300px;font-size: large; font-weight: 80; color: #64b1ff">
        
      </form>   




<?
  
  exit;     




mysql_close($con);
 

}
else {
echo '<META HTTP-EQUIV="Refresh" Content="20; URL=label-selfform.php">';

?>
no product find




<?

}



?>

<?php ++$i;
}
?>

</body>
</html>







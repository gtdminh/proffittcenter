


<?php

$id=$_POST['id'];
$offtex=$_POST['offtex'];
$offtex2=$_POST['offtex2'];


$today= date("l,  F d, Y -      :-  h:i"     ,time());

echo ''; 


include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

$query=" SELECT * FROM products , Offers WHERE Products.id=$id AND Offers.Product=$id ";
$result=mysql_query($query);

while ($row = mysql_fetch_assoc($result)) 
{   

} 


$Price=mysql_result($result,$i,"Price");
$Price1=$FrontPrice.sprintf("%01.2f", $Price/100); 

$offerpri=mysql_result($result,$i,"Y");
$offerpri1=$FrontPrice.sprintf("%01.2f", $offerpri/100);

 
$descr=mysql_result($result,$i,"Description");
 
$imag=mysql_result($result,$i,"ID");
$offerqt=mysql_result($result,$i,"X");

$enddate=mysql_result($result,$i,"EndDate");

$ix=mysql_result($result,$i,"Ix");




?>
<style type="text/css">
<!--
.style1 {
	font-size: 90px;
	font-weight: bold;
	font-family: Arial, Helvetica, sans-serif;
	color: #000099;
}
.style2 {
	font-weight: bold;
	font-size: 380px;
	color: #990033;
}
.style3 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 90px;
	color: #990000;
        font-weight: bold;
}
.style4 {
	color: #990000;
	font-size: 400px;
	font-weight: bold;
}
.style5 {
	font-size: 10px;
	color: #666666;
}

-->
</style>







<table width="995" height="650" border="0" bgcolor="#ffffff">
 
  <tr>
    <td height="60" colspan="2"><div  bgcolor="#ffffff" >
      
    <div align="center"><span class="style1"><?php echo "$descr"; ?> </span> </div></td>
  </tr>
  <tr>
    <td height="720" width="780"  BACKGROUND="../imag/<?php echo "$imag"; ?>.<?php echo "$img"; ?>" STYLE="background-repeat: no-repeat; height: 750px; 
  width: 750px; padding-leaft:0px; ">
      
    
	     <div align="right"><span class="style2"> <?php echo "$offerqt"; ?> </span>
           
	  <p align="right"> <span class="style3"><?php echo "$offtex"; ?> </span>    
    
     
                
                
				    
	              
      
 
 
    
        <p align="center"><span class="style4 "> <?php echo "$offerpri1"; ?> </span> 
   </td>
  </tr>
</table>



<p> <p align="center"><b>
                <span class="style5">    <?php echo "$ShopName"; ?>  --  <?php echo "$today"; ?> <BR> OFFER ID:  <?php echo "$ix"; ?>, OFFER END:<?php echo "$enddate"; ?> </span>  &nbsp;</p>

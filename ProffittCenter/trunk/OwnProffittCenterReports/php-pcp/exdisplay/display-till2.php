<?php

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";



$today= date("l,  F d, Y   h:i"     ,time());

echo '<meta http-equiv=Refresh content="2;url=display-till2.php?reload=1">'; 




$query=" SELECT * FROM sales WHERE TillId=2 ORDER BY WhenCreated DESC LIMIT 1 ";
$result=mysql_query($query);

while ($row = mysql_fetch_assoc($result)) 
{   
// echo $row['Total'];   
//  echo $row['Cash']; 
} 


$total=mysql_result($result,$i,"Total");
$total1=$FrontPrice.sprintf("%01.2f", $total/100); 

$cash=mysql_result($result,$i,"Cash");
$cash1=$FrontPrice.sprintf("%01.2f", $cash/100); 

$change=$cash-$total ;
$change1=$FrontPrice.sprintf("%01.2f", $change/100); 

// $sprice=mysql_result($result,$i,"Price");
$sprice1=$FrontPrice.sprintf("%01.2f", $sprice/100); 

// $sprice=$cash-$total ;
$ange1=$FrontPrice.sprintf("%01.2f", $change/100); 






// echo "price:<b> $sprice1";

?>


  </tr>
  
  <style type="text/css">
<!--
.style1 {
	font-size: 24px;
	font-weight: bold;
	color: #663366;
}
.style2 {
	font-size: 24px;
	color: #CC3333;
}
.style3 {
	font-size: 39px;
	color: #990099;
}
.style4 {
	font-size: 16px;
	color: #9900ff;
}
-->
  </style>
  
  <tr>
    <th height="262" bgcolor="#006699" scope="row">&nbsp;</th>
  </tr>
</table>


<table width="791" height="578" border="5" bgcolor="#000000">
  <tr>
    <th width="781" height="322" bgcolor="#0099FF" scope="col"><p><?php echo "$shopname"; ?></p>
      <p></p></th>
  </tr>
  <tr>
    <td height="250"><div  bgcolor="#000000" >
      <span class="style4"><?php echo "$today"; ?> </span>
                <p align="center"><span class="style1">TOTAL: <?php echo "$total1"; ?> </span><br>
				<P align="center">
				<span class="style2">CASH: <?php echo "$cash1"; ?> </span><br>
	            <p align="center">
                  <span class="style3">CHANGE:<b><?php echo "$change1"; ?> </span></p>
                
				  
    </div></td>
  </tr>
</table>

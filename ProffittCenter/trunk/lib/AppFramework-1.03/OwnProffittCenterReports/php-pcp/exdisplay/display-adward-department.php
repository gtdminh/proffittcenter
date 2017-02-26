

<head>
<meta http-equiv=Refresh content="6;url=display-adward-department.php?reload=1">
<style type="text/css">
<!--
.style1 {
	font-size: 60px;
	font-weight: bold;
	color: #990099;
}
.style2 {
	font-size: 170px;
	font-weight: bold;
	color: #FF3300;
}
.style3 {
	font-size: 59px;
	color: #990099;
}
.style4 {
	font-size: 160px;
	font-weight: bold;
	color: #FF3300;
}
.style5 {
	font-size: 19px;
	color: #009999;
}
-->

  </style>
 


</head>

<?php

$today= date("l,  F d, Y -      :-  h:i"     ,time());

echo ''; 


include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";

$dep="9";
$query2=" SELECT * FROM products , skus WHERE Products.Sku=skus.ID AND skus.Department='$dep' ORDER BY RAND()
LIMIT 1 ";
$result2=mysql_query($query2);

while ($row2 = mysql_fetch_assoc($result2)) 
$bar=mysql_result($result2,$i,"ID");




$query=" SELECT * FROM products , Offers WHERE Products.id='$bar' AND Offers.Product='$bar' ORDER BY RAND()
LIMIT 1 ";
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

//----------


?>

<table width="945" height="600" border="0" bgcolor="#ffffff">
 
  <tr>
    <td height="60" width="945" colspan="2"><div  bgcolor="#ffffff" >

<p id="desc" align="center"><span class="style1"><?php echo "$descr"; ?></span></p>
     
                				  
    </td>
  </tr>
  <tr>
    <td height="550" width="550">
      <p align="center"><span > <img border="0" <img id="reflect" src="../imag/<?php echo "$imag"; ?>.<?php echo "$img"; ?>" width="550" height="550"  </span> </p>

 </td>
    <td height="490" width="460" rowspan="0">
                
                <blockquote>
                
				<span class="style2"><?php echo "$offerqt"; ?> </span>
				
				<span class="style3"><?php echo "$offertex"; ?> </span>
				<BR>
                </blockquote>
				<P>
				<span class="style4"> <?php echo "$offerpri1"; ?> </span>

				
				
                </p>

				
				
				</td>
  </tr>
  <tr>
    <td height="50" width="940">
      <p align="center"><b>
                <span class="style5">  <b> <?php echo "$ShopName"; ?></b>  <br>   <?php echo "$today"; ?> </span>
</td>
<td><span class="style5">
OFFER END DATE :  <?php echo "$enddate"; ?><BR>
 ID:<?php echo "$ix"; ?></span>
				  
      </td>
    </tr>
</table>






<script language="JavaScript">


fCol='444444'; //face colour.
sCol='FF0000'; //seconds colour.
mCol='444444'; //minutes colour.
hCol='444444'; //hours colour.

Ybase=60; //Clock height.
Xbase=60; //Clock width.


H='...';
H=H.split('');
M='....';
M=M.split('');
S='.....';
S=S.split('');
NS4=(document.layers);
NS6=(document.getElementById&&!document.all);
IE4=(document.all);
Ypos=0;
Xpos=0;
dots=12;
Split=360/dots;
if (NS6){
for (i=1; i < dots+1; i++){
document.write('<div id="n6Digits'+i+'" style="position:absolute;top:0px;left:0px;width:30px;height:30px;font-family:Arial;font-size:10px;color:#'+fCol+';text-align:center;padding-top:10px">'+i+'</div>');
}
for (i=0; i < M.length; i++){
document.write('<div id="Ny'+i+'" style="position:absolute;top:0px;left:0px;width:2px;height:2px;font-size:2px;background:#'+mCol+'"></div>');
}
for (i=0; i < H.length; i++){
document.write('<div id="Nz'+i+'" style="position:absolute;top:0px;left:0px;width:2px;height:2px;font-size:2px;background:#'+hCol+'"></div>');
}
for (i=0; i < S.length; i++){
document.write('<div id="Nx'+i+'" style="position:absolute;top:0px;left:0px;width:2px;height:2px;font-size:2px;background:#'+sCol+'"></div>');
}
}
if (NS4){
dgts='1 2 3 4 5 6 7 8 9 10 11 12 ';
dgts=dgts.split(' ')
for (i=0; i < dots; i++){
document.write('<layer name=nsDigits'+i+' top=0 left=0 height=30 width=30><center><font face=Arial size=1 color='+fCol+'>'+dgts[i]+'</font></center></layer>');
}
for (i=0; i < M.length; i++){
document.write('<layer name=ny'+i+' top=0 left=0 bgcolor='+mCol+' clip="0,0,2,2"></layer>');
}
for (i=0; i < H.length; i++){
document.write('<layer name=nz'+i+' top=0 left=0 bgcolor='+hCol+' clip="0,0,2,2"></layer>');
}
for (i=0; i < S.length; i++){
document.write('<layer name=nx'+i+' top=0 left=0 bgcolor='+sCol+' clip="0,0,2,2"></layer>');
}
}
if (IE4){
document.write('<div style="position:absolute;top:0px;left:0px"><div style="position:relative">');
for (i=1; i < dots+1; i++){
document.write('<div id="ieDigits" style="position:absolute;top:0px;left:0px;width:30px;height:30px;font-family:Arial;font-size:14px;color:'+fCol+';text-align:center;padding-top:10px">'+i+'</div>');
}
document.write('</div></div>')
document.write('<div style="position:absolute;top:0px;left:0px"><div style="position:relative">');
for (i=0; i < M.length; i++){
document.write('<div id=y style="position:absolute;width:2px;height:2px;font-size:4px;background:'+mCol+'"></div>');
}
document.write('</div></div>')
document.write('<div style="position:absolute;top:0px;left:0px"><div style="position:relative">');
for (i=0; i < H.length; i++){
document.write('<div id=z style="position:absolute;width:2px;height:2px;font-size:4px;background:'+hCol+'"></div>');
}
document.write('</div></div>')
document.write('<div style="position:absolute;top:0px;left:0px"><div style="position:relative">');
for (i=0; i < S.length; i++){
document.write('<div id=x style="position:absolute;width:2px;height:2px;font-size:5px;background:'+sCol+'"></div>');
}
document.write('</div></div>')
}



function clock(){
time = new Date ();
secs = time.getSeconds();
sec = -1.57 + Math.PI * secs/30;
mins = time.getMinutes();
min = -1.57 + Math.PI * mins/30;
hr = time.getHours();
hrs = -1.57 + Math.PI * hr/6 + Math.PI*parseInt(time.getMinutes())/360;

if (NS6){
Ypos=window.pageYOffset+window.innerHeight-Ybase-25;
Xpos=window.pageXOffset+window.innerWidth-Xbase-30;
for (i=1; i < dots+1; i++){
 document.getElementById("n6Digits"+i).style.top=Ypos-15+Ybase*Math.sin(-1.56 +i *Split*Math.PI/180)
 document.getElementById("n6Digits"+i).style.left=Xpos-15+Xbase*Math.cos(-1.56 +i*Split*Math.PI/180)
 }
for (i=0; i < S.length; i++){
 document.getElementById("Nx"+i).style.top=Ypos+i*Ybase/4.1*Math.sin(sec);
 document.getElementById("Nx"+i).style.left=Xpos+i*Xbase/4.1*Math.cos(sec);
 }
for (i=0; i < M.length; i++){
 document.getElementById("Ny"+i).style.top=Ypos+i*Ybase/4.1*Math.sin(min);
 document.getElementById("Ny"+i).style.left=Xpos+i*Xbase/4.1*Math.cos(min);
 }
for (i=0; i < H.length; i++){
 document.getElementById("Nz"+i).style.top=Ypos+i*Ybase/4.1*Math.sin(hrs);
 document.getElementById("Nz"+i).style.left=Xpos+i*Xbase/4.1*Math.cos(hrs);
 }
}
if (NS4){
Ypos=window.pageYOffset+window.innerHeight-Ybase-20;
Xpos=window.pageXOffset+window.innerWidth-Xbase-30;
for (i=0; i < dots; ++i){
 document.layers["nsDigits"+i].top=Ypos-5+Ybase*Math.sin(-1.045 +i*Split*Math.PI/180)
 document.layers["nsDigits"+i].left=Xpos-15+Xbase*Math.cos(-1.045 +i*Split*Math.PI/180)
 }
for (i=0; i < S.length; i++){
 document.layers["nx"+i].top=Ypos+i*Ybase/4.1*Math.sin(sec);
 document.layers["nx"+i].left=Xpos+i*Xbase/4.1*Math.cos(sec);
 }
for (i=0; i < M.length; i++){
 document.layers["ny"+i].top=Ypos+i*Ybase/4.1*Math.sin(min);
 document.layers["ny"+i].left=Xpos+i*Xbase/4.1*Math.cos(min);
 }
for (i=0; i < H.length; i++){
 document.layers["nz"+i].top=Ypos+i*Ybase/4.1*Math.sin(hrs);
 document.layers["nz"+i].left=Xpos+i*Xbase/4.1*Math.cos(hrs);
 }
}

if (IE4){
Ypos=document.body.scrollTop+window.document.body.clientHeight-Ybase-20;
Xpos=document.body.scrollLeft+window.document.body.clientWidth-Xbase-20;
for (i=0; i < dots; ++i){
 ieDigits[i].style.pixelTop=Ypos-15+Ybase*Math.sin(-1.045 +i *Split*Math.PI/180)
 ieDigits[i].style.pixelLeft=Xpos-15+Xbase*Math.cos(-1.045 +i *Split*Math.PI/180)
 }
for (i=0; i < S.length; i++){
 x[i].style.pixelTop =Ypos+i*Ybase/4.1*Math.sin(sec);
 x[i].style.pixelLeft=Xpos+i*Xbase/4.1*Math.cos(sec);
 }
for (i=0; i < M.length; i++){
 y[i].style.pixelTop =Ypos+i*Ybase/4.1*Math.sin(min);
 y[i].style.pixelLeft=Xpos+i*Xbase/4.1*Math.cos(min);
 }
for (i=0; i < H.length; i++){
 z[i].style.pixelTop =Ypos+i*Ybase/4.1*Math.sin(hrs);
 z[i].style.pixelLeft=Xpos+i*Xbase/4.1*Math.cos(hrs);
 }
}
setTimeout('clock()',100);
}
clock();
//-->
</script>



<?php require_once("access -index.php"); ?>
<head>
<title></title>
</head>

<body bgcolor="#000000" text="#00ffff">


<p>

<?php
include "../config.php"; 
?>

<div align="left" style="width: 372; height: 119">
  <table border="10" width="368" height="176" bordercolor="#3333FF" bordercolordark="#3333FF" bordercolorlight="#008080" bgcolor="#008080">
    <tr>
      <td width="368" height="45" colspan="2" bgcolor="#3333FF">
        <p align="center"><b><font size="3"><? echo "$ShopName"?>  </font>
 <form name="form3" method="post" action="logout.php">
       <div align="center">
         <input type="submit" name="Submit" value="<? echo $_SESSION["myusername"]; ?>  : LOGOUT" style="height: 40; 
         width: 150; font-size: 12; font-weight: 130; color: #880000">
    </form> 

      </td>
    </tr>
    <tr>
    
      <td width="172" height="119"><form name="form1" method="post" action="../../label/label-selfform.php">
       <div align="center">
         <input type="submit" name="Submit" value="LABEL" style="height: 70; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 
<BR>
<form name="form2" method="post" action="../../label/A4-offerform.php">
       <div align="center">
         <input type="submit" name="Submit" value="A4 OFFER LABEL" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 
<BR>
<form name="form5" method="post" action="../../product/offer-new-barcod.php">
       <div align="center">
         <input type="submit" name="Submit" value="EDIT OFFER" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 



<BR>
<form name="form5" method="post" action="../../product/qt-barcod.php">
       <div align="center">
         <input type="submit" name="Submit" value="AD-QTY" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 
<BR>
<form name="form5" method="post" action="../../product/pro-barcod.php">
       <div align="center">
         <input type="submit" name="Submit" value="EDIT PRODUCT" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 





</td>
      <td width="180" height="119">

<BR>
<form name="form5" method="post" action="../../product/offer-delete -barcod.php">
       <div align="center">
         <input type="submit" name="Submit" value="DELETE OFFER" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 

      
      
<form name="form4" method="post" action="../../exdisplay/displa-select.php">
       <div align="center">
             <input type="submit" name="Submit" value="displa-select" style="height: 72; 
         width: 161; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 

<BR>
<form name="form5" method="post" action="../../reports/allstock.php">
       <div align="center">
         <input type="submit" name="Submit" value="ALL PRODOCTS" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 



      </td>
    </tr>
 
  </table>
  
 <span id="liveclock" style="position:absolute;left:100;top:00;">
</span>

<script language="JavaScript">
 <!--


function show5(){
if (!document.layers&&!document.all&&!document.getElementById)
return

 var Digital=new Date()
 var hours=Digital.getHours()
 var minutes=Digital.getMinutes()
 var seconds=Digital.getSeconds()

var dn="PM"
if (hours<12)
dn="AM"
if (hours>12)
hours=hours-12
if (hours==0)
hours=12

 if (minutes<=9)
 minutes="0"+minutes
 if (seconds<=9)
 seconds="0"+seconds
//change font size here to your desire
myclock="Current Time:"+hours+":"+minutes+":"
 +seconds+" "+dn+"</b>"
if (document.layers){
document.layers.liveclock.document.write(myclock)
document.layers.liveclock.document.close()
}
else if (document.all)
liveclock.innerHTML=myclock
else if (document.getElementById)
document.getElementById("liveclock").innerHTML=myclock
setTimeout("show5()",1000)
 }


window.onload=show5
 //-->
 </script>

  
  
</div>

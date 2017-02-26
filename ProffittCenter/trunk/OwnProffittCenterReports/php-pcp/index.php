<head>
<title></title>
</head>

<body bgcolor="#000000" text="#00FFFF">

<?php
include "setting/config.php"; 
?>

<div align="left" style="width: 372; height: 119">
  <table border="10" width="368" height="176" bordercolor="#3333FF" bordercolordark="#000080" bordercolorlight="#0000FF" bgcolor="#008080">
    <tr>
      <td width="368" height="45" colspan="2" bgcolor="#3333FF">
        <p align="center"><b><font size="3"><? echo "$ShopName"?></font></b></p>
      </td>
    </tr>
    <tr>
    
      <td width="172" height="119"><form name="form1" method="post" action="label/label-selfform.php">
       <div align="center">
         <input type="submit" name="Submit" value="LABEL" style="height: 70; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 

<form name="form2" method="post" action="label/A4-offerform.php">
       <div align="center">
         <input type="submit" name="Submit" value="A4 OFFER LABEL" style="height: 72; 
         width: 173; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 



</td>
      <td width="180" height="119">
      
      <form name="form3" method="post" action="setting/access/index.php">
       <div align="center">
         <input type="submit" name="Submit" value="LOGIN" style="height: 74; 
         width: 159; font-size: 14; font-weight: 100; color: #000000">
    


   </div>
</form> 
<form name="form4" method="post" action="exdisplay/displa-select.php">
       <div align="center">
             <input type="submit" name="Submit" value="displa-select" style="height: 72; 
         width: 161; font-size: 14; font-weight: 100; color: #000000">
       </div>
</form> 

<form name="form4" method="post" action="till/START-TILL.php">
       <div align="center">
             <input type="submit" name="Submit" value="WEB-TILL(STILL WORKING ON IT)" style="height: 72; 
         width: 161; font-size: 14; font-weight: 100; color: #000000">
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

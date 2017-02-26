
<html>
<head>
<meta http-equiv="Content-Language" content="en-gb">
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>1</title>
</head>

<body bgcolor="#000000" text="#00FFFF">

<?php
include "../config.php"; 
?>

<div align="left">
  <table border="10" width="290" height="177" bordercolor="#00FFFF" bordercolorlight="#008080" bordercolordark="#008000">
    <tr>
      <td width="307" height="30" bgcolor="#008000">
        <p align="center"><? echo "$ShopName"?></p>
      </td>
    </tr>
    <tr>
      <td width="307" height="118" bgcolor="#008080" bordercolor="#808000">
      <form name="form1" method="post" action="checklogin.php">

<p align="center">

<b>

AUTHORITY:</b> <select name="autho" type="tex" id="autho">
<option value="999">SELECT</option>

<option value="0">OWNER</option>

<option value="1">MANAGER</option>
</select>


<p align="center">

last 4 number(<font color="#C0C0C0">10000**,</font><font color="#FF0000">1234</font>)
  <P align="center">
<b>
Password:</b><font color="#FF0000" size="4"><b><input name="mypassword" type="password" id="mypassword" size="13" style="color: #FF0000; font-size: 14pt; font-weight: bold">
</b></font>
<BR>
<input type="submit" name="Submit" value="LOGIN" style="height: 50; width: 280; font-size: large; font-weight: 80; color: #649888">
       
</p>
       
</td>
    </tr>
    <tr>
      <td width="307" height="15" bgcolor="#008080">\\</td>
    </tr>
  </table>
</div>

</body>

</html>

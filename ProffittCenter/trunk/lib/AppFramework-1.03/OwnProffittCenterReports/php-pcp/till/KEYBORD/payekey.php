

 <head>
<script type="text/javascript"> 
function addCode(key){ 
        var cash = document.forms[0].cash; 
        if(cash.value.length <9999999){ 
                cash.value = cash.value + key; 
        } 
        
} 
 

</script> 




<style> 

#qtykeypad {margin:auto; margin-top:20px;} 
 
#qtykeypad tr td { 
        vertical-align:middle;  
        text-align:center;  
        border:1px solid #000000;  
        font-size:18px;  
        font-weight:bold;  
        width:40px;  
        height:30px;  
        cursor:pointer;  
        background-color:#666666;  
        color:#CCCCCC;} 
#qtykeypad tr td:hover {background-color:#999999; color:#FFFF00;} 
 
.display { 
        width:130px;  
        margin:10px auto auto auto;  
        background-color:#000000;  
        color:#00FF00;  
        font-size:18px;  
        border:1px solid #999999; 
} 

</style> 



 

</head>


 





<div align="left">
  <table border="1" width="137" height="117" bordercolor="#000080">
    <tr>
      <td width="402" height="58" align="center" bgcolor="#000080">
        
        <table id="qtykeypad" cellpadding="5" cellspacing="3" width="315" height="205" bgcolor="#000080"> 
        <tr> 
        <td onclick="addCode('1');" width="40" height="21"><font size="6">&nbsp;
          1</font></td> 
        <td onclick="addCode('2');" width="40" height="21">
          <p align="center"><font size="6">&nbsp;&nbsp;2</font></p>
        </td> 
        <td onclick="addCode('3');" width="84" height="21"><b><font size="6">&nbsp;
          3</font></b></td> 
    </tr> 
    <tr> 
        <td onclick="addCode('4');" width="40" height="21"><font size="6">&nbsp;
          4</font></td> 
        <td onclick="addCode('5');" width="40" height="21">
          <p align="center"><font size="6">&nbsp; 5</font></p>
        </td> 
        <td onclick="addCode('6');" width="84" height="21"><font size="6">&nbsp;
          6</font></td> 
    </tr> 
    <tr> 
        <td onclick="addCode('7');" width="40" height="21"><font size="6">&nbsp;
          7</font></td> 
        <td onclick="addCode('8');" width="40" height="21"><font size="6">&nbsp;
          8</font></td> 
        <td onclick="addCode('9');" width="84" height="21"><font size="6">&nbsp;
          9</font></td> 
    </tr> 
    <tr> 
        <td onclick="addCode('0');" width="40" height="1"><font size="6">&nbsp; 0</font></td> 
        <td onclick="addCode('00');" width="40" height="1"><font size="6">&nbsp;00</font></td> 
        <td onclick="addCode('-');" width="84" height="1"><font size="6">&nbsp; -</font></td> 
    </tr> 
</table> 
        <b><font size="5" color="#FF00FF">
         <form method="POST"  action="<?php echo $_SERVER['PHP_SELF']; ?>" id="theFormID">
<input type="text" name="cash"  value=""  size="17" style="color: #800080; font-size: 18pt; font-weight: bold; text-align: Center; border-style: solid; border-color: #000080" >
  </font></b>
  <input type="submit" value="ENTER" name="FIND" style="font-size: 24pt; font-weight: bold; color: #008080">
<input type="reset" value="CLEAR" name="FIND" style="font-size: 14pt; font-weight: bold; color: #00FFFF">
 </form> 

        
        </td>
    </tr>
  </table>
</div>

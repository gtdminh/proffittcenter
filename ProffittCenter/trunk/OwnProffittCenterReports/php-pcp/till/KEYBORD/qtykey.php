

 <head>
<script type="text/javascript"> 
function addCode(key){ 
        var qty1 = document.forms[0].qty1; 
        if(qty1.value.length <9999999){ 
                qty1.value = qty1.value + key; 
        } 
        
} 
 
function emptyCode(){ 
        document.forms[0].qty.value = ""; 
} 
</script> 


<script>
 function showTable(){
 document.getElementById('qtykeypad').style.visibility = "visible";
 }
 function hideTable(){
 document.getElementById('qtykeypad').style.visibility = "hidden";
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



 

<table id="qtykeypad" cellpadding="5" cellspacing="3"> 
<TR>
<form method="POST"  action="till.php" id="theFormIDqty">
QTY :<input type="text" name="qty1"  value=""  size="10" >
<input type="submit" value="ENTER" name="ENTER" style="font-size: 12pt; font-weight: bold; color: #00FFFF">
 </form>
</TR>
        <tr> 
        <td onclick="addCode('1');">1</td> 
        <td onclick="addCode('2');">2</td> 
        <td onclick="addCode('3');">3</td> 
    </tr> 
    <tr> 
        <td onclick="addCode('4');">4</td> 
        <td onclick="addCode('5');">5</td> 
        <td onclick="addCode('6');">6</td> 
    </tr> 
    <tr> 
        <td onclick="addCode('7');">7</td> 
        <td onclick="addCode('8');">8</td> 
        <td onclick="addCode('9');">9</td> 
    </tr> 
    <tr> 
        <td onclick="addCode('*');">*</td> 
        <td onclick="addCode('0');">0</td> 
        <td onclick="addCode('-');">-</td> 
   </td>
</tr> 
</table> 


</body>
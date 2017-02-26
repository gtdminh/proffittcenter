<body onload="emptyCode();"> 
 
<script type="text/javascript"> 
function addCode(key){ 
        var code = document.forms[0].code; 
        if(code.value.length < 999999999999){ 
                code.value = code.value + key; 
        } 
        if(code.value.length == 999994){ 
                document.getElementById("message").style.display = "block"; 
                setTimeout(submitForm,1000);  
        } 
} 
 
function submitForm(){ 
        document.forms[0].submit(); 
} 
 
function emptyCode(){ 
        document.forms[0].code.value = ""; 
} 
</script> 
<style> 
body { 
        text-align:center;  
        background-color:#333333;  
        font-family:Verdana, Arial, Helvetica, sans-serif; 
}  
#keypad {margin:auto; margin-top:20px;} 
 
#keypad tr td { 
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
#keypad tr td:hover {background-color:#999999; color:#FFFF00;} 
 
.display { 
        width:130px;  
        margin:10px auto auto auto;  
        background-color:#000000;  
        color:#00FF00;  
        font-size:18px;  
        border:1px solid #999999; 
} 
#message { 
        text-align:center;  
        color:#009900;  
        font-size:14px;  
        font-weight:bold;  
        display:none; 
} 
</style> 
 
<form action="code.htm" method="get"> 
<table id="keypad" cellpadding="5" cellspacing="3"> 
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
        <td onclick="addCode('#');">#</td> 
    </tr> 
</table> 
<input type="text" name="code" value=""  /> 
<p id="message">ACCESSIGN...</p> 
</form> 
</body>
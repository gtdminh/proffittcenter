 <head>
<script type="text/javascript"> 
function barcodkey(key){ 
        var id = document.forms[0].id; 
        if(id.value.length <9999999){ 
                id.value = id.value + key; 
        } 
        
} 
 
function emptyCode(){ 
        document.forms[0].id.value = ""; 
} 
</script> 

<style> 

#barcodkey {margin:auto; margin-top:20px;} 
 
#barcodkey tr td { 
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
#barcodkey tr td:hover {background-color:#999999; color:#FFFF00;} 
 
</style> 

<table id="barcodkey" cellpadding="5" cellspacing="3"> 
        <tr> 
        <td onclick="barcodkey('1');">1</td> 
        <td onclick="barcodkey('2');">2</td> 
        <td onclick="barcodkey('3');">3</td> 
    
        <td onclick="barcodkey('4');">4</td> 
        <td onclick="barcodkey('5');">5</td> 
         </tr> 
    <tr> 

        <td onclick="barcodkey('6');">6</td> 
   
        <td onclick="barcodkey('7');">7</td> 
        <td onclick="barcodkey('8');">8</td> 
        <td onclick="barcodkey('9');">9</td> 
   
        
        <td onclick="barcodkey('0');">0</td> 
        
    </tr> 
</table> 

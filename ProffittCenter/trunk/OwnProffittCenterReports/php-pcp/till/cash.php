<html>
<head>
<script language=javascript>
function submitPostLink(){document.postlink.submit();}
</script>


<script language=javascript>
function submitPostLink1(){document.postlink1.submit();}
</script>

</head>


<body>

<form action="<?php echo $_SERVER['PHP_SELF']; ?>" name=postlink method="post">
<input type="hidden" name="cash" value="1000">

</form>
<a href=# onclick="submitPostLink()">£10.00</a>




<form action="<?php echo $_SERVER['PHP_SELF']; ?>" name=postlink1 method="post">
<input type="hidden" name="id" value="54491472">
<input type="hidden" name="qty" value="1">
</form>
<a href=# onclick="submitPostLink()"><img border="0" src="../imag/54491472.jpg" width="99" height="75"></a>





<BR>
</body>
</html>

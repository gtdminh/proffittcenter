<? 
$qtypo=$_POST['qty1'];
setcookie('qty1',$qtypo);
if ($qtypo) {
$qty= $_COOKIE['qty1'];
}
else {
?>
<form name="qtyform1" method="POST" action="<?php echo $_SERVER['PHP_SELF']; ?>">
<input type="hidden" name="qty1" value="1">
<script language="JavaScript">document.qtyform1.submit();</script>
</form>
<?
 }
?>    

	<script>
		!window.jQuery && document.write('<script src="jquery-1.4.3.min.js"><\/script>');
	</script>
	<script type="text/javascript" src="./fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
	<script type="text/javascript" src="./fancybox/jquery.fancybox-1.3.4.pack.js"></script>
	<link rel="stylesheet" type="text/css" href="./fancybox/jquery.fancybox-1.3.4.css" media="screen" />
 	<link rel="stylesheet" href="style.css" />
	<script type="text/javascript">
		$(document).ready(function() {

	$("#qtyfancy").fancybox();

$("#barcodenterfancy").fancybox();

$("#payefancy").fancybox();

$("#various2").fancybox();

			$("#various3").fancybox({
				'width'				: '75%',
				'height'			: '75%',
				'autoScale'			: false,
				'transitionIn'		: 'none',
				'transitionOut'		: 'none',
				'type'				: 'iframe'
			});

			$("#various4").fancybox({
				'padding'			: 0,
				'autoScale'			: false,
				'transitionIn'		: 'none',
				'transitionOut'		: 'none'


});
		});

	</script>
<title> till</title>
<style>
body {
	background-color: #8f8f8f;
}
</style><meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"></head>
<body text="#000000" link="#0000FF" vlink="#0000FF" alink="#0000FF">
  <? 
if(isset($_COOKIE['SALESID']))
	$SALESIDck = $_COOKIE['SALESID']; 
if ($SALESIDck) {
include "total.php" ;
include "quantity.php";	
 include "getproduct.php"; 
}
else {
?>
<form name="tillmyform" method="POST" action="START-TILL.php">
<script language="JavaScript">document.tillmyform.submit();</script>
</form>
<?
}
 ?>

  <table border="1" width="880" height="424">
    <tr>
 <td width="470" height="200" rowspan="2">
<b>
</form>
<iframe id="barcodkey" name="barcodkeyframe" width="450"
              height="460" src="barcodscan.php"></iframe>
</td>
 <td height="41" colspan="2" align="left">
<? 


if ($total1) {
?>
<form method = "POST" action ="paye.php">
<input type="hidden" name="cash"  >
<input type="submit" value="PAYE" style="font-size: 12pt; font-weight: bold; color: #00FFFF">
</form> 

<?
}
?>
<input type="submit" value="QTY" id="qtyfancy"  href="keybord/qtykey.php">
  <input type="submit" value="BARCOD " id="barcodenterfancy" href="keybord/barcodkey.php">   
     <table width="252">
 <td width="244" height="327">
<? include "key/key.php" ?>
</td>
</table>
</body>
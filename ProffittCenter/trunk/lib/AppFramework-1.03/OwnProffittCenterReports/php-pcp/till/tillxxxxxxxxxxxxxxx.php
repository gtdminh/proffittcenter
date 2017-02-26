<head>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js"></script>
	<script>
		!window.jQuery && document.write('<script src="jquery-1.4.3.min.js"><\/script>');
	</script>
	<script type="text/javascript" src="./fancybox/jquery.mousewheel-3.0.4.pack.js"></script>
	<script type="text/javascript" src="./fancybox/jquery.fancybox-1.3.4.pack.js"></script>
	<link rel="stylesheet" type="text/css" href="./fancybox/jquery.fancybox-1.3.4.css" media="screen" />
 	<link rel="stylesheet" href="style.css" />
	<script type="text/javascript">
		$(document).ready(function() {
			/*
			*   Examples - images
			*/

			$("a#example1").fancybox();

			$("a#example2").fancybox({
				'overlayShow'	: false,
				'transitionIn'	: 'elastic',
				'transitionOut'	: 'elastic'
			});

			$("a#example3").fancybox({
				'transitionIn'	: 'none',
				'transitionOut'	: 'none'	
			});

			$("a#example4").fancybox({
				'opacity'		: true,
				'overlayShow'	: false,
				'transitionIn'	: 'elastic',
				'transitionOut'	: 'none'
			});

			$("a#example5").fancybox();

			$("a#example6").fancybox({
				'titlePosition'		: 'outside',
				'overlayColor'		: '#000',
				'overlayOpacity'	: 0.9
			});

			$("a#example7").fancybox({
				'titlePosition'	: 'inside'
			});

			$("a#example8").fancybox({
				'titlePosition'	: 'over'
			});

			$("a[rel=example_group]").fancybox({
				'transitionIn'		: 'none',
				'transitionOut'		: 'none',
				'titlePosition' 	: 'over',
				'titleFormat'		: function(title, currentArray, currentIndex, currentOpts) {
					return '<span id="fancybox-title-over">Image ' + (currentIndex + 1) + ' / ' + currentArray.length + (title.length ? ' &nbsp; ' + title : '') + '</span>';
				}
			});

			/*
			*   Examples - various
			*/

			$("#various1").fancybox({
				'titlePosition'		: 'inside',
				'transitionIn'		: 'none',
				'transitionOut'		: 'none'
			});

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



<div align="left">
  <table border="1" width="880" height="424">
    <tr>

 <td width="470" height="200" rowspan="2">

<b><font color="#800000" size="5">

TOTAL =</font><font color="#0000FF" size="6"><? echo "$total2"; ?></font>





</form>

<iframe id="frame" name="frame" width="450"
              height="380" src="saleline.php"></iframe>
<?


	 
$qtypo=$_POST['qty2'];
if ($qtypo) {
$qty=$_POST['qty2'];
}
else {
?>

<form name="qtyform" method="POST" action="<?php echo $_SERVER['PHP_SELF']; ?>">
<input type="hidden" name="qty2" value="1">

<script language="JavaScript">document.qtyform.submit();</script>
</form>

<?

 }
?>    
<form method="POST"  action="<?php echo $_SERVER['PHP_SELF']; ?>" id="theFormID">


<input type="text" name="id"  id="theFieldID" size="33">

<input type="hidden" name="qty"  value="<? echo "$qty"  ?>"   > 
<input type="submit" value="ENTER" name="FIND" style="font-size: 12pt; font-weight: bold; color: #00FFFF">

QTY:<font color="#0000ff" size="5"><b><? echo "$qty"  ?> </font>
 </form> 






</td>



      <td height="41" colspan="2" align="left">




 


<?
if ($total1) {


?>
<form method="POST" action="paye.php">
  
    
      <div align="left" style="width: 171; height: 434">
        <input type="hidden" name="cash" value="<? echo "$total1"; ?>" size="4">
        <input name="paye" type="submit" id="paye" style="font-size: 24pt; font-weight: bold; color: #0000FF; border-style: solid; border-color: #008080" value="PAYE">
        <?
}
?>





<form method="POST" action="START-TILL.php">
<input type="submit" value="NEW SALE" name="newsale" style="font-size: 14pt; font-weight: bold; color: #00FFFF">
</form>
 <form method="POST" id="various1" action="keybord/qtykey.php">
<input type="submit" value="QTY" name="qty" style="font-size: 14pt; font-weight: bold; color: #00FFFF">
</a>
</form>
   

<li><a id="various2" href="keybord/qtykey.php">QTY</a></li>

    
   
     <table width="252">

    
    
      <td width="244" height="327">
<? include "key/key.php" ?>


</td>
  
  </table>









<script type="text/javascript">theFormID.theFieldID.focus();</script>

      </div>
    &nbsp;

</body>






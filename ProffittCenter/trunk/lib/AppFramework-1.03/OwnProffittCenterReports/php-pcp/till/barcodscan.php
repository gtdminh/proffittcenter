<? setcookie("qty1", "", time()-3600); 
include "getproduct.php";
include "total.php" ;
if(isset($_COOKIE['qty1']))
$qtypo = $_COOKIE['qty1']; 
if ($qtypo) {
$qty= $_COOKIE['qty1'];
}
else {
$qty='1';
 }

?>    
<form method="POST"  action="<?php echo $_SERVER['PHP_SELF']; ?>" id="theFormBARCOD" >
BARCOD: <input type="text" name="id"  id="theFieldBARCOD" value=""  size="10" >
<input type="hidden" name="qty"   value="<? echo "$qty"  ?>"  size="15" > QTY:: <? echo $qty ; ?>
<input type="submit" value="ENTER" name="ENTER" style="font-size: 12pt; font-weight: bold; color: #00FFFF">
 </form>
<? include "keybord/barcodkey.php" ;    echo  $total2 ;  ?>
<? 

include "saleline.php" ;

?>


<script type="text/javascript">theFormBARCOD.theFieldBARCOD.focus();</script>
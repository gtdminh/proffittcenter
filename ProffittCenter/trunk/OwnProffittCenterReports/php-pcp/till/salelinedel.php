
 


<?php

include "countlineconnection.php"; 
include "../setting/config.php"; 

mysql_query("DELETE FROM countline") 
or die(mysql_error()); 

echo '<META HTTP-EQUIV="Refresh" Content="0; URL=till.php">';    
exit;



 
?>




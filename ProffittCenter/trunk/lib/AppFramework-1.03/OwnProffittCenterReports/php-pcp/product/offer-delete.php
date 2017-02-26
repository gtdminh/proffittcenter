<?php require_once("../setting/access/access.php"); ?>

<?php

include "../setting/dbCONECT/mysql_connection.php"; 
include "../setting/config.php"; 
include "../setting/mysql-config.php";


$id=$_GET['id'];

$query = "DELETE FROM offers WHERE Product = ('$id')"; 

$result = mysql_query($query); 

echo "The data has been deleted."; 

echo "$id"; 


 
mysql_close($con);
 

?>



<td width="300"><form name="form1" method="post" action="../index.php">
      <p>
        <input type="submit" name="Submit" value="main" style="height: 59px; width: 300px;font-size: large; font-weight: 80; color: #64b1ff">
        <span class="style5">
          </span>        </p>
      </form>   

</body>
</html>

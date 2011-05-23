<?php
class DboMyDboSource extends DataSource {
    public function isConnected() {
           return true;
    }
    
    function connect(){
            $this->connected = true;
            return true;
    }
 
    function disconnect(){
            $this->connected = false;
            return true;
    }
}
?>
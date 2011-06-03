<?php
class AppController extends Controller {
    var $components = array('Session');
    var $helpers = array('Html','Form','Session','Js' => array('Jquery'));

    
    public function redirect($url, $status = null, $exit = true) {
        parent::redirect($url, $status, $exit);
    }
    
    /*
     * Determines is an array is numerically indexed
     *
     * @param array $array
     *
     * @return boolean
     */
    protected function _numeric ($array = array()) {
    	if (empty($array)) {
    		return null;
    	}
    	$keys = array_keys($array);
	foreach ($keys as $key) {
	    if (!is_numeric($key)) {
	    	return false;
	    }
	}
	return true;
    }
}
?>
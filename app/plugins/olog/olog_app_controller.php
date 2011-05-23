<?php

class OlogAppController extends PluginManagerAppController {
  
  var $components = array('Security','LogAuth','Session');
//  var $helpers = array('Html','Form','Session','Xml','Js' => array('Jquery'));
  var $helpers = array('Html','Form','Session','Xml');
  
  function beforeFilter() { 
    $this->LogAuth->allowedActions = array('display');
    $this->LogAuth->authorize = 'actions';
    $this->LogAuth->loginAction = array('plugin' => 'olog', 'controller' => 'users', 'action' => 'login');
    $this->LogAuth->logoutRedirect = array('plugin' => 'olog', 'controller' => 'users', 'action' => 'login');
    $this->LogAuth->loginRedirect = array('plugin' => 'olog', 'controller' => 'logs', 'action' => 'index');
  }
  /**
   * Overrides CakePHP's default paging settings, optional.
   *
   * @var array
   */
  public $paginate = array(
    'page' => 1,
    'limit' => 20,
  );

  /**
   * Overrides Controller::paginate() to set paging options in the
   * Model::paginate property so they available in the Model::paginateCount()
   * method.
   *
   * @param mixed $object
   * @param mixed $scope
   * @param mixed $whitelist
   * @return array The result set
   */
  public function paginate($object = null, $scope = array(), $whitelist = array()) {
    if (isset($this->passedArgs['page'])) {
      $this->paginate['page'] = $this->passedArgs['page'];
    }
    if (isset($this->passedArgs['limit'])) {
      $this->paginate['limit'] = $this->passedArgs['limit'];
    }
    $options = $this->paginate;
    if (isset($options[$object])) {
      $options = array_merge($options, $options[$object]);
      unset($options[$object]);
    }
    $this->$object->paginate = $this->paginate[$object] = $options;
    return parent::paginate($object = null, $scope = array(), $whitelist = array());
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
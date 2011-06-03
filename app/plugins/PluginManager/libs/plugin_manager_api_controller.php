<?php

class PluginManagerApiController extends PluginManagerAppController 
{
    var $autoRender = false;
    
    function __construct($plugin)
    {
        $this->plugin = $plugin;
    
        parent::__construct();
    }
    
	function &getInstance() {
        return PluginManager::getApiInstance($this->name);
	}
}

?>
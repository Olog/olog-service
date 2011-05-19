<?php
App::import('Controller', null); 
App::import('Controller', 'App'); 
class PluginManagerAppController extends AppController 
{
    var $apis = array();

    function __construct()
    {    
        if (!PluginManager::isApiController(get_class($this)))
        {            
            if (empty($this->beforeFilter))
                $this->beforeFilter = array();    
                
            array_push($this->beforeFilter, 'callBeforeFilterHooks');
            
            PluginManager::callHooks('controllerConstruct', null, $this);
        }
        
        parent::__construct();             
    }
    
    /**
     * This function overwrites Controller::
     *
     */
    function constructClasses()
    {
        // Load all Apis used in this controller
        if (!empty($this->apis))
        {
            if (is_array($this->apis))
            {
                foreach ($this->apis as $api)
                {
                    list($api) = PluginManager::extractApiAndPlugin($api);
                    
                    $apiClass = $api.'Api';
                    $this->$apiClass =& PluginManager::getApiInstance($api);
                }
            }
            else 
            {
                list($api) = PluginManager::extractApiAndPlugin($this->apis);
                
                $apiClass = $api.'Api';
                $this->$apiClass =& PluginManager::getApiInstance($api);
            }
        }        

        parent::constructClasses();
    }
    
    function afterFilter()
    {
        $timersPercentage = Performance::getTimersPercantage();
        if (!empty($timersPercentage) && (count($timersPercentage)>1))
        {
            debug($timersPercentage);
        }
    }
            
    function callBeforeFilterHooks()
    {                    
        PluginManager::callHooks('beforeFilter', null, $this);        
    } 
}

?>
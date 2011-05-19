<?php

class PluginManager {

    /**
     * Checks weather a certain plugin exists, or not.
     *
     * @param unknown_type $plugin
     * @return unknown
     */
    function pluginExists($plugin)
    {
        return is_dir(APP.'plugins'.DS.Inflector::underscore($plugin));
    }
        
    /**
     * This function can be used to check weather a given Controller is an Api or not (means if it extends the PluginManagerApiController class to some Degree)
     * You can either provide the object as parameter or the result of get_class($this), the object name (which is faster).
     * 
     * Returns false if the controller is no Api, and the 
     *
     * @param mixed $controller
     * @return mixed
     */
    function isApiController($controller)
    {
        if (is_object($controller))
            $controller = get_class($controller);
        
        $parentClass = get_parent_class($controller);
        while (!empty($parentClass))
        {            
            if ($parentClass==low('PluginManagerApiController'))
            {
                if (!strpos(low($controller), 'api')===false)
                {
                    list($api) = explode('api', low($controller));
                    return true;
                }
            }

            $parentClass = get_parent_class($parentClass);            
        }    
        
        return false;    
    }
    
    function uses($lib)
    {
		$args = func_get_args();
		foreach($args as $arg) {
			require_once(APP.'plugins'.DS.'pluginManager'.DS.'libs'.DS.Inflector::underscore($arg) . '.php');
		}    
    }
    
    function pluginUses($lib)
    {
		$args = func_get_args();
		foreach($args as $arg) 
		{
		    list($plugin, $lib) = explode('/', $arg);
			require_once(APP.'plugins'.DS.Inflector::underscore($plugin).DS.'libs'.DS.Inflector::underscore($lib) . '.php');
		}    
    }    
    
    
    /**
     * Loads the php file of an Api. See extractApiAndPlugin() for format of $api.
     *
     * @param unknown_type $api
     */
    function loadApi($api)
    {
        list($api, $plugin) = PluginManager::extractApiAndPlugin($api);
           
        $apiClass = $api.'Api';
        if (!class_exists($apiClass))            
            require_once APP.'plugins'.DS.$plugin.DS.'controllers'.DS.Inflector::underscore($api).'_api.php';        
    }    
    
    /**
     * Creates an instance of an Api and returns it. If an instance has been created before, that instance will be returned to save
     * Memory & Performance time. See extractApiAndPlugin() for format of $api.
     *
     * @param string $api
     * @return object
     */
    function &getApiInstance($api)
    {
        PluginManager::loadApi($api);
        
        list($api, $plugin) = PluginManager::extractApiAndPlugin($api);          
    
        $apiClass = $api.'Api';
        
        uses('class_registry');
        
        $classKey = 'PluginManager[Apis]::'.$apiClass;
        if (!ClassRegistry::isKeySet($classKey))
        {
            $apiInstance = &new $apiClass($plugin);
            $apiInstance->constructClasses();
            
    		foreach($apiInstance->components as $c) 
    		{
    			if (isset($apiInstance->{$c}) && is_object($apiInstance->{$c}) && is_callable(array($apiInstance->{$c}, 'startup'))) 
    			{
    				$apiInstance->{$c}->startup($apiInstance);
    			}
    		}
            
            $apiInstance->beforeFilter();
                
            ClassRegistry::addObject($classKey, $apiInstance);
    
            return $apiInstance;               
        }
        else 
        {
            $apiInstance = &ClassRegistry::getObject($classKey);
            
            return $apiInstance;
        }
     
    }
    
    /**
     * Extracts the plugin and api name from the $api variable which can either look like [Plugin]/[Api], or
     * [Api]
     *
     * @param string $api
     * @return array
     */    
    function extractApiAndPlugin($api)
    {
        if (strpos($api, '/')===false)
            $plugin = $api;
        else 
            list($plugin, $api) = explode('/', $api);    
            
        $plugin = Inflector::underscore($plugin);            
            
        return array($api, $plugin);
    }

    /**
     * This function calls a specific hook out of any plugin's hooks.php that matches $pluginFilter
     * The list of hooks.php files get's cached for a certain time depending on the value of DEBUG.
     * The 3rd argument &$caller has to be a reference to the caller/variable that get's affected by
     * the Hook.
     *
     * @param string $hook
     * @param string $pluginFilter
     * @param mixed $caller
     */
    function callHooks($hook, $pluginFilter = '.+', &$caller)
    {
        // pluginHooks contains an array of plugins that provide a hook File
        static $hookPlugins = array();
        
        if (empty($pluginFilter))
            $pluginFilter = '.+';
            
        $params = func_get_args();
        
        // Get rid of $hook, $pluginFilter and &$caller in our $params array
        array_shift($params);
        array_shift($params);
        array_shift($params);
            
    
        if (empty($hookPlugins))
        {
            $cachePath = 'hook_files';
                
            if (DEBUG==3)
                $cacheExpires = '+5 seconds';
            elseif (DEBUG==1 || DEBUG==2)
                $cacheExpires = '+60 seconds';
            else 
                $cacheExpires = '+24 hours';
                
            $hookFiles = cache($cachePath, null, $cacheExpires);
            
            if (empty($hookFiles))
            {
                uses('Folder');        
                $Folder =& new Folder(APP.'plugins');
                $hookFiles = $Folder->findRecursive('hooks.php');
                
                cache($cachePath, serialize($hookFiles));
            }        
            else
                $hookFiles = unserialize($hookFiles);
                        
            
            foreach ($hookFiles as $hookFile)
            {
                list($plugin) = explode(DS, substr($hookFile, strlen(APP.'plugins'.DS)));                
                require($hookFile);
                
                $hookPlugins[] = $plugin;
                
                if (preg_match('/'.$pluginFilter.'/iUs', $plugin))
                {
                    $hookFunction = $plugin.$hook.'Hook';
                    if (function_exists($hookFunction))
                    {
                        call_user_func_array($hookFunction, array_merge(array(&$caller), $params));
                    }
                }
            }        
        }
        else 
        {
            foreach ($hookPlugins as $plugin)
            {
                if (preg_match('/'.$pluginFilter.'/iUs', $plugin))
                {
                    $hookFunction = $plugin.$hook.'Hook';                    
                    if (function_exists($hookFunction))
                    {
                        call_user_func_array($hookFunction, array_merge(array(&$caller), $params));
                    }
                }                   
            }
        }
    }
}
?>
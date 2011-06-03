<?php

// Bootstraps PluginManager!
require_once APP.'plugins'.DS.'PluginManager'.DS.'config.php';
require_once APP.'plugins'.DS.'PluginManager'.DS.'plugin_manager.php';

PluginManager::uses('PluginManagerAppController');
PluginManager::uses('PluginManagerAppModel');
PluginManager::uses('PluginManagerApiController');

// As long as we are still in Alpha/Beta mode we'll need this, later it'll be optional
PluginManager::uses('Performance');

PluginManager::callHooks('urlRewrite', null, $from_url);

?>
<?php

class Log extends OlogAppModel {

  /**
   * The name of this model
   *
   * @var name
   */
  public $name ='Log';

  /**
   * The custom find types available on the model
   * 
   * @var array
   */
  public $_findMethods = array(
    'logs' => true,
    'log' => true
  );

  /**
   * Finds logs matching the given criteria.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('logs', $options)
   *
   *
   * Conditions can include
   * - logbook : String logbook name to search for
   * - tag : String tag name to search for
   * - search : String regex to search on
   * - {key} : {key} value to search on
   *
   * @param string $state 'before' or 'after'
   * @param array $query The query options passed to the Model::find() call
   * @param array $results The results from the call
   * @return array Either the modified query params or results depending on the
   *  value of the state parameter.
   */
  protected function _findLogs($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      $this->request['uri']['path'] = 'logs';
  
      $this->request['uri']['query'] = $query['conditions'];

      return $query;
      
    } else {

      return $results;
      
    }
    
  }

  /**
   * Finds log matching the given id.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('log', $options)
   *
   * $options can include the usual keys for 'conditions'
   *
   * Conditions can include
   * - id : an integer corresponding to the log id
   *
   * @param string $state 'before' or 'after'
   * @param array $query The query options passed to the Model::find() call
   * @param array $results The results from the call
   * @return array Either the modified query params or results depending on the
   *  value of the state parameter.
   */
  protected function _findLog($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      if (!isset($query['conditions']['id'])) {
        return false;
      }

      $this->request['uri']['path'] = 'logs/' . $query['conditions']['id'];

      return $query;

    } else {

      return $results;

    }

  }
  
  public function save($data = null, $validate = true, $fieldList = array()) {
    foreach($data['Log'] as $field=>$value){
      list($fields[], $values[]) = array($field, $value);
    }
    
    $db =& ConnectionManager::getDataSource($this->useDbConfig);
    if (!empty($data->id)) {
      $success = (bool)$db->update($this, $fields, $values);
    }
    if (!$db->create($this, $fields, $values)) {
      $success = false;
    } else {
      $success = true;
    }
    return $success;
  }

}
?>
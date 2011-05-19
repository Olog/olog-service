<?php

class Level extends OlogAppModel {

  /**
   * The name of this model
   *
   * @var name
   */
  public $name ='Level';

  /**
   * The custom find types available on the model
   * 
   * @var array
   */
  public $_findMethods = array(
    'levels' => true,
    'level' => true
  );

  /**
   * Finds levels matching the given criteria.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('levels', $options)
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
  protected function _findLevels($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      $this->request['uri']['path'] = 'levels';
  
      $this->request['uri']['query'] = $query['conditions'];

      return $query;
      
    } else {

      return $results;
      
    }
    
  }

  /**
   * Finds level matching the given id.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('level', $options)
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
  protected function _findLevel($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      if (!isset($query['conditions']['id'])) {
        return false;
      }

      $this->request['uri']['path'] = 'levels/' . $query['conditions']['id'];

      return $query;

    } else {

      return $results;

    }

  }

}
?>
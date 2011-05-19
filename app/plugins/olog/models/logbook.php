<?php

class Logbook extends OlogAppModel {

  /**
   * The name of this model
   *
   * @var name
   */
  public $name ='Logbook';

  /**
   * The custom find types available on the model
   * 
   * @var array
   */
  public $_findMethods = array(
    'logbooks' => true,
    'logbook' => true,
    'list' => true
  );

  /**
   * Finds logbooks matching the given criteria.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('logbooks', $options)
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
  protected function _findLogbooks($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      $this->request['uri']['path'] = 'logbooks';
  
      $this->request['uri']['query'] = $query['conditions'];

      return $query;
      
    } else {

      return $results;
      
    }
    
  }

  /**
   * Finds logbook matching the given id.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('logbook', $options)
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
  protected function _findLogbook($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      if (!isset($query['conditions']['id'])) {
        return false;
      }

      $this->request['uri']['path'] = 'logbooks/' . $query['conditions']['id'];

      return $query;

    } else {

      return $results;

    }

  }
  
  /**
   * Finds logbooks in list form.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('list', $options)
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
  public function _findList($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      $this->request['uri']['path'] = 'logbooks';
  
      $this->request['uri']['query'] = $query['conditions'];

      return $query;
      
    } else {

     foreach($results['logbooks']['logbook'] as $list){
        $listform[$list['name']] = $list['name']; 
      }

      return $listform;
      
    }
    
  }

}
?>
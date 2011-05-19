<?php

class Tag extends OlogAppModel {

  /**
   * The name of this model
   *
   * @var name
   */
  public $name ='Tag';

  /**
   * The custom find types available on the model
   * 
   * @var array
   */
  public $_findMethods = array(
    'tags' => true,
    'tag' => true,
    'list' => true
  );

  /**
   * Finds tags matching the given criteria.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('tags', $options)
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
  protected function _findTags($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      $this->request['uri']['path'] = 'tags';
  
      $this->request['uri']['query'] = $query['conditions'];

      return $query;
      
    } else {

      return $results;
      
    }
    
  }

  /**
   * Finds tag matching the given id.
   *
   * Calls the Olog web service
   *
   * Called using Model::find('tag', $options)
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
  protected function _findTag($state, $query = array(), $results = array()) {

    if ($state == 'before') {

      if (!isset($query['conditions']['id'])) {
        return false;
      }

      $this->request['uri']['path'] = 'tags/' . $query['conditions']['id'];

      return $query;

    } else {

      return $results;

    }

  }
  
  /**
   * Finds tags in list form.
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

      $this->request['uri']['path'] = 'tags';

      return $query;
      
    } else {
      foreach($results['tags']['tag'] as $list){
        $listform[$list['name']] =$list['name']; 
      }

      return $listform;
      
    }
    
  }

}
?>
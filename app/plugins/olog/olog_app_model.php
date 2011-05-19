<?php

class OlogAppModel extends PluginManagerAppModel {

  /**
   * The datasource all models in this plugin use
   * 
   * @var string
   */
  public $useDbConfig = 'olog';

  /**
   * The models in the plugin get data from the web service, so they don't need
   * a table.
   *
   * @var string
   */
  public $useTable = false;

  /**
   * The methods in the models affect this request property which is then used
   * in the datasource. The request property value set in each of the methods is
   * in the format of HttpSocket::request.
   *
   * @var array
   */
  public $request = array();

  /**
   * Since the webservice call returns the results in the current page of the
   * result set and the total number of results in the whole results set, we
   * need custom paginate and paginateCount methods, whereby the call to the
   * web service is made in the paginateCount method, the results stored and the
   * total results returned, then the actual results are returned from the
   * paginate method. This way the call to the web service is only made once.
   * However, in order to do this, we need to know the page and limit params in
   * the paginateCount method. So these should be set in this Model::paginate
   * property in the controller, before calling Controller::paginate().
   * 
   * @var array
   */
  public $paginate = array();

  /**
   * Temporarily stores the results after being fetched during the paginateCount
   * method, before returning in the paginate method.
   *
   * @var array
   */
  protected $_results = null;

  /**
   * Overloads the Model::find() method. Resets request array in between calls
   * to Model::find()
   * 
   * @param string $type
   * @param array $options
   */
  public function find($type, $options = array()) {
    $this->request = array();
    return parent::find($type, $options);
  }

  /**
   * Called by Controller::paginate(). Calls the custom find type. Stores the
   * results for later returning in the paginate() method. Returns the total
   * number of results from the full result set.
   *
   * @param array $conditions
   * @param integer $recursive
   * @param array $extra
   * @return integer The number of items in the full result set
   */
  public function paginateCount($conditions, $recursive = 1, $extra = array()) {
    $this->paginate['conditions']['limit']=$this->paginate['limit'];
    $this->paginate['conditions']['page']=$this->paginate['page'];
    
    $response = $this->find($this->paginate[0], $this->paginate);
    $total = $this->find($this->paginate[0]);
    $this->_results = $response;
    return count($total['logs']['log']);
  }

  /**
   * Returns the results of the call to the web service fetched in the
   * paginateCount() method above.
   * 
   * @param mixed $conditions
   * @param mixed $fields
   * @param mixed $order
   * @param integer $limit
   * @param integer $page
   * @param integer $recursive
   * @param array $extra
   * @return array The results of the call to the web service
   */
  public function paginate($conditions, $fields = null, $order = null, $limit = null, $page = 1, $recursive = null, $extra = array()) {
    return $this->_results;
  }

}
?>
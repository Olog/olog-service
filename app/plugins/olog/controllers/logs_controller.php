<?php

class LogsController extends OlogAppController {

    var $name = 'Logs';

    function beforeFilter() {
        parent::beforeFilter();
        $this->LogAuth->allowedActions = array('index', 'add', 'view');
    }

    function index() {
        $this->data['Log'] = $this->passedArgs;
        $this->paginate['Log'] = array(
            'logs',
            'conditions' => $this->passedArgs,
        );
        $this->set('logs', $this->paginate('Log'));

        $levels = array("Info" => "Info",
            "Problem" => "Problem",
            "Request" => "Request",
            "Suggestion" => "Suggestion",
            "Urgent" => "Urgent");

        Controller::loadModel('Logbook');
        $logbooks = $this->Logbook->find('list');

        Controller::loadModel('Tag');
        $tags = $this->Tag->find('list');

        $this->set('logbooks', $logbooks);
        $this->set(compact('tags', 'levels'));

        $this->set('session', $this->Session);
        $this->set('base', $this->base);
    }

    /** Todo: implement a threaded view * */
    function threaded() {
        $this->Log->recursive = 0;
        $this->Log->order = array('Log.created DESC');
        $this->paginate = array('fields' =>
            array('id', 'created',
                'User.name', 'User.id',
                'Level.name', 'Level.id',
                'subject', 'detail', 'ParentLog.id'));
        $this->set('logs', $this->paginate());
    }

    function view($id = null) {
        if (!$id) {
            $this->Session->setFlash(__('Invalid log', true));
            $this->redirect(array('action' => 'index'));
        }
        $this->set('log', $this->Log->find('log', array('conditions' => array('id' => $id))));
        //$this->set('log', $this->Log->read());
    }

    function add() {
        if (!empty($this->data)) {

            if (isset($this->data['log']['username']) && isset($this->data['log']['password'])) {
                $this->Log->request['auth']['user'] = $this->data['log']['username'];
                $this->Log->request['auth']['pass'] = $this->data['log']['password'];
            }

            //if ($this->Session->check('Auth.User.id')) {
            $saved = $this->Log->save($this->data);
            // save is called in uploader plugin component against $this->data
            //if(!$this->uploadFiles($this->Log->id)) $saved=false;
            if ($saved) {
                $this->Session->setFlash(__('The log has been saved', true));
                $this->redirect(array('action' => 'index'));
            } else {
                /** Todo quick fix for tag validation error not showing up * */
                $print_error = "";
                foreach ($this->Log->validationErrors as $errorKey => $error) {
                    $print_error .= "For input " . $errorKey . ": " . $error . '<br>';
                }
                $this->Session->setFlash(__($print_error . 'The log could not be saved. Please, try again.', true));
                $this->redirect(array('action' => 'index'));
            }
            //} else {
            //	$this->Session->setFlash(__('The log could not be saved. Please, try again.', true));
            //}
        }
        $levels = array("Info" => "Info",
            "Problem" => "Problem",
            "Request" => "Request",
            "Suggestion" => "Suggestion",
            "Urgent" => "Urgent");
        Controller::loadModel('Tag');
        $tags = $this->Tag->find('list');
        Controller::loadModel('Logbook');
        $logbooks = $this->Logbook->find('list');
        //$uploads = $this->Log->Upload->find('list');
        $this->set(compact('levels', 'tags', 'logbooks'));
    }

    function edit($id = null) {
        if (!$id && empty($this->data)) {
            $this->Session->setFlash(__('Invalid log', true));
            $this->redirect(array('action' => 'index'));
        }
        if (!empty($this->data)) {
//			if ($this->Session->check('Auth.User.id')) {
            if ($this->Log->save($this->data)) {
                $this->Session->setFlash(__('The log has been saved', true));
                $this->redirect(array('action' => 'index'));
            } else {
                $this->Session->setFlash(__('The log could not be saved. Please, try again.', true));
            }
//			} else {
//					$this->Session->setFlash(__('The log could not be saved. Please, try again.', true));
//			}
        }
        if (empty($this->data)) {
            $this->data = $this->Log->find('log', array('conditions' => array('id' => $id)));
        }
        //$uploads = $this->Log->Upload->find('all', array(
        //				'conditions' => array('log_id'=>$id),
        //				'fields' => array('Upload.name','Upload.store')
        //			      ));
        Controller::loadModel('Tag');
        $tags = $this->Tag->find('list');
        Controller::loadModel('Logbook');
        $logbooks = $this->Logbook->find('list');
        //$uploads = $this->Log->Upload->find('list');
        $this->set(compact('levels', 'tags', 'logbooks'));
        //$this->set(compact('users', 'levels', 'parentLogs', 'logbooks', 'tags', 'uploads', 'webdavdir'));
    }

    function delete($id = null) {
        if (!$id) {
            $this->Session->setFlash(__('Invalid id for log', true));
            $this->redirect(array('action' => 'index'));
        }
        if ($this->Log->delete($id)) {
            $this->Session->setFlash(__('Log deleted', true));
            $this->redirect(array('action' => 'index'));
        }
        $this->Session->setFlash(__('Log was not deleted', true));
        $this->redirect(array('action' => 'index'));
    }

    function timespanChange($newTimeSpan = null) {
        $startDate = null;
        $endDate = date('U');

        switch ($newTimeSpan) {
            case 0: // Last day
                $startDate = mktime(0, 0, 0, date('m'), date('d') - 1, date('y'));
                break;
            case 1: // Last 3 Days
                $startDate = mktime(0, 0, 0, date('m'), date('d') - 3, date('y'));
                break;
            case 2: // Last week
                $startDate = mktime(0, 0, 0, date('m'), date('d') - 7, date('y'));
                break;
            case 3: // Last month
                $startDate = mktime(0, 0, 0, date('m') - 1, date('d'), date('y'));
                break;
            case 4: // Last 3 Months
                $startDate = mktime(0, 0, 0, date('m') - 3, date('d'), date('y'));
                break;
            case 5: // Last 6 Months
                $startDate = mktime(0, 0, 0, date('m') - 6, date('d'), date('y'));
                break;
            case 6: // Last year
                $startDate = mktime(0, 0, 0, date('m'), date('d'), date('y') - 1);
                break;
        }

        $this->redirect('/olog/logs/index/start:' . $startDate . '/end:' . $endDate);
    }

    private function uploadFiles($id) {
        $success = true;
        $this->data['Upload']['log_id'] = $id;
        if ($this->FileUpload->hasFile) {
            $directory = WWW_ROOT . 'files' . DS . $id;
            if (!(file_exists($directory)))
                mkdir($directory);
            $this->FileUpload->Uploader->options['uploadDir'] = $directory;
            $this->FileUpload->uploadDir('files' . DS . $id);
            $this->FileUpload->processAllFiles();
            if (!$this->FileUpload->success) {
                $success = false;
            }
        }
        return $success;
    }

}

?>
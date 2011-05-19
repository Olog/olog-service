<?php
class LogsController extends OlogAppController {

	var $name = 'Logs';
	
	function index() {

		//if (!empty($this->passedArgs)) {
                  $this->data['Log'] = $this->passedArgs;
                  $this->paginate['Log'] = array(
                    'logs',
                    'conditions' => $this->passedArgs,
                  );
                //}
                $this->set('logs', $this->paginate('Log'));

		$logbooks = array('Operations','SRF');
		$this->set('logbooks', $logbooks);
	}
	function tag($id=null) {
		//$this->Log->Behaviors->attach('Containable');
		// todo: this doesn't return the correct xml
		// should maybe use bindModel hasAndBelongsToMany
		$this->Log->recursive = 0;
		$this->Log->order = array('Log.created DESC');
		$params['fields'] = array('Log.id','Log.created','Log.modified',
					'Log.subject','Log.detail',
					'Level.id','Level.name',
					'User.id','User.name',
					//'ChildLog.id','ChildLog.modified',
					'LogsTag.id','LogsTag.log_id','LogsTag.tag_id','LogsTag.status'
		);
		
		//$params['contain'] = array('User' => array('fields'=>array('id','name')),
		//			   'Level' => array('fields'=>array('id','name')),
		//			   'ChildLog' => array('fields'=>array('id','modified')),
		//			   'Tag'=> array('fields'=>array('id','name')));
		$params['conditions'] = array('Log.status_id' => null);
		
		//$params['fields'] = array('Log.id','Log.created',
		//			'User.id','User.name',
		//			'Level.id','Level.name',
		//			'Log.subject','Log.detail','ParentLog.id');
		if($id) {
			$params['conditions'] = array_merge($params['conditions'],array('LogsTag.tag_id'=> $id));
		}
		$this->Log->bindModel(array('hasOne' => array('LogsTag')),false);
		$this->paginate = $params;
		$this->set('logs', $this->paginate());
		
		Controller::loadModel('Tag');
		$logbooks = $this->Tag->find('list', array(
					'conditions' => array('book'=>1)
					));
		$this->set('logbooks', $logbooks);
	}
	/** Todo: implement a threaded view **/
	function threaded() {
		$this->Log->recursive = 0;
		$this->Log->order = array('Log.created DESC');
		$this->paginate = array('fields' =>
				array('id','created',
				      'User.name','User.id',
				      'Level.name','Level.id',
				      'subject','detail','ParentLog.id'));
		$this->set('logs', $this->paginate());
	}
	
	function view($id = null) {
		if (!$id) {
		  $this->Session->setFlash(__('Invalid log', true));
		  $this->redirect(array('action' => 'index'));

		}
                $this->set('log', $this->Log->find('log', array('conditions' => array('id' => $id))));
		//$this->set('log', $this->Log->read());
                //pr($backtrace);
	}

	function add() {
		if (!empty($this->data)) {

			//if ($this->Session->check('Auth.User.id')) {
				$saved=$this->Log->save($this->data);
				// save is called in uploader plugin component against $this->data
				//if(!$this->uploadFiles($this->Log->id)) $saved=false;
				if ($saved) {
					$this->Session->setFlash(__('The log has been saved', true));
					$this->redirect(array('action' => 'index'));							
				} else {
					/** Todo quick fix for tag validation error not showing up **/
					$print_error = "";
					foreach ($this->Log->validationErrors as $errorKey => $error) {
						$print_error .= "For input ".$errorKey.": ".$error.'<br>';
					}
					$this->Session->setFlash(__($print_error.'The log could not be saved. Please, try again.', true));
				}
			//} else {
			//	$this->Session->setFlash(__('The log could not be saved. Please, try again.', true));
			//}
		}
		$levels = array("Info"=>"Info",
                                "Problem"=>"Problem",
                                "Request"=>"Request",
                                "Suggestion"=>"Suggestion",
                                "Uregent"=>"Urgent");
		Controller::loadModel('Tag');
                $tags = $this->Tag->find('list');
		Controller::loadModel('Logbook');
		$logbooks = $this->Logbook->find('list');
		//$uploads = $this->Log->Upload->find('list');
		$this->set(compact('levels', 'tags', 'logbooks'));
	}

	function edit($id = null) {
		$result = array('response' => 'failed');
		if (!$id && empty($this->data)) {
			if ($this->RequestHandler->isXml()){
				$result = array('response' => 'failed');
			} else {
				$this->Session->setFlash(__('Invalid log', true));
				$this->redirect(array('action' => 'index'));
			}
		}
		if (!empty($this->data)) {
			if ($this->Session->check('Auth.User.id')) {
				if ($this->RequestHandler->isXml()) {
					$this->data = array_merge($this->Log->read(null, $id),$this->data);
				}
				$editedCreated = $this->Log->read('created',$id);
				$this->Log->create();
				if (is_array($this->data['Tag']['Tag'])&&is_array($this->data['Log']['Logbook'])) {
					$this->data['Tag']['Tag'] = array_merge($this->data['Tag']['Tag'], $this->data['Log']['Logbook']);
				} else {
					$this->data['Tag']['Tag'] = $this->data['Log']['Logbook'];
				}
				unset($this->data['Log']['id']);
				$this->data['Log']['user_id'] = $this->Session->read('Auth.User.id');
				$this->data['Log']['source'] = $this->RequestHandler->getClientIp();
				$this->data['Log']['md5recent'] = $this->md5ComputeRecent($this->data);
				$this->data['Log']['md5entry'] = $this->md5ComputeEntry($this->data);
				$this->data['Log']['created'] = $editedCreated['Log']['created'];
				if ($this->Log->save($this->data)) {
					// get children of $id and change parent_id to $this->Log->id
					// read $id, and change parent_id to $this->Log->id
					// read $id, and change status_id to 1 (edit)
					$newParent = $this->Log->id;
					$this->Log->id = $id;
					$modified = $this->Log->read('modified',$id);
					$this->Log->save(array(
							       'parent_id'=>$newParent,
							       'status_id'=>2,
							       'modified'=>$modified['Log']['modified']
					));
					foreach($this->Log->children($id,true) as $kids) {
						$this->Log->id = $kids['Log']['id'];
						$modified = $this->Log->read('modified',$kids['Log']['id']);
						$this->Log->save(array(
								       'parent_id'=>$newParent,
								       'status_id'=>1,
								       'modified'=>$modified['Log']['modified']
						));
					}
					if ($this->RequestHandler->isXml()){
						$result = array('response' => 'success', 'id' => $this->Log->id);
					} else {
						$this->Session->setFlash(__('The log has been saved', true));
						$this->redirect(array('action' => 'index'));
					}
					$this->sendEmail($this->Log->id);
				} else {
					if ($this->RequestHandler->isXml()){
						$result = array('response' => 'failed');
					} else {
						$this->Session->setFlash(__('The log could not be saved. Please, try again.', true));
					}
				}
			} else {
				if ($this->RequestHandler->isXml()){
					$result = array('response' => 'failed');
				} else {
					$this->Session->setFlash(__('The log could not be saved. Please, try again.', true));
				}
			}
		}
		if (empty($this->data)) {
			$this->data = $this->Log->read(null, $id);
			// Todo: Maybe this logbook as a tag was a bad idea
			// need to separate it!
			//print_r($this->data);
			
			foreach($this->data['Tag'] as $tag) {
				if ($tag['book']==1)
					$this->data['Log']['Logbook'][]=$tag['id'];
			}
		}
		if ($this->RequestHandler->isXml()){
			$this->set(compact('result'));
		} else {
			$webdavdir = $this->Sabredav->server->getBaseUri();
			$uploads = $this->Log->Upload->find('all', array(
							'conditions' => array('log_id'=>$id),
							'fields' => array('Upload.name','Upload.store')
						      ));
			$users = $this->Log->User->find('list');
			$levels = $this->Log->Level->find('list');
			$parentLogs = $this->Log->ParentLog->find('list');
			$tags = $this->Log->Tag->find('list', array(
							'conditions' => array('book'=>0)
						      ));
			$logbooks = $this->Log->Tag->find('list', array(
							'conditions' => array('book'=>1)
						      ));
			$this->set(compact('users', 'levels', 'parentLogs', 'logbooks', 'tags', 'uploads', 'webdavdir'));
		}
	}

	function delete($id = null) {
		$result = array('response' => 'failed');
		if (!$id) {
			if ($this->_isRest()){
				$result = array('response' => 'failed');
			} else {
				$this->Session->setFlash(__('Invalid id for log', true));
				$this->redirect(array('action'=>'index'));
			}
		}
		if (empty($this->data)) {
			$this->data = $this->Log->read(null, $id);
		}
		$this->data['Log']['status_id'] = 3;
		if ($this->Log->save($this->data)) {
			if ($this->_isRest()){
				$result = array('response' => 'success');
			} else {
				$this->Session->setFlash(__('Log deleted', true));
				$this->redirect(array('action'=>'index'));
			}
		}
		if ($this->_isRest()){
			$this->set(compact('result'));
		} else {
			$this->Session->setFlash(__('Log was not deleted', true));
			$this->redirect(array('action' => 'index'));
		}
	}
	/** moving to own controller ?**/
	function search() {
		if(!empty($this->data)) {
			$documents = $this->lucene->query($this->data['Log']['searchkey']);
			function parseLucene ($array) {
					return $array->log_id;
			}
			$id = array_map('parseLucene',$documents);
			$searchkey = $this->Session->read('Log.searchkey');
			if(empty($searchkey)) {
				$this->Session->write('Log.searchkey', $this->data['Log']['searchkey']);
				$this->Session->write('Log.ids', $id);	
			}
			if($this->data['Log']['searchkey'] != $this->Session->read('Log.searchkey')) {
				$this->Session->delete('Log.searchkey');
				$this->Session->delete('Log.ids');
				$this->Session->write('Log.searchkey', $this->data['Log']['searchkey']);
				$this->Session->write('Log.ids', $id);	
			}
		}
		if ($this->Session->read('Log.searchkey') != null){
			$this->paginate = array(
				'conditions' => array('Log.id' => $this->Session->read('Log.ids')),
				'limit' => 10
			);
			$this->set('logs',$this->paginate('Log'));
		}
	}
	
	private function md5ComputeEntry($data) {
		$details = $data['Log']['detail'];
		$detailsLinesArray = preg_split('/\n/',$details);
		$detailsLine = '';
    
		foreach($detailsLinesArray as $line) {           
			$detailsLine .= "details:" . html_entity_decode($line) . "\n";
		}
    
		$md5_recent = $data['Log']['md5recent'];
		$explodeRecentArray = explode("\n", $md5_recent);

		$explodeRecent = '';
		foreach($explodeRecentArray as $line) {
			if ( ($line == "") || ($line == "\n") ) continue;   
			$explodeRecent .= "md5recent:" . $line . "\n";
		}        
       
		$entry = "id:"         . $this->Log->id           . "\n" .
		//	"created:"     . $data['Log']['created']  . "\n" .
			"source:"      . $data['Log']['source']   . "\n" .
			"user_id:"     . $data['Log']['user_id']  . "\n" .
			"level_id:"    . $data['Log']['level_id'] . "\n" .
			"subject:"     . $data['Log']['subject']  . "\n" .
			$detailsLine   . "\n" .		 
			$explodeRecent;  
		
		$md5_entry = md5($entry);   
    
		return $md5_entry;
	}
	
	private function md5ComputeRecent($data) {
		$md5_recent = '';
		$params = array('order' => array('Log.id DESC'),
				'limit' => 10,
				'fields' => array('Log.id','Log.created','Log.md5entry'));
		$entries = $this->Log->find('all',$params);

		foreach($entries as $entry){
			$md5_entry = $entry['Log']['md5entry'];
			if (empty($md5_entry)) continue;
			$md5_recent .= $entry['Log']['id']." ".$entry['Log']['md5entry']."\n";
		}
		return $md5_recent;
	}
	
	private function sendEmail($id) {
		$this->Log->Behaviors->attach('Containable');
		$logSubscription = $this->Log->find('all', array(
			'contain'=>array(
				'Tag' => array(
					'Subscription' => array(
							'Level' => array('id','name'),
							'fields' => array('id','email')
					),
					'fields' => array('id','name')
				)
			),
			'conditions' => array('Log.id' => $id),
			'fields' => array('id','level_id')
		));
		$logLevel = $logSubscription[0]['Log']['level_id'];
		foreach ($logSubscription[0]['Tag'] as $tag) {
			foreach($tag['Subscription'] as $subscription) {
				if ($logLevel >= $subscription['level_id']) {
					$emailAddresses[]=$subscription['email'];
				}
			}
		}
		if(isset($emailAddresses)) {
			$Log = $this->Log->read(null,$id);
			
			$this->Email->delivery = 'debug';
			$this->Email->to = $emailAddresses;
			$this->Email->subject = 'eLog - '.$Log['Log']['subject'];
			$this->Email->replyTo = 'support@als.lbl.gov';
			$this->Email->from = 'eLog <support@als.lbl.gov>';
			$this->Email->template = 'new_log_notification';
			//Send as 'html', 'text' or 'both' (default is 'text')
			$this->Email->sendAs = 'text';
			$this->set('log', $Log);
			$this->Email->smtpOptions =  Configure::read('Smtp.options');
			//$this->Email->delivery = 'smtp';
			//Check for SMTP errors
			$this->set('smtp-errors', $this->Email->smtpError);
			//Do not pass any args to send()
			$this->Email->send();
		}
	}
	
	private function multiSave(&$model, $values, $fields = null) { 
		$this->db =& $model->getDataSource(); 
		$this->db->insertMulti($model->table, $fields, $values); 
	}
	
	private function restFormater($logs) {
		// Test for single log or array of logs
		if (isset($logs['Log'])){
			$logs_temp[0]=$logs;
			unset($logs);
			$logs=$logs_temp;
			unset($logs_temp);
		}
		foreach ($logs as $index=>$log){
			$restFormat[$index]['id']=$log['Log']['id'];
			$restFormat[$index]['created']=$log['Log']['created'];
			$restFormat[$index]['modified']=$log['Log']['modified'];
			$restFormat[$index]['subject']=$log['Log']['subject'];
			$restFormat[$index]['detail']=$log['Log']['detail'];
			$restFormat[$index]['user']=$log['User']['name'];
			$restFormat[$index]['level']['name']=$log['Level']['name'];
			foreach ($log['Tag'] as $tag_index=>$tag){
				($tag['book']==1)?$logbooks[]=$tag:$tags[]=$tag;
			}
			if(!empty($logbooks)){
				foreach ($logbooks as $logbook_index=>$logbook){
					$restFormat[$index]['logbooks'][$logbook_index]['name']=$logbook['name'];
				}
			}
			if(!empty($tags)){
				foreach ($tags as $tag_index=>$tag){
					$restFormat[$index]['tags'][$tag_index]['name']=$tag['name'];		
					$restFormat[$index]['tags'][$tag_index]['status']=$tag['LogsTag']['status'];					
					$restFormat[$index]['tags'][$tag_index]['created']=$tag['LogsTag']['created'];				
				}
			}
		}
		return $restFormat;	
	}
	
	private function restPreparePostData($data){
		Controller::loadModel('Tag');
		Controller::loadModel('Level');
		
		// Test for single log or array of logs
		if (!$this->_numeric($data['Logs']['Log'])){
			$data_temp['Logs']['Log'][0]=$data['Logs']['Log'];
			unset($data);
			$data=$data_temp;
			unset($data_temp);
		}
		foreach ($data['Logs']['Log'] as $index=>$log){
			// Translate Logbook to Tag
			if (isset($log['Logbook'])) {
				if (is_array($log['Tags']['Tag'])&&is_array($log['Logbook'])) {
					$log['Tags']['Tag'] = array_merge($log['Tags']['Tag'], $log['Logbook']);
				} else {
					$log['Tags']['Tag'] = $log['Logbook'];
				}
			}
			// XML Translation
			$dataFormat[$index]['Log']['subject'] = $log['subject'];
			$dataFormat[$index]['Log']['detail'] = $log['detail'];
			(!empty($log['level']['name']))?$name=$log['level']['name']:$name=$log['level'];
			$findLevelId = $this->Level->find('list', array(
					'conditions' => array('name'=>$name)
			));
			$dataFormat[$index]['Log']['level_id'] = key($findLevelId);
			if (!$this->_numeric($log['Tags']['Tag'])){
				$log_temp['Tags']['Tag'][0]=$log['Tags']['Tag'];
				unset($log);
				$log=$log_temp;
				unset($log_temp);
			}
			foreach ($log['Tags']['Tag'] as $tag_index=>$tag){
				$findTagId = $this->Tag->find('list', array(
					'conditions' => array('name'=>$tag['name'])
				));
				$dataFormat[$index]['Tag']['Tag'][$tag_index] = key($findTagId);		
				//$dataFormat[$index]['Tag']['LogsTag'][$tag_index]['status'] = $tag['status'];					
			}
			// Override for mass add to migrate ***delete after data migration
			if(isset($log['override'])){
				$dataFormat[$index]['Log']['User']['name'] = $log['user'];
				$dataFormat[$index]['Log']['created'] = $log['created'];
			}else{
				$dataFormat[$index]['Log']['user_id'] = $this->Session->read('Auth.User.id');
				$dataFormat[$index]['Log']['source'] = $this->RequestHandler->getClientIp();
			}
		}
		return $dataFormat;
	}
	
	private function preparePostData($data) {
		// Translate Logbook to Tag
		if (is_array($data['Tag']['Tag'])&&is_array($data['Log']['Logbook'])) {
			$data['Tag']['Tag'] = array_merge($data['Tag']['Tag'], $data['Log']['Logbook']);
		} else {
			$data['Tag']['Tag'] = $data['Log']['Logbook'];
		}
		$data['Log']['user_id'] = $this->Session->read('Auth.User.id');
		$data['Log']['source'] = $this->RequestHandler->getClientIp();
		//$data['Log']['md5recent'] = $this->md5ComputeRecent($data);
		//$data['Log']['md5entry'] = $this->md5ComputeEntry($data);
		
		return $data;
	}
	
	private function uploadFiles($id) {
		$success = true;
		$this->data['Upload']['log_id']=$id;
		if ($this->FileUpload->hasFile) {
			$directory = WWW_ROOT.'files'.DS.$id;
			if(!(file_exists($directory))) mkdir($directory);
			$this->FileUpload->Uploader->options['uploadDir'] = $directory;
			$this->FileUpload->uploadDir('files'.DS.$id);
			$this->FileUpload->processAllFiles();
			if(!$this->FileUpload->success){
				$success = false;
			}
		}
		return $success;
	}
}
?>
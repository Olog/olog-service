<?php
class UsersController extends PluginManagerAppController {
	var $name = 'Users';
	var $helpers = array('Html', 'Form', 'Session' );
	var $components = array (
			'Rest.Rest' => array(
				'debug' => 0,
				'view' => array(
					'extract' => array('user.User' => 'users.0'),
				),
				'delete' => array(
					'extract' => array('result'),
				),
				'index' => array(
					'extract' => array('users'),
				),
				'add' => array(
					'extract' => array('result'),
				)
			),
	);
	
	protected function _isRest() {
            return !empty($this->Rest) && is_object($this->Rest) && $this->Rest->isActive();
        }
	
	function beforeFilter() {
		parent::beforeFilter(); 
		$this->LdapAuth->allowedActions = array('index', 'view', 'logout', 'login');
		if (!$this->LdapAuth->user() && $this->Rest->isActive()) {
			// Try to login user via REST
			$this->LdapAuth->autoRedirect = false;
			$credentials = $this->Rest->credentials();
		    	$data = array(
	    		    $this->LdapAuth->userModel => array(
    			        'username' => $credentials['username'],
				'password' => $credentials['password'],
			        ),
			    );
		    	$data = $this->LdapAuth->hashPasswords($data);
			if (!$this->LdapAuth->login($data,$credentials['password'])) {
    			    $msg = sprintf('Unable to log you in with the supplied credentials. ');
			    return $this->Rest->abort(array('status' => '403', 'error' => $msg));
			}
			
		}
	}
	
	function login() {
		if ($this->Session->read('LdapAuth.User')) {
			$this->Session->setFlash('You are logged in!');
			$this->redirect('/', null, false);
		}
	}

	function logout() {
		$this->Session->setFlash('Good-Bye');
		$this->redirect($this->LdapAuth->logout());
	}
	
	function index() {
		$this->User->recursive = 0;
		if ($this->_isRest()){
			$this->set('users', $this->User->find('all'));			
		} else {
			$this->set('users', $this->paginate());
		}
	}

	function view($id = null) {
		if (!$id) {
			$this->Session->setFlash(__('Invalid user', true));
			$this->redirect(array('action' => 'index'));
		}
		$this->set('user', $this->User->read(null, $id));
	}

	function add() {
		$result = array('response' => 'failed');
		if (!empty($this->data)) {
			$this->User->create();
			if ($this->User->save($this->data)) {
				if ($this->RequestHandler->isXml()){
					$result = array('response' => 'success', 'id' => $this->User->id);
				} else {
					$this->Session->setFlash(__('The user has been saved', true));
					$this->redirect(array('action' => 'index'));
				}
			} else {
				if ($this->RequestHandler->isXml()){
					$result = array('response' => 'failed');
				} else {
					$this->Session->setFlash(__('The user could not be saved. Please, try again.', true));
				}
			}
		}
		$groups = $this->User->Group->find('list');
		if ($this->RequestHandler->isXml()){
			$this->set(compact('result'));
		} else {
			$this->set(compact('groups'));
		}
	}

	function edit($id = null) {
		if (!$id && empty($this->data)) {
			$this->Session->setFlash(__('Invalid user', true));
			$this->redirect(array('action' => 'index'));
		}
		if (!empty($this->data)) {
			if ($this->User->save($this->data)) {
				$this->Session->setFlash(__('The user has been saved', true));
				$this->redirect(array('action' => 'index'));
			} else {
				$this->Session->setFlash(__('The user could not be saved. Please, try again.', true));
			}
		}
		if (empty($this->data)) {
			$this->data = $this->User->read(null, $id);
		}
		$groups = $this->User->Group->find('list');
		$this->set(compact('groups'));
	}

	function delete($id = null) {
		if (!$id) {
			$this->Session->setFlash(__('Invalid id for user', true));
			$this->redirect(array('action'=>'index'));
		}
		if ($this->User->delete($id)) {
			$this->Session->setFlash(__('User deleted', true));
			$this->redirect(array('action'=>'index'));
		}
		$this->Session->setFlash(__('User was not deleted', true));
		$this->redirect(array('action' => 'index'));
	}
}
?>
<?php
App::import('Component', 'Auth');

class LdapAuthComponent extends AuthComponent {
	var $ldap = null;
        var $users = null;
        
        /**
        * Initialize method ensures Auth methods remain working as described in the book.
        */
//        function initialize(&$controller) {
//        parent::initialize($controller);
//        $controller->Auth =& $this;
//        }

	/**
	 * Main execution method.  Handles redirecting of invalid users, and processing
	 * of login form data.
	 *
	 * @param object $controller A reference to the instantiating controller object
	 * @return boolean
	 * @access public
	 */
	function startup(&$controller) {
               
		$methods = array_flip($controller->methods);
		$isErrorOrTests = (
			strtolower($controller->name) == 'cakeerror' ||
			(strtolower($controller->name) == 'tests' && Configure::read() > 0)
		);
		if ($isErrorOrTests) {
			return true;
		}

		$isMissingAction = (
			$controller->scaffold === false &&
			!isset($methods[strtolower($controller->params['action'])])
		);

		if ($isMissingAction) {
			return true;
		}

		if (!$this->__setDefaults()) {
			return false;
		}
       
		$url = '';

		if (isset($controller->params['url']['url'])) {
			$url = $controller->params['url']['url'];
		}
		$url = Router::normalize($url);
		$loginAction = Router::normalize($this->loginAction);

		$isAllowed = (
			$this->allowedActions == array('*') ||
			in_array($controller->params['action'], $this->allowedActions)
		);

		if ($loginAction != $url && $isAllowed) {
			return true;
		}
		$this->ldap = $this->getModel('LdapUser');
                $this->users = $this->getModel('User');

		if ($loginAction == $url) {
			if (empty($controller->data) || !isset($controller->data[$this->userModel])) {
				if (!$this->Session->check('Auth.redirect') && env('HTTP_REFERER')) {
					$this->Session->write('Auth.redirect', $controller->referer(null, true));
				}
				return false;
			}
			$isValid = !empty($controller->data[$this->userModel][$this->fields['username']]);

			if ($isValid) {
				$username = $controller->data[$this->userModel][$this->fields['username']];
				$password = $controller->data[$this->userModel][$this->fields['password']];
                                $data = array(
                                        $this->userModel => array(
                                                'username' => $username,
                                                'password' => $password,
                                        ),
                                );
                                $data = $this->hashPasswords($data);
                                
				if ($this->login($data,$password)) {

                                        if ($this->autoRedirect) {
						$controller->redirect($this->redirect(), null, true);
					}
					return true;
				}
			}
			$this->Session->setFlash($this->loginError, 'default', array(), 'auth');
			$controller->data[$this->userModel][$this->fields['password']] = null;
			return false;
		} else {
			if (!$this->user()) {
				if (!$this->RequestHandler->isAjax()) {
					$this->Session->setFlash($this->authError, 'default', array(), 'auth');
					$this->Session->write('Auth.redirect', $url);
					$controller->redirect($loginAction);
					return false;
				} elseif (!empty($this->ajaxLogin)) {
					$controller->viewPath = 'elements';
					echo $controller->render($this->ajaxLogin, $this->RequestHandler->ajaxLayout);
					$this->_stop();
					return false;
				} else {
					$controller->redirect(null, 403);
				}
			}
		}


		if (!$this->authorize) {
			return true;
		}

		extract($this->__authType());
		switch ($type) {
			case 'controller':
				$this->object =& $controller;
			break;
			case 'crud':
			case 'actions':
				if (isset($controller->Acl)) {
					$this->Acl =& $controller->Acl;
				} else {
					$err = 'Could not find AclComponent. Please include Acl in ';
					$err .= 'Controller::$components.';
					trigger_error(__($err, true), E_USER_WARNING);
				}
			break;
			case 'model':
				if (!isset($object)) {
					$hasModel = (
						isset($controller->{$controller->modelClass}) &&
						is_object($controller->{$controller->modelClass})
					);
					$isUses = (
						!empty($controller->uses) && isset($controller->{$controller->uses[0]}) &&
						is_object($controller->{$controller->uses[0]})
					);

					if ($hasModel) {
						$object = $controller->modelClass;
					} elseif ($isUses) {
						$object = $controller->uses[0];
					}
				}
				$type = array('model' => $object);
			break;
		}

		if ($this->isAuthorized($type)) {
			return true;
		}

		$this->Session->setFlash($this->authError, 'default', array(), 'auth');
		$controller->redirect($controller->referer(), null, true);
		return false;
	}


	function login($data,$clear=null){
                if(is_null($this->users))
                        $this->users = $this->getModel('User');
                $uid = $data[$this->userModel]['username'];
                $password = $clear;
		$this->__setDefaults();
		$this->_loggedIn = false;
                $dbConfig = new DATABASE_CONFIG;
                ($dbConfig->ldap['type'] == 'ActiveDirectory')?$uidAttr='sAMAccountName':$uidAttr='uid';
		$dn = $this->getDn($uidAttr, $uid);
                $loginResult = 0;
		if (!is_null($password))$loginResult = $this->ldapauth($dn, $password); 
		if( $loginResult == 1){
			$this->_loggedIn = true;
                        $userObj = $this->users->find('all',array('conditions' => array('User.username' => $uid)));
                        $data[$this->userModel][$this->fields['username']]=$uid;
                        $data[$this->userModel][$this->fields['password']]=$this->password($password);
                        $user = $this->ldap->find('all', array('scope'=>'base', 'targetDn'=>$dn));
                        if (!empty($userObj)){
                                $id = $userObj[0][$this->userModel]['id'];
                                $this->users->id = $id;
                                $user_data = $userObj[0][$this->userModel];
                                $data[$this->userModel]['group_id']=$user_data['group_id'];
                        } else {
                                $data[$this->userModel]['group_id']=3;
                                $data[$this->userModel]['name']=$user[0]['LdapUser']['cn'];
                                $user_data = $data[$this->userModel];
                        }
                        $this->users->save($data[$this->userModel]);
                        $user_id = $this->users->id;
                        
                        $user_data['id'] = $user_id;
                        $user_data['LdapUser'] = $user[0]['LdapUser'];
                        $user_data['bindDN'] = $dn;
			$user_data['bindPasswd'] = $password;
			$this->Session->write($this->sessionKey, $user_data);
		}else{
                       // Check Database
                       // Doesn't not redirect with debugging on (writing late to header problem)
                       $loginResult = parent::login($data);
                        if ($loginResult == 1){
                                $this->_loggedIn = true;
                        } else {
                                $this->loginError =  $loginResult;
                        }
		}
                return $this->_loggedIn;
	}

	function ldapauth($dn, $password){
                if(is_null($this->ldap))
                        $this->ldap = $this->getModel('LdapUser');
		$authResult =  $this->ldap->auth( array('dn'=>$dn, 'password'=>$password));
		return $authResult;
	}

	function getDn( $attr, $query){
                if(is_null($this->ldap))
                        $this->ldap = $this->getModel('LdapUser');
		$userObj = $this->ldap->find('all', array('conditions'=>"$attr=$query", 'scope'=>'sub'));
                //$this->log("auth lookup found: ".print_r($userObj,true)." with the following conditions: ".print_r(array('conditions'=>"$attr=$query", 'scope'=>'one'),true),'debug');
		return($userObj[0]['LdapUser']['dn']);
	}
        
// Validate group, this parent is not working from the aros_acos ... not sure why.        
        function isAuthorized($type = null, $object = null, $user = null) {
                $actions  = $this->__authType($type);
                if( $actions['type'] != 'actions' ){
                    return parent::isAuthorized($type, $object, $user);
                }
                if (empty($user) && !$this->user()) {
                    return false;
                } elseif (empty($user)) {
                    $user = $this->user();
                }


                $group = array('model' => 'Group','foreign_key' =>$user[$this->userModel]['group_id']);
                $valid = $this->Acl->check($group, $this->action());
                return $valid;
    }
}
?>
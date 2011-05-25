<?php

App::import('Component', 'Auth');

class LogAuthComponent extends AuthComponent {

    var $Log = null;

    /**
     * Initialize method ensures Auth methods remain working as described in the book.
     */
//        function initialize(&$controller) {
//        parent::initialize($controller);
//        $controller->Auth =& $this;
//        }
    function initialize(&$controller, $settings=array()) {
        $this->Log = $controller->loadModel('Log');
        $this->Log = $controller->Log;
        parent::initialize($controller, $settings);
    }

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

                if ($this->login($data, $password)) {

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
                $this->Session->setFlash($this->authError, 'default', array(), 'auth');
                $this->Session->write('Auth.redirect', $url);
                $controller->redirect($loginAction);
                return false;
            }
        }


        if (!$this->authorize) {
            return true;
        }

        extract($this->__authType());
        switch ($type) {
            case 'controller':
                $this->object = & $controller;
                break;
            case 'crud':
            case 'actions':
                //TODO: ACL on actions?
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

    function login($data, $clear=null) {
        $uid = $data[$this->userModel]['username'];
        $password = $clear;
        $this->__setDefaults();
        $this->_loggedIn = false;
        $loginResult = 0;
        if (!is_null($password)
            )$loginResult = $this->logauth($uid, $password);
        if ($loginResult == true) {
            $this->_loggedIn = true;
            $data[$this->userModel][$this->fields['username']] = $uid;
            $data[$this->userModel][$this->fields['password']] = $this->password($password);
            $user_data = $data[$this->userModel];

            $user_data['bindPasswd'] = $password;
            $this->Session->write($this->sessionKey, $user_data);
            $this->Session->write('Log', $user_data);
            $this->Session->write('Auth.User.name', $uid);
        } else {
            $this->loginError = $loginResult;
        }
        return $this->_loggedIn;
    }

    function logout() {
        $this->Session->delete('Log');
        $this->Session->delete('Auth.User.name');
        return parent::logout();
    }

    function logauth($uid, $password) {
        $db = & ConnectionManager::getDataSource('olog');
        $this->Log->request['auth']['user'] = $uid;
        $this->Log->request['auth']['pass'] = $password;
        $this->Log->request['auth_request'] = true;
        if (!is_null($password)) {
            $authResult = $db->create($this->Log, null, null);
        }
        ($authResult == false) ? $result = false : $result = true;
        return $result;
    }

// Validate group, this parent is not working ... not sure why.
    function isAuthorized($type = null, $object = null, $user = null) {
        $actions = $this->__authType($type);
        if ($actions['type'] != 'actions') {
            return parent::isAuthorized($type, $object, $user);
        }
        if (empty($user) && !$this->user()) {
            return false;
        } elseif (empty($user)) {
            $user = $this->user();
        }

        $valid = true;
        return $valid;
    }

}

?>
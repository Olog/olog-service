<?php
class UploadsController extends OlogAppController {

   	var $name = 'Uploads';
	var $uses = array();
	

	
	function index() {
		$id = $this->params['named']['id'];
		$file = $this->params['named']['file'];
		$this->autoRender = false;
		App::import('Vendor','olog.FileUpload',array('file'=>'FileUpload'.DS.'upload.php'));
		App::import('Component', 'CakeSession');
		
		$session = new CakeSession();
		$auth = $session->read('Log');
		if(isset($auth['username'])&&isset($auth['bindPasswd'])){
			$user = $auth['username'];
			$pass = $auth['bindPasswd'];
		}

		$dbinfo = get_class_vars('DATABASE_CONFIG');
		$repository = $dbinfo['olog']['repository'];
		$repoArray = parse_url($repository);
		
		$options = array(
			'user' => $user,
			'pass' => $pass,
			'id' => $id,
			// I know I shouldn't take host from the repo
			'script_url' => $this->base.'/olog/uploads/index',
			'upload_dir' => $repoArray['path'],
			'upload_url' => $repository,
			'param_name' => 'files',
			// The php.ini settings upload_max_filesize and post_max_size
			// take precedence over the following max_file_size setting:
			'max_file_size' => null,
			'min_file_size' => 1,
			'accept_file_types' => '/.+$/i',
			'max_number_of_files' => null,
			'discard_aborted_uploads' => true,
			'image_versions' => array(
			    'thumbnail' => array(
			        'upload_dir' => $repoArray['path'].'thumbnails/',
			        'upload_url' => $repository.'thumbnails/',
			        'max_width' => 80,
			        'max_height' => 80
			    )
			)
		);
		$upload_handler = new UploadHandler($options);

		switch ($_SERVER['REQUEST_METHOD']) {
		    case 'HEAD':
		    case 'GET':
		        $result = $upload_handler->get($id);
		        break;
		    case 'POST':
		        $result = $upload_handler->post($id);
		        break;
		    case 'DELETE':
		        $result = $upload_handler->delete($id,$file);
		        break;
		    default:
		        $result = header('HTTP/1.0 405 Method Not Allowed');
		}
		return $result;
	}

}
?>
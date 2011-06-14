<?php
/*
 * jQuery File Upload Plugin PHP Example 5.2
 * https://github.com/blueimp/jQuery-File-Upload
 *
 * Copyright 2010, Sebastian Tschan
 * https://blueimp.net
 *
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
include(dirname(__FILE__).DS.'class_webdav_client.php');
error_reporting(E_ALL | E_STRICT);

class UploadHandler
{
    private $options;
    
    function __construct($options=null) {
        $this->options = array(
            'user' => null,
            'pass' => null,
            'id' => null,
            'script_url' => $_SERVER['PHP_SELF'],
            'upload_dir' => '',
            'upload_url' => '',
            'param_name' => 'files',
            // The php.ini settings upload_max_filesize and post_max_size
            // take precedence over the following max_file_size setting:
            'max_file_size' => null,
            'min_file_size' => 1,
            'accept_file_types' => '/.+$/i',
            'max_number_of_files' => null,
            'discard_aborted_uploads' => true,
            'image_versions' => array(
                // Uncomment the following version to restrict the size of
                // uploaded images. You can also add additional versions with
                // their own upload directories:
                /*
                'large' => array(
                    'upload_dir' => dirname(__FILE__).'/files/',
                    'upload_url' => dirname($_SERVER['PHP_SELF']).'/files/',
                    'max_width' => 1920,
                    'max_height' => 1200
                ),
                */
                'thumbnail' => array(
                    'upload_dir' => '',
                    'upload_url' => '',
                    'max_width' => 80,
                    'max_height' => 80
                )
            )
        );
        if ($options) {
            $this->options = $options;
        }
    }
    
    private function getDavInstance() { 

            //if(!self::$davclient) {
                $dbinfo = get_class_vars('DATABASE_CONFIG');
                $repository = $dbinfo['olog']['repository'];
                $repoArray = parse_url($repository);
		
                $davclient = new webdav_client(); 
                $davclient->set_server($repoArray['host']);
                $davclient->set_port(8080);
                $davclient->set_user($this->options['user']);
                $davclient->set_pass($this->options['pass']);
                $davclient->set_debug(1);
                if (!$davclient->open()) {
                    print_r('Error connecting to server.');
                }
            //} 
            return $davclient; 
    } 
    
    private function get_file_object($id,$file_name) {
        $davclient = $this->getDavInstance();
//	print_r($this->options['image_versions']);
        if ($davclient->is_file($file_name) && $file_name[0] !== '.') {
            $file = new stdClass();
            $file->name = basename($file_name);
//            print_r($file->name);
            //$file->size = filesize($file_path);
            $file->url = $this->options['upload_url'].$id.'/'.rawurlencode($file->name);
            foreach($this->options['image_versions'] as $version => $options) {
//		print_r($options['upload_dir'].$id.'/'.$file->name);
                if ($davclient->is_file($options['upload_dir'].$id.'/'.$file->name)) {
                    $file->{$version.'_url'} = $options['upload_url'].$id.'/'
                        .rawurlencode($file->name);
                }
            }
            $file->delete_url = $this->options['script_url']
                .'/file:'.rawurlencode($file->name).'/id:'.$id;
            $file->delete_type = 'DELETE';
            return $file;
        }
        return null;
    }
    
    private function get_file_objects($id) {
        $davclient = $this->getDavInstance();
        $ls = $davclient->ls($this->options['upload_dir'].$id.'/');
        //print_r($davclient);
        if (is_array($ls)){
            foreach($ls as $item){
                $parseUrl=parse_url($item['href']);
                $lsArray[]=$parseUrl['path'];
		$idArray[]=$id;
            }
            return array_values(array_filter(array_map(
                array($this, 'get_file_object'), 
                $idArray,$lsArray
            )));
        } else {
            //return '';
        }
    }

    private function create_scaled_image($file_name, $options) {
        $davclient = $this->getDavInstance();
        $file_path = $this->options['upload_dir'].$this->options['id'].'/'.$file_name;
        $file_url = $this->options['upload_url'].$this->options['id'].'/'.$file_name;
        $davclient->mkcol($options['upload_dir'].$this->options['id'].'/');
        $new_file_path = $options['upload_dir'].$this->options['id'].'/'.$file_name;
        list($img_width, $img_height) = @getimagesize($file_url);
        if (!$img_width || !$img_height) {
            return false;
        }
        $scale = min(
            $options['max_width'] / $img_width,
            $options['max_height'] / $img_height
        );
        if ($scale > 1) {
            $scale = 1;
        }
        $new_width = $img_width * $scale;
        $new_height = $img_height * $scale;
        $new_img = @imagecreatetruecolor($new_width, $new_height);
        switch (strtolower(substr(strrchr($file_name, '.'), 1))) {
            case 'jpg':
            case 'jpeg':
                $src_img = @imagecreatefromjpeg($file_url);
                $write_image = 'imagejpeg';
                break;
            case 'gif':
                $src_img = @imagecreatefromgif($file_url);
                $write_image = 'imagegif';
                break;
            case 'png':
                $src_img = @imagecreatefrompng($file_url);
                $write_image = 'imagepng';
                break;
            default:
                $src_img = $image_method = null;
        }
        $success = $src_img && @imagecopyresampled(
            $new_img,
            $src_img,
            0, 0, 0, 0,
            $new_width,
            $new_height,
            $img_width,
            $img_height
        );
        ob_start();
        $write_image($new_img, null, 100);
        $data = ob_get_clean();

        //todo: check file first
        $ret = $davclient->put($new_file_path, $data);
        // Free up memory (imagedestroy does not delete files):
        @imagedestroy($src_img);
        @imagedestroy($new_img);
        return $success;
    }
    
    private function has_error($uploaded_file, $file, $error) {
        if ($error) {
            return $error;
        }
        if (!preg_match($this->options['accept_file_types'], $file->name)) {
            return 'acceptFileTypes';
        }
        if ($uploaded_file && is_uploaded_file($uploaded_file)) {
            $file_size = filesize($uploaded_file);
        } else {
            $file_size = $_SERVER['CONTENT_LENGTH'];
        }
        if ($this->options['max_file_size'] && (
                $file_size > $this->options['max_file_size'] ||
                $file->size > $this->options['max_file_size'])
            ) {
            return 'maxFileSize';
        }
        if ($this->options['min_file_size'] &&
            $file_size < $this->options['min_file_size']) {
            return 'minFileSize';
        }
        if (is_int($this->options['max_number_of_files']) && (
                count($this->get_file_objects()) >= $this->options['max_number_of_files'])
            ) {
            return 'maxNumberOfFiles';
        }
        return $error;
    }
    
    private function handle_file_upload($uploaded_file, $name, $size, $type, $error) {
        $davclient = $this->getDavInstance();
        $file = new stdClass();
        $file->name = basename(stripslashes($name));
        $file->size = intval($size);
        $file->type = $type;
        $error = $this->has_error($uploaded_file, $file, $error);
        if (!$error && $file->name) {
            if ($file->name[0] === '.') {
                $file->name = substr($file->name, 1);
            }
            $file_path = $this->options['upload_dir'].$this->options['id'].'/'.$file->name;
            $append_file = is_file($file_path) && $file->size > filesize($file_path);
            clearstatcache();
            if ($uploaded_file && is_uploaded_file($uploaded_file)) {
                $fp = fopen($uploaded_file, 'rb');
                $data = '';
                while ($chunk = fgets($fp, 1024)) {
                    $data .= $chunk;
                };
                $davclient->mkcol('/Olog/repository/olog/'.$this->options['id']);
                $ret = $davclient->put('/Olog/repository/olog/'.$this->options['id'].'/'.$file->name, $data);
            }
            $file_size = filesize($file_path);
            if ($file_size === $file->size) {
                $file->url = $this->options['upload_dir'].$this->options['id'].'/'.rawurlencode($file->name);
                foreach($this->options['image_versions'] as $version => $options) {
                    if ($this->create_scaled_image($file->name, $options)) {
                        $file->{$version.'_url'} = $options['upload_url'].$this->options['id'].'/'
                            .rawurlencode($file->name);
                    }
                }
            } else if ($this->options['discard_aborted_uploads']) {
                unlink($file_path);
                $file->error = 'abort';
            }
            $file->size = $file_size;
            $file->delete_url = $this->options['script_url']
                .'/file:'.rawurlencode($file->name).'/id:'.$this->options['id'];
            $file->delete_type = 'DELETE';
        } else {
            $file->error = $error;
        }
        return $file;
    }
    
    public function get($id) {
        $file_name = isset($_REQUEST['file']) ?
            basename(stripslashes($_REQUEST['file'])) : null;
        if ($file_name) {
            $info = $this->get_file_object($id,$file_name);
        } else {
            $info = $this->get_file_objects($id);
        }
        header('Cache-Control: no-cache, must-revalidate');
        header('Content-type: application/json');
        return json_encode($info);
    }
    
    public function post($id) {
        $davclient = $this->getDavInstance();
        $fp = fopen($_FILES['file']['tmp_name'], 'rb');
        $data = '';
        while ($chunk = fgets($fp, 1024)) {
            $data .= $chunk;
        };
        //print_r(fgets($fp, 1024));
        $filename = $_FILES['file']['name'];
        $davclient->mkcol('/Olog/repository/olog/'.$id);
        $ret = $davclient->put('/Olog/repository/olog/'.$id.'/'.$filename, $data);
        header('Cache-Control: no-cache, must-revalidate');
        header('Vary: Accept');
        if (isset($_SERVER['HTTP_ACCEPT']) &&
            (strpos($_SERVER['HTTP_ACCEPT'], 'application/json') !== false)) {
            header('Content-type: application/json');
        } else {
            header('Content-type: text/plain');
        }
        $file = new stdClass();
        $file->name = basename($_FILES['file']['name']);
        $file->sizef = intval($_FILES['file']['size']);
        $file->type = $_FILES['file']['type'];
        $file->url = $this->options['upload_url'].$id.'/'.rawurlencode($file->name);
        $file->delete_url = $this->options['script_url']
                .'/file:'.rawurlencode($file->name).'/id:'.$id;
        $file->delete_type = 'DELETE';
        foreach($this->options['image_versions'] as $version => $options) {
                    if ($this->create_scaled_image($file->name, $options)) {
                        $file->{$version.'_url'} = $options['upload_url'].$id.'/'
                            .rawurlencode($file->name);
                    }
        }
        return json_encode(array($file));
    }
    
    public function delete($id,$file_name) {
        $davclient = $this->getDavInstance();
        $file_name = isset($file_name)?stripslashes($file_name) : null;
        $file_path = $this->options['upload_dir'].$id.'/'.$file_name;
        $success = $davclient->delete($file_path);
	//print_r($davclient);
        if ($success) {
            foreach($this->options['image_versions'] as $version => $options) {
                $file = $options['upload_dir'].$file_name;
                if (is_file($file)) {
                    $davclient->delete($file);
                }
            }
        }
        header('Content-type: application/json');
        return json_encode($success);
    }
}
?>
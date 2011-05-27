<?php
/**
 *
 * PHP versions 4 and 5
 *
 * CakePHP(tm) : Rapid Development Framework (http://cakephp.org)
 * Copyright 2005-2010, Cake Software Foundation, Inc. (http://cakefoundation.org)
 *
 * Licensed under The MIT License
 * Redistributions of files must retain the above copyright notice.
 *
 * @copyright     Copyright 2005-2010, Cake Software Foundation, Inc. (http://cakefoundation.org)
 * @link          http://cakephp.org CakePHP(tm) Project
 * @package       cake
 * @subpackage    cake.cake.libs.view.templates.layouts
 * @since         CakePHP(tm) v 0.10.0.1076
 * @license       MIT License (http://www.opensource.org/licenses/mit-license.php)
 */
$session->flash('auth');
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<?php echo $this->Html->charset(); ?>
	<title>
		<?php echo $title_for_layout; ?>
	</title>
	<?php
		echo $this->Html->meta('icon');

		echo $this->Html->css('jquery-ui-1.8.13.custom');
		echo $this->Html->css('jquery.fileupload-ui');
		echo $this->Html->css('logbook');
		echo $this->Html->css('style');

		echo $scripts_for_layout;
	?>
	<?php
		echo $this->Html->script('jquery-1.6.1.min');


	?>
</head>
<body>
	<div id="container">
		<div id="header">
			<h1><?php echo $this->Html->link(__('Logbook', true) , array('plugin'=>'olog','controller' => 'logs', 'action' => 'index')); ?></h1>
                        <div id="sign"><?php
                            if ($this->Session->check('Auth.User.name')) {
                                echo $session->read('Auth.User.name')."&nbsp;|&nbsp;";
                                echo $this->Html->link(__('Sign out',true), array('controller' => 'users', 'action' => 'logout'));
                            } else {
                                echo $this->Html->link(__('Sign in',true), array('controller' => 'users', 'action' => 'login'));
                            }
                        ?></div>
		</div>
		<div id="content">

			<?php echo $this->Session->flash(); ?>
			<?php echo $this->Session->flash('email'); ?>

			<?php echo $content_for_layout; ?>

		</div>

		<div id="footer">
		</div>
	</div>
	<?php echo $this->element('sql_dump'); ?>
	<?php echo $this->Js->writeBuffer(); ?>
</body>
</html>
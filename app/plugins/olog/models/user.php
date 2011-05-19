<?php
class User extends OlogAppModel {
	var $name = 'User';


	public $useDbConfig = 'user';

/*	var $hasMany = array(
		'Subscription' => array(
			'className' => 'Subscription',
			'foreignKey' => 'tag_id',
			'dependent' => false,
			'conditions' => '',
			'fields' => '',
			'order' => '',
			'limit' => '',
			'offset' => '',
			'exclusive' => '',
			'finderQuery' => '',
			'counterQuery' => ''
		)
	);
*/
}
?>
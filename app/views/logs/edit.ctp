<div class="logs form">
<?php //echo $this->Html->script('addUpload.js'); ?>
<?php echo $this->element('tinymce',array('preset' => 'basic')); ?> 
<?php echo $this->Form->create('Log', array('type' => 'file'));?>
	<fieldset>
 		<legend><?php __('Edit Log: '.$this->data['log']['id']); ?></legend>
	<?php
		echo $this->Form->input('log.id');
		echo $this->Form->input('log.level');
		echo $this->Form->input('log.subject');
		echo $this->Form->input('log.description');
		echo $this->Form->input('log.logbooks',array( 'type' => 'select', 'multiple' => true ));
		echo $this->Form->input('log.tags',array( 'type' => 'select', 'multiple' => true ));
		$i=0;
		//foreach($uploads as $upload){
		//	$file = str_replace('/olog','',$webdavdir).str_replace('\\','/',$upload['Upload']['store']);
		//	echo $this->Html->link($upload['Upload']['name'],$file);
			//echo $this->Form->input('Upload.'.$i.'.file', array('type' => 'file', 'value'=>$file));
			//echo '<br>';
			//$i++;
		//}
		//echo '<br><br>';
		//echo '<a href="javascript:addElement()">add another file</a>';
		//echo $this->Form->input('Upload.0.file', array('type' => 'file','div' => array('id' => 'fileDivId')));
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit', true));?>
</div>

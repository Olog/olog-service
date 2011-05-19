<div class="logs form">
<?php echo $this->Html->script('addUpload.js'); ?>
<?php echo $this->element('tinymce',array('preset' => 'basic')); ?> 
<?php echo $this->Form->create('Log', array('type' => 'file'));?>
	<fieldset>
 		<legend><?php __('Edit Log'); ?></legend>
	<?php
		echo $this->Form->input('id');
		echo $this->Form->input('level_id');
		echo $this->Form->input('subject');
		echo $this->Form->input('detail');
		echo $this->Form->input('Logbook',array( 'type' => 'select', 'multiple' => true ));
		echo $this->Form->input('Tag');
		$i=0;
		foreach($uploads as $upload){
			$file = str_replace('/olog','',$webdavdir).str_replace('\\','/',$upload['Upload']['store']);
			echo $this->Html->link($upload['Upload']['name'],$file);
			//echo $this->Form->input('Upload.'.$i.'.file', array('type' => 'file', 'value'=>$file));
			//echo '<br>';
			//$i++;
		}
		echo '<br><br>';
		echo '<a href="javascript:addElement()">add another file</a>';
		echo $this->Form->input('Upload.0.file', array('type' => 'file','div' => array('id' => 'fileDivId')));
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit', true));?>
</div>

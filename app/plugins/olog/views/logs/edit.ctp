<div class="logs form">
<?php //echo $this->Html->script('addUpload.js'); ?>
<?php echo $this->element('tinymce',array('preset' => 'basic')); ?> 
<?php echo $this->Form->create('Log', array('type' => 'file'));?>
	<fieldset>
 		<legend><?php __('Edit Log: '.$this->data['log']['id']); ?></legend>
	<?php
		$logbooksSelected = null;
		$tagsSelected = null;
		
		if(isset($this->data['log']['logbooks'])){
			foreach ($this->data['log']['logbooks'] as $logbook) {
				$logbooksSelected[$logbook['name']]=$logbook['name'];
			}
		}
		if(isset($this->data['log']['tags'])){
			foreach ($this->data['log']['tags'] as $tag) {
				$tagsSelected[$tag['name']]=$tag['name'];
			}
		}
		echo $this->Form->input('log.id',array('type'=>'hidden'));
		echo $this->Form->input('log.level');
		echo $this->Form->input('log.subject', array('type'=>'hidden'));
		echo $this->Form->input('log.description',array('type' => 'textarea', 'rows' => '2'));
		echo $this->Form->select('log.logbooks',$logbooks,$logbooksSelected,array( 'multiple' => true ));
		echo $this->Form->select('log.tags',$tags,$tagsSelected,array('multiple' => true ));
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

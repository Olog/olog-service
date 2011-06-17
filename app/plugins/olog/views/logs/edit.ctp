<div class="logs form">
<?php //echo $this->Html->script('addUpload.js'); ?>
<?php //echo $this->element('tinymce',array('preset' => 'basic')); ?> 
<?php echo $this->Form->create('Log', array('type' => 'file'));?>
	<fieldset>
 		<legend><?php __('Edit Log: '.$this->data['log']['id']); ?></legend>
<?php
$log = $this->data['log'];
$base = $this->base;
?> 
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
	?>
	</fieldset>
<?php echo $this->Form->end(__('Submit', true));?>
<span class="uploadspan">
<div id="fileupload_<?php echo $log['id']; ?>">
<div class="files" title="<?php echo $base; ?>/olog/uploads/index/id:<?php echo $log['id'];?>"/>
    <form action="<?php echo $base; ?>/olog/uploads/index/id:<?php echo $log['id']; ?>" method="POST" enctype="multipart/form-data">
        <?php //<div class="fileupload-buttonbar"> ?>
            <label class="fileinput-button">
                <span>Add files</span>
		<input type="hidden" name="id" value="<?php echo $log['id']; ?>" />
                <input type="file" name="file" multiple>
            </label>
       <?php // </div> ?>
    </form>
</div>
<script id="template-upload" type="text/x-jquery-tmpl">
    <tr class="template-upload{{if error}} ui-state-error{{/if}}">
        <td class="preview"></td>
        <td class="name">${name}</td>
        <td class="size">${sizef}</td>
        {{if error}}
            <td class="error" colspan="2">Error:
                {{if error === 'maxFileSize'}}File is too big
                {{else error === 'minFileSize'}}File is too small
                {{else error === 'acceptFileTypes'}}Filetype not allowed
                {{else error === 'maxNumberOfFiles'}}Max number of files exceeded
                {{else}}${error}
                {{/if}}
            </td>
        {{else}}
            <td class="progress"><div></div></td>
            <td class="start"><button>Start</button></td>
        {{/if}}
        <td class="cancel"><button>Cancel</button></td>
    </tr>
</script>
<script id="template-download" type="text/x-jquery-tmpl">
    <tr class="template-download{{if error}} ui-state-error{{/if}}">
        {{if error}}
            <td></td>
            <td class="name">${name}</td>
            <td class="size">${sizef}</td>
            <td class="error" colspan="2">Error:
                {{if error === 1}}File exceeds upload_max_filesize (php.ini directive)
                {{else error === 2}}File exceeds MAX_FILE_SIZE (HTML form directive)
                {{else error === 3}}File was only partially uploaded
                {{else error === 4}}No File was uploaded
                {{else error === 5}}Missing a temporary folder
                {{else error === 6}}Failed to write file to disk
                {{else error === 7}}File upload stopped by extension
                {{else error === 'maxFileSize'}}File is too big
                {{else error === 'minFileSize'}}File is too small
                {{else error === 'acceptFileTypes'}}Filetype not allowed
                {{else error === 'maxNumberOfFiles'}}Max number of files exceeded
                {{else error === 'uploadedBytes'}}Uploaded bytes exceed file size
                {{else error === 'emptyResult'}}Empty file upload result
                {{else}}${error}
                {{/if}}
            </td>
        {{else}}
            <td class="preview">
                {{if thumbnail_url}}
                    <a href="${url}" target="_blank"><img src="${thumbnail_url}"></a>
                {{/if}}
            </td>
            <td class="name">
                <a href="${url}"{{if thumbnail_url}} target="_blank"{{/if}}>${name}</a>
            </td>
            <td class="size">${sizef}</td>
            <td colspan="2"></td>
        {{/if}}
        <td class="delete">
            <button data-type="${delete_type}" data-url="${delete_url}">Delete</button>
        </td>
    </tr>
</script>
</span>

<?php
		echo $this->Html->script('FileUpload/jquery-ui-1.8.13.custom.min');
		echo $this->Html->script('FileUpload/jquery.iframe-transport');
		echo $this->Html->script('FileUpload/jquery.fileupload');
		echo $this->Html->script('FileUpload/jquery.fileupload-ui');
		echo $this->Html->script('FileUpload/jquery.application');
		echo $this->Html->script('FileUpload/jquery.tmpl.min');



?>


</div>

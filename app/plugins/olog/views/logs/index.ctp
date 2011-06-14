

<?php

//echo $ajax->remoteTimer( 
//    array( 
//    'url' => array( 'controller' => 'logs', 'action' => 'index'), 
//    'update' => 'test', 'frequency' => 10
//    )
//);
?>
 <div id='logsFormAdd' align='right' >
     <button id="AddNewLog" class="button" >Add a New Log</button>
 </div>
    
<?php echo $this->Html->script('addUpload.js'); ?>
<?php //echo $this->Html->link(__('Configure application', true), array('controller' => 'searches', 'action' => 'search')); ?>
<div class="logs index">
    <div id="logForm" class="logs form" style="display: none" >
       
        
        <?php echo $this->Form->create('log', array('type' => 'file', 'action' => 'add')); ?>
        <fieldset id='logFormFieldset'>
            <legend><?php __('Add a New Log'); ?></legend>
            <div id='logFormCredentials'>
                <?php if (!$session->check('Auth.User.name')) {
                ?>
                    <div id='logFormUsername'><?php echo $this->Form->input('username', array('label' => 'Username')); ?></div>
                    <div id='logFormPassword'><?php echo $this->Form->input('password', array('label' => 'Password')); ?></div>
                <?php } ?>
            </div>
            <div id='logFormContainer'>
                
                <div id='logFormInfo'>
                    
                    <?php echo $this->Form->input('subject', array('type' => 'hidden')); ?>
                    <div id='logFormDescription'><?php echo $this->Form->input('description', array('type' => 'textarea', 'rows' => '15')); ?></div>
                </div>
                <div id='logFormSelects'>
                    <div id='logFormLevels'><?php echo $this->Form->input('level'); ?></div>
                    <div id='logFormLogbooks'><?php echo $this->Form->input('logbooks', array('type' => 'select', 'multiple' => true)); ?></div>
                    <div id='logFormTags'><?php echo $this->Form->input('tags', array('type' => 'select', 'multiple' => true)); ?></div>
                </div>
            </div>
	    <div class="addFiles" id="fileupload_<?php //echo $log['id']; ?>">
	    <form action="<?php echo $base; ?>/olog/uploads/index/id:<?php //echo $log['id']; ?>" method="POST" enctype="multipart/form-data">
		<label class="fileinput-button">
		    <span>Add files</span>
			<input type="hidden" name="id" value="<?php //echo $log['id']; ?>" />
		    <input type="file" name="file" multiple>
		</label>
	    </form>
	    <div class="fileupload-content">
		<table class="files"></table>
	    <div class="fileupload-progressbar"></div>
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
    </div>
</div>
            <div id='logFormSubmit'><?php echo $this->Form->end(__('Submit', true)); ?></div>
        </fieldset>
        <div align="right">
            <button id="cancelAddNewLog" class="button" >Close</button>
        </div>
    </div>

    <script type="text/javascript" >
    $('#AddNewLog').click(function() {
	$('#logForm').show('fast');
    });
    $('#cancelAddNewLog').click(function() {
    $('#logForm').hide(1000);
    });
    </script>
    
    <div id="menu">
        <!--<div id='search'><?php echo $this->Form->create('search', array('action' => 'search')); ?>
            <div id='searchItem'><?php echo $this->Form->input('search_item', array('label' => '')); ?></div>
            <div id='searchButton'><?php echo $this->Form->end(__('Search', true)); ?></div>
        </div>-->
        <?php // Todo:  on click update +2 days  ?>
                </div>
                <div id="logviews">
        <?php // Todo:  toggle, when Threaded selected, display collapse/expand  ?>
        <?php //echo $this->Html->link(__('Full', true), array('action' => 'add')) . ' | '; ?>
        <?php //echo $this->Html->link(__('Summary', true), array('action' => 'add')) . ' | '; ?>
        <?php //echo $this->Html->link(__('Threaded', true), array('action' => 'threaded')); ?>

                    <span id="quickfilters">
            <?php
                    //  Todo:  Update logs on change of select box
                    //  attach this to something
                    //  also check params for current
                    $timespans = array('Last day',
                        'Last 3 Days',
                        'Last week',
                        'Last month',
                        'Last 3 Months',
                        'Last 6 Months',
                        'Last year'
                    );
		    
                    echo $this->Form->select('timespan', $timespans, null, array('id' => 'timespan'));
                    //  Todo:  Update logs on change of select box
                    if (isset($this->params['named']['logbook'])) {
                        echo $this->Form->select('logbook', $logbooks, array($this->params['named']['logbook']), array('id' => 'logbook'));
                    } else {
                        echo $this->Form->select('logbook', $logbooks, null, array('id' => 'logbook'));
                    }
            ?>
                </span>
            </div>
            <table cellpadding="0" cellspacing="0">

        <?php
                    $i = 0;
                    $j = 0;

                    if(empty($logs['logs']['log'][0])){
                        if(empty($logs['logs']['log'])){
                            //somehow say there are no logs
                        }else{
                        $temp=$logs['logs']['log'];
                        unset($logs);
                        $logs['logs']['log'][0]=$temp;
                        }
                    }

                    foreach ($logs['logs']['log'] as $log):
                        $class = null;
                        if ($i++ % 2 == 0) {
                            $class = ' class="altrow"';
                        }
        ?>
                        <tr<?php echo $class; ?>>
                            <td class="subject">
				<span><?php echo date('d M Y H:i', strtotime($log['createdDate'])).', '.$log['owner']; ?></span>
				<span id="tag"><?php
                        
				    foreach ($log['tags'] as $tags) {
					if (isset($tags['name'])) {
					    echo $tags['name'];
					} else {
					    foreach ($tags as $tag) {
					        if (isset($tag['name'])) {
							echo $tag['name'] . '&nbsp;,&nbsp;';
					        }
					    }
					}
				    }?></span>
				<span id="logbook"><?php
				    foreach ($log['logbooks'] as $logbooks) {
					if (isset($logbooks['name'])) {
					    echo $logbooks['name'];
					} else {
					    foreach ($logbooks as $logbook) {
					        if (isset($logbook['name'])) {
							echo $logbook['name'] . '&nbsp;,&nbsp;';
					        }
					    }
					}
				    }?>,&nbsp; </span>
				<div id="level"><?php echo $log['level'] ?></div>
				<div class="edited"><?php if ($log['version'] > 0 )echo '[edited]'; ?></div>
                                <div id='description'><?php echo (!empty($log['description']) ? $log['description'] : ''); ?></div>

<div id="fileupload_<?php echo $log['id']?>" >
<div class="files" title="<?php echo $base; ?>/olog/uploads/index/id:<?php echo $log['id'];?>"/>
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
            <td class="size">${sizef}</td>
            <td colspan="2"></td>
        {{/if}}

    </tr>
</script>
</div>

                </tr>
<?php endforeach; ?>
            </table>
            <p>
<?php
                        echo $this->Paginator->counter(array(
                            'format' => __('Page %page% of %pages%, showing %current% records out of %count% total, starting on record %start%, ending on %end%', true)
                        ));
?>	</p>

                    <div class="paging">
<?php echo $this->Paginator->prev('<< ' . __('previous', true), array(), null, array('class' => 'disabled')); ?>
                                                                                                                                                                                                                                                                                                                                                                                                                            	 | 	<?php echo $this->Paginator->numbers(); ?>
                        |
<?php echo $this->Paginator->next(__('next', true) . ' >>', array(), null, array('class' => 'disabled')); ?>
    </div>
</div>
<script type="text/javascript">
    $('#logbook').bind('change', function() {
        var logbookType = $('#logbook').val();
	<?php
	$args = '';
	foreach($this->params['named'] as $key=>$param){
	  if($key !='logbook'){
	    $args .= '/'.$key.':'.$param;
	  }
	}
	?>
        window.location.replace('<?php echo $base.'/'.$this->params['plugin'].'/'.$this->params['controller'].'/'.$this->params['action'].'/logbook:'; ?>' + logbookType + '<?php echo $args; ?>');
    });
    $('#timespan').bind('change', function() {
        var newTimeSpan = $('#timespan').val();
        window.location.replace('<?php echo $base.'/'.$this->params['plugin'].'/'.$this->params['controller']; ?>+ /timespanChange/' + newTimeSpan + '<?php echo $argumentString; ?>');
    });
</script>
<?php
		echo $this->Html->script('FileUpload/jquery-ui-1.8.13.custom.min');
		echo $this->Html->script('FileUpload/jquery.iframe-transport');
		echo $this->Html->script('FileUpload/jquery.fileupload');
		echo $this->Html->script('FileUpload/jquery.fileupload-ui');
		echo $this->Html->script('FileUpload/jquery.application');
		echo $this->Html->script('FileUpload/jquery.tmpl.min');


?>



<?php

//echo $ajax->remoteTimer( 
//    array( 
//    'url' => array( 'controller' => 'logs', 'action' => 'index'), 
//    'update' => 'test', 'frequency' => 10
//    )
//);
?>
 <div id='logsFormAdd' align='right' >
    <a><img id="closeNewLog" style="display:none" src="<?php echo $base; ?>/img/blue-document--minus.png" alt="close new log" class="NewLog_icons"></a>
    <a><img id="addNewLog" src="<?php echo $base; ?>/img/blue-document--plus.png" alt="add new log" class="NewLog_icons"></a>
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
                    <div id='logFormDescription' style="resize: none">
                        <?php echo $this->Form->input('description', array('type' => 'textarea', 'rows' => '15')); ?>
                    </div>
                </div>
                <div id='logFormSelects'>
                    <div id='logFormLevels'><?php echo $this->Form->input('level'); ?></div>
                    <div id='logFormLogbooks'><?php echo $this->Form->input('logbooks', array('type' => 'select', 'multiple' => true)); ?></div>
                    <div id='logFormTags'><?php echo $this->Form->input('tags', array('type' => 'select', 'multiple' => true)); ?></div>
                </div>
            </div>
	    <div style="display:none" class="addFiles" id="fileupload_<?php //echo $log['id']; ?>">
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
            <div id='logFormSubmit'>
                <?php echo $this->Form->end(__('Submit', true)); ?>
            </div>
        </fieldset>
    </div>

    <script type="text/javascript" >
    $('#addNewLog').click(function() {
	$('#logForm').show('fast');
	$('#addNewLog').hide();
	$('#closeNewLog').show();
    });
    $('#closeNewLog').click(function() {
	$('#logForm').hide();
	$('#closeNewLog').hide();
	$('#addNewLog').show();
    });
    
    $(document).ready(function() {
	$("input[type=file]").filestyle({ 
	    image: "<?php echo $base; ?>/img/image--plus.png",
	    imageheight : 16,
	    imagewidth : 16,
	    width : 16,
	    showFilename : false
	});
    });
    </script>
    
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
		    
                    echo '<input size="100" style="margin-bottom:5px;" type="text" name="search" id="search" />';
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
            <table style="border-top:1px solid #ccc;" cellpadding="0" cellspacing="0">

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
				<span class="tag">
				<?php if(!empty($log['tags'])) echo '<img src="'.$base.'/img/tag-medium.png">&nbsp;';
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
				<span class="logbook">
				    <img src="<?php echo $base; ?>/img/17px-Nuvola_apps_bookcase_1_blue.png">&nbsp;<?php
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
				<div class="level"><?php echo $log['level'] ?></div>
				<div class="edited"><?php if ($log['version'] > 0 )echo '[edited]'; ?></div>
                                <div class='description'><?php echo (!empty($log['description']) ? htmlentities($log['description']) : ''); ?></div>

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
	    {{if thumbnail_url}}
	        <td class="preview">
                    <a href="${url}" target="_blank"><img src="${thumbnail_url}"></a>
		</td>
	    {{else}}
		<td class="name">
		    <a href="${url}" target="_blank">${name}</a>
		</td>
	    {{/if}}
            <td colspan="2"></td>
        {{/if}}

    </tr>
</script>
</div>

<div class="actionButton">
    <form style='padding: 0px' action="<?php echo $base; ?>/olog/uploads/index/id:<?php echo $log['id']; ?>" method="POST" enctype="multipart/form-data">

	<input type="file" name="file" multiple>
	<input type="hidden" name="id" value="<?php echo $log['id']; ?>" />
	<a style="padding: 0px 0px 0px 20px;" href="<?php echo $base.'/'.$this->params['plugin'].'/'.$this->params['controller'].'/edit/'.$log['id'];?>">
	    <img border="0" src="<?php echo $base; ?>/img/blue-document--pencil.png" alt="edit">
	</a>
    </form>
</div>
    <script type="text/javascript" >
    $('.edit_log').click(function() {
	$('#logForm').show('fast');
	$('#addNewLog').hide();
	$('#closeNewLog').show();
        $.getJSON(this.title);
    });
    </script>

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
	if(logbookType!=''){
	    logbookType='logbook:'+logbookType;
	}
	<?php
	$args = '';
	foreach($this->params['named'] as $key=>$param){
	  if($key !='logbook'){
	    $args .= '/'.$key.':'.$param;
	  }
	}
	?>
        window.location.replace('<?php echo $base.'/'.$this->params['plugin'].'/'.$this->params['controller'].'/'.$this->params['action'].'/'; ?>' + logbookType + '<?php echo $args; ?>');
    });
    
    $('#timespan').bind('change', function() {
        var newTimeSpan = $('#timespan').val();
        window.location.replace('<?php echo $base.'/'.$this->params['plugin'].'/'.$this->params['controller']; ?>+ /timespanChange/' + newTimeSpan + '<?php echo $argumentString; ?>');
    });

    $('#search').bind('keypress', function(e) {
        var code = (e.keyCode ? e.keyCode : e.which);
        if(code == 13) {
            var search = $('#search').val();
            if(search!=''){
                search='search:'+search;
            }
            <?php
            $args = '';
            foreach($this->params['named'] as $key=>$param){
                if($key !='search'){
                    $args .= '/'.$key.':'.$param;
                }
            }
            ?>
        window.location.replace('<?php echo $base.'/'.$this->params['plugin'].'/'.$this->params['controller'].'/'.$this->params['action'].'/'; ?>' + search + '<?php echo $args; ?>');
        }
    }).watermark('Search...');
</script>


<?php
		echo $this->Html->script('FileUpload/jquery-ui-1.8.13.custom.min');
		echo $this->Html->script('FileUpload/jquery.iframe-transport');
		echo $this->Html->script('FileUpload/jquery.fileupload');
		echo $this->Html->script('FileUpload/jquery.fileupload-ui');
		echo $this->Html->script('FileUpload/jquery.application');
		echo $this->Html->script('FileUpload/jquery.tmpl.min');



?>

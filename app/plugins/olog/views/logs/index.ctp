

<?php
//echo $ajax->remoteTimer( 
//    array( 
//    'url' => array( 'controller' => 'logs', 'action' => 'index'), 
//    'update' => 'test', 'frequency' => 10
//    )
//);
?>
<?php echo $this->Html->script('addUpload.js'); ?>
<?php //echo $this->Html->link(__('Configure application', true), array('controller' => 'searches', 'action' => 'search')); ?>
<div class="logs index">
    <div class="logs form">
        <?php echo $this->element('tinymce', array('preset' => 'basic')); ?>
        <?php echo $this->Form->create('log', array('type' => 'file', 'action' => 'add')); ?>
        <fieldset>
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
                    <div id='logFormDescription'><?php echo $this->Form->input('description', array('type' => 'textarea', 'rows' => '2')); ?></div>
                </div>
                <div id='logFormSelects'>
                    <div id='logFormLevels'><?php echo $this->Form->input('level'); ?></div>
                    <div id='logFormLogbooks'><?php echo $this->Form->input('logbooks', array('type' => 'select', 'multiple' => true)); ?></div>
                    <div id='logFormTags'><?php echo $this->Form->input('tags', array('type' => 'select', 'multiple' => true)); ?></div>
                </div>
            </div>
            <div id='logFormSubmit'><?php echo $this->Form->end(__('Submit', true)); ?></div>
        </fieldset>
    </div>
    <div id="menu">
        <!--<div id='search'><?php echo $this->Form->create('search', array('action' => 'search')); ?>
            <div id='searchItem'><?php echo $this->Form->input('search_item', array('label' => '')); ?></div>
            <div id='searchButton'><?php echo $this->Form->end(__('Search', true)); ?></div>
        </div>-->
        <?php // Todo:  on click update +2 days  ?>
                </div>
                <div id="logviews">
        <?php // Todo:  toggle, when Threaded selected, display collapse/expand  ?>
        <?php echo $this->Html->link(__('Full', true), array('action' => 'add')) . ' | '; ?>
        <?php echo $this->Html->link(__('Summary', true), array('action' => 'add')) . ' | '; ?>
        <?php echo $this->Html->link(__('Threaded', true), array('action' => 'threaded')); ?>
        <?php //echo $this->Html->link(__('Expand', true), array('action' => 'add')).' | '; ?>
        <?php //echo $this->Html->link(__('Collapse', true), array('action' => 'add'));  ?>
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
                    if (isset($this->params['pass'][0])) {
                        echo $this->Form->select('logbook', $logbooks, $this->params['pass'][0], array('id' => 'logbook'));
                    } else {
                        echo $this->Form->select('logbook', $logbooks, null, array('id' => 'logbook'));
                    }
            ?>
                </span>
            </div>
            <table cellpadding="0" cellspacing="0">
                <tr>
               <!--     <th><?php echo $this->Paginator->sort('id'); ?></th>-->
                    <th><?php echo $this->Paginator->sort('description'); ?></th>
                    <th><?php echo $this->Paginator->sort('created'); ?></th>
                    <th><?php echo $this->Paginator->sort('level'); ?></th>
                    <th><?php echo $this->Paginator->sort('owner'); ?></th>
                    <th class="actions"><?php __('Actions'); ?></th>
                </tr>
        <?php
                    $i = 0;
                    $j = 0;

                    foreach ($logs['logs']['log'] as $log):
                        $class = null;
                        if ($i++ % 2 == 0) {
                            $class = ' class="altrow"';
                        }
        ?>
                        <tr<?php echo $class; ?>>
                 <!--           <td class="id"><?php echo $log['id']; ?></td> -->
                            <td class="subject">
                                <div id='description'><?php echo (!empty($log['description']) ? $log['description'] : ''); ?></div>
                                <div id="tag"><?php
//                        foreach ($log['logbooks'] as $logbook) {
//                            if (isset($logbook['name'])) {
//                                echo $logbook['name'] . '&nbsp;|&nbsp;';
//                            }
//                        }
                        foreach ($log['tags'] as $tags) {
                            if (isset($tags['name'])) {
                                echo $tags['name'];
                            } else {
                                foreach ($tags as $tag) {
                                    if (isset($tag['name'])) {
                                        echo $tag['name'] . '&nbsp;|&nbsp;';
                                    }
                                }
                            }
                        }
        ?></div></td>
                <td class="date"><?php echo date('d M Y H:i', strtotime($log['createdDate'])); ?>&nbsp;<br><div class="edited"><?php if ($log['version'] > 0

                            )echo '[edited]'; ?></div></td>
                <td class="level">
<?php echo $this->Html->link($log['level'], array('controller' => 'levels', 'action' => 'view', $log['level'])); ?>
                </td>
                <td class="owner">
<?php echo $this->Html->link($log['owner'], array('controller' => 'users', 'action' => 'view', $log['owner'])); ?>
                    </td>
                    <td class="actions">
<?php echo $this->Html->link(__('View', true), array('action' => 'view', $log['id'])); ?>
                <?php echo $this->Html->link(__('Edit', true), array('action' => 'edit', $log['id'])); ?>
<div id="fileupload_<?php echo $log['id']; ?>">
    <form action="/FileUpload/upload.php?id=<?php echo $log['id']; ?>" method="POST" enctype="multipart/form-data">
        <div class="fileupload-buttonbar">
            <label class="fileinput-button">
                <span>Add files</span>
		<input type="hidden" name="id" value="<?php echo $log['id']; ?>" />
                <input type="file" name="file" multiple>
            </label>
        </div>
    </form>
    <div class="fileupload-content">
        <table class="files"></table>
        <div class="fileupload-progressbar"></div>
    </div>
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

                <?php //echo $this->Html->link(__('Delete', true), array('action' => 'delete', $log['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $log['id']));   ?>
                    </td>
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
        window.location.replace('<?php echo $base; ?>/olog/logs/logbookChange/' + logbookType + '<?php echo $argumentString; ?>');
    });
    $('#timespan').bind('change', function() {
        var newTimeSpan = $('#timespan').val();
        window.location.replace('<?php echo $base; ?>/olog/logs/timespanChange/' + newTimeSpan + '<?php echo $argumentString; ?>');
    });
</script>
<?php
		echo $this->Html->script('FileUpload/jquery-ui.min.js');
		echo $this->Html->script('FileUpload/jquery.iframe-transport');
		echo $this->Html->script('FileUpload/jquery.fileupload');
		echo $this->Html->script('FileUpload/jquery.fileupload-ui');
		echo $this->Html->script('FileUpload/jquery.application');
		echo $this->Html->script('FileUpload/jquery.tmpl.min');


?>

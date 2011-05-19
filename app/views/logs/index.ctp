

<?php //echo $ajax->remoteTimer( 
//    array( 
//    'url' => array( 'controller' => 'logs', 'action' => 'index'), 
//    'update' => 'test', 'frequency' => 10
//    )
//); 
?>
<div class="logs index">
	<div id="menu">
		<?php echo $this->Html->link(__('New Log', true), array('action' => 'add')).' | '; ?>
		<?php echo $this->Html->link(__('Find', true), array('controller' => 'searches', 'action' => 'search')).' | '; ?>
		<?php echo $this->Html->link(__('Config', true), array('controller' => 'searches', 'action' => 'search')).' | '; ?>
		<?php // Todo:  on click update +2 days ?> 
		<?php echo $this->Html->link(__('Last Day', true), array('controller' => 'searches', 'action' => 'search')).' | '; ?>
	</div>
	<div id="logviews">
		<?php // Todo:  toggle, when Threaded selected, display collapse/expand ?>
		<?php echo $this->Html->link(__('Full', true), array('action' => 'add')).' | '; ?>
		<?php echo $this->Html->link(__('Summary', true), array('action' => 'add')).' | '; ?>
		<?php echo $this->Html->link(__('Threaded', true), array('action' => 'threaded')).' | '; ?>
		<?php //echo $this->Html->link(__('Expand', true), array('action' => 'add')).' | '; ?>
		<?php //echo $this->Html->link(__('Collapse', true), array('action' => 'add')); ?>
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
			echo $this->Form->select('timespan',$timespans, null, array('id'=>'timespan'));
			
			//  Todo:  Update logs on change of select box
			//  $this->Js->get('#logbook');
			//  $this->Js->event('change', update logs);
			if ( isset($this->params['pass'][0])) {
				echo $this->Form->select('logbook',$logbooks,$this->params['pass'][0],array('id'=>'logbook'));
			} else {
				echo $this->Form->select('logbook',$logbooks, null, array('id'=>'logbook'));
			}
			?>
		</span>
	</div>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id');?></th>
			<th><?php echo $this->Paginator->sort('created');?></th>
			<th><?php echo $this->Paginator->sort('level_id');?></th>
			<th><?php echo $this->Paginator->sort('subject');?></th>
			<th><?php echo $this->Paginator->sort('user_id');?></th>
			<th class="actions"><?php __('Actions');?></th>
	</tr>
	<?php
	$i = 0;
	foreach ($logs as $log):
		$class = null;
		if ($i++ % 2 == 0) {
			$class = ' class="altrow"';
		}
	?>
	<tr<?php echo $class;?>>
		<td><?php echo $log['Log']['id']; ?></td>
		<td><?php echo $log['Log']['created']; ?>&nbsp;<br><div class="edited"><?php if(!empty($log['ChildLog']))echo '[edited]'; ?></div></td>
		<td>
			<?php echo $this->Html->link($log['Level']['name'], array('controller' => 'levels', 'action' => 'view', $log['Level']['id'])); ?>
		</td>
		<td><?php echo $log['Log']['subject']; ?>&nbsp;&nbsp<br>
		    <?php echo $log['Log']['detail']; ?>
		    <div class="tag"><?php foreach ($log['Tag'] as $tag) echo $tag['name'].'&nbsp;|&nbsp;'?></div></td>
		<td>
			<?php echo $this->Html->link($log['User']['name'], array('controller' => 'users', 'action' => 'view', $log['User']['id'])); ?>
		</td>
		<td class="actions">
			<?php echo $this->Html->link(__('View', true), array('action' => 'view', $log['Log']['id'])); ?>
			<?php echo $this->Html->link(__('Edit', true), array('action' => 'edit', $log['Log']['id'])); ?>
			<?php echo $this->Html->link(__('Delete', true), array('action' => 'delete', $log['Log']['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $log['Log']['id'])); ?>
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
		<?php echo $this->Paginator->prev('<< ' . __('previous', true), array(), null, array('class'=>'disabled'));?>
	 | 	<?php echo $this->Paginator->numbers();?>
 |
		<?php echo $this->Paginator->next(__('next', true) . ' >>', array(), null, array('class' => 'disabled'));?>
	</div>
</div>

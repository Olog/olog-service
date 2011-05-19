

<?php //echo $ajax->remoteTimer( 
    //array( 
    //'url' => array( 'controller' => 'logs', 'action' => 'threaded'), 
    //'update' => '#content', 'frequency' => 10
   // )
//); 
?>
<?php
	$paginator->options(array(
		'update' => '#content', 
		'evalScripts' => true,
	//	'before' => $js->get('#logs')->effect('fadeOut', array('buffer' => false)),
	//	'success' => $js->get('#logs')->effect('fadeIn', array('buffer' => false))
	));
?>
<div id="logs" class="logs index">
	<h2><?php __('Logs');?></h2>
	<table cellpadding="0" cellspacing="0">
	<tr>
			<th><?php echo $this->Paginator->sort('id');?></th>
			<th><?php echo $this->Paginator->sort('created');?></th>
			<th><?php echo $this->Paginator->sort('level_id');?></th>
			<th><?php echo $this->Paginator->sort('subject');?></th>
			<th><?php echo $this->Paginator->sort('user_id');?></th>
			<th><?php echo $this->Paginator->sort('parent_id');?></th>
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
		<td><?php echo $log['Log']['id']; ?>&nbsp;</td>
		<td><?php echo $log['Log']['created']; ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($log['Level']['name'], array('controller' => 'levels', 'action' => 'view', $log['Level']['id'])); ?>
		</td>
		<td><?php echo $log['Log']['subject']; ?>&nbsp;&nbsp<br>
		    <?php echo $log['Log']['detail']; ?>&nbsp;</td>
		<td>
			<?php echo $this->Html->link($log['User']['name'], array('controller' => 'users', 'action' => 'view', $log['User']['id'])); ?>
		</td>
		<td>
			<?php echo $this->Html->link($log['ParentLog']['id'], array('controller' => 'logs', 'action' => 'view', $log['ParentLog']['id'])); ?>
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


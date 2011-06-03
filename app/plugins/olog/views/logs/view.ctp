<div class="logs view">
<h2><?php  __('Log #'.$log['log']['id']);?></h2>
	<dl><?php $i = 0; $class = ' class="altrow"';?>
		<dt<?php if ($i % 2 == 0) echo $class;?>><?php __('Id'); ?></dt>
		<dd<?php if ($i++ % 2 == 0) echo $class;?>>
			<?php echo $log['log']['id']; ?>
			&nbsp;
		</dd>
		<dt<?php if ($i % 2 == 0) echo $class;?>><?php __('Created'); ?></dt>
		<dd<?php if ($i++ % 2 == 0) echo $class;?>>
			<?php echo date('d M Y H:i', strtotime($log['log']['createdDate'])); ?>
			&nbsp;
		</dd>
		<dt<?php if ($i % 2 == 0) echo $class;?>><?php __('Owner'); ?></dt>
		<dd<?php if ($i++ % 2 == 0) echo $class;?>>
			<?php echo $this->Html->link($log['log']['owner'], array('controller' => 'users', 'action' => 'view', $log['log']['owner'])); ?>
			&nbsp;
		</dd>
		<dt<?php if ($i % 2 == 0) echo $class;?>><?php __('Level'); ?></dt>
		<dd<?php if ($i++ % 2 == 0) echo $class;?>>
			<?php echo $this->Html->link($log['log']['level'], array('controller' => 'levels', 'action' => 'view', $log['log']['level'])); ?>
			&nbsp;
		</dd>
		<dt<?php if ($i % 2 == 0) echo $class;?>><?php __('Subject'); ?></dt>
		<dd<?php if ($i++ % 2 == 0) echo $class;?>>
			<?php echo $log['log']['subject']; ?>
			&nbsp;
		</dd>
		<dt<?php if ($i % 2 == 0) echo $class;?>><?php __('Description'); ?></dt>
		<dd<?php if ($i++ % 2 == 0) echo $class;?>>
			<?php echo $log['log']['description']; ?>
			&nbsp;
		</dd>
	</dl>
</div>
<div class="related">
	<h3><?php __('Related Logs');?></h3>
	<?php if (!empty($log['ChildLog'])):?>
	<table cellpadding = "0" cellspacing = "0">
	<tr>
		<th><?php __('Id'); ?></th>
		<th><?php __('Created'); ?></th>
		<th><?php __('Modified'); ?></th>
		<th><?php __('Source'); ?></th>
		<th><?php __('User Id'); ?></th>
		<th><?php __('Level Id'); ?></th>
		<th><?php __('Subject'); ?></th>
		<th><?php __('Detail'); ?></th>
		<th><?php __('Parent Id'); ?></th>
		<th class="actions"><?php __('Actions');?></th>
	</tr>
	<?php
		$i = 0;
		foreach ($log['ChildLog'] as $childLog):
			$class = null;
			if ($i++ % 2 == 0) {
				$class = ' class="altrow"';
			}
		?>
		<tr<?php echo $class;?>>
			<td><?php echo $childLog['id'];?></td>
			<td><?php echo $childLog['created'];?></td>
			<td><?php echo $childLog['modified'];?></td>
			<td><?php echo $childLog['source'];?></td>
			<td><?php echo $childLog['user_id'];?></td>
			<td><?php echo $childLog['level_id'];?></td>
			<td><?php echo $childLog['subject'];?></td>
			<td><?php echo $childLog['detail'];?></td>
			<td><?php echo $childLog['parent_id'];?></td>
			<td class="actions">
				<?php echo $this->Html->link(__('View', true), array('controller' => 'logs', 'action' => 'view', $childLog['id'])); ?>
				<?php echo $this->Html->link(__('Edit', true), array('controller' => 'logs', 'action' => 'edit', $childLog['id'])); ?>
				<?php echo $this->Html->link(__('Delete', true), array('controller' => 'logs', 'action' => 'delete', $childLog['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $childLog['id'])); ?>
			</td>
		</tr>
	<?php endforeach; ?>
	</table>
<?php endif; ?>

	<div class="actions">
		<ul>
			<li><?php echo $this->Html->link(__('New Child Log', true), array('controller' => 'logs', 'action' => 'edit',$log['log']['id']));?> </li>
		</ul>
	</div>
</div>
<div class="related">
	<h3><?php __('Related Properties');?></h3>
	<?php if (!empty($log['Property'])):?>
	<table cellpadding = "0" cellspacing = "0">
	<tr>
		<th><?php __('Id'); ?></th>
		<th><?php __('Log Id'); ?></th>
		<th><?php __('Name'); ?></th>
		<th><?php __('Value'); ?></th>
		<th class="actions"><?php __('Actions');?></th>
	</tr>
	<?php
		$i = 0;
		foreach ($log['Property'] as $property):
			$class = null;
			if ($i++ % 2 == 0) {
				$class = ' class="altrow"';
			}
		?>
		<tr<?php echo $class;?>>
			<td><?php echo $property['id'];?></td>
			<td><?php echo $property['log_id'];?></td>
			<td><?php echo $property['name'];?></td>
			<td><?php echo $property['value'];?></td>
			<td class="actions">
				<?php echo $this->Html->link(__('View', true), array('controller' => 'properties', 'action' => 'view', $property['id'])); ?>
				<?php echo $this->Html->link(__('Edit', true), array('controller' => 'properties', 'action' => 'edit', $property['id'])); ?>
				<?php echo $this->Html->link(__('Delete', true), array('controller' => 'properties', 'action' => 'delete', $property['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $property['id'])); ?>
			</td>
		</tr>
	<?php endforeach; ?>
	</table>
<?php endif; ?>

	<div class="actions">
		<ul>
			<li><?php echo $this->Html->link(__('New Property', true), array('controller' => 'properties', 'action' => 'add'));?> </li>
		</ul>
	</div>
</div>
<div class="related">
	<h3><?php __('Related Uploads');?></h3>
	<?php if (!empty($log['Upload'])):?>
	<table cellpadding = "0" cellspacing = "0">
	<tr>
		<th><?php __('Id'); ?></th>
		<th><?php __('Log Id'); ?></th>
		<th><?php __('Name'); ?></th>
		<th><?php __('Store'); ?></th>
		<th><?php __('Type'); ?></th>
		<th><?php __('Size'); ?></th>
		<th><?php __('Created'); ?></th>
		<th><?php __('Modified'); ?></th>
		<th class="actions"><?php __('Actions');?></th>
	</tr>
	<?php
		$i = 0;
		foreach ($log['Upload'] as $upload):
			$class = null;
			if ($i++ % 2 == 0) {
				$class = ' class="altrow"';
			}
		?>
		<tr<?php echo $class;?>>
			<td><?php echo $upload['id'];?></td>
			<td><?php echo $upload['log_id'];?></td>
			<td><?php echo $upload['name'];?></td>
			<td><?php echo $upload['store'];?></td>
			<td><?php echo $upload['type'];?></td>
			<td><?php echo $upload['size'];?></td>
			<td><?php echo $upload['created'];?></td>
			<td><?php echo $upload['modified'];?></td>
			<td class="actions">
				<?php echo $this->Html->link(__('View', true), array('controller' => 'uploads', 'action' => 'view', $upload['id'])); ?>
				<?php echo $this->Html->link(__('Edit', true), array('controller' => 'uploads', 'action' => 'edit', $upload['id'])); ?>
				<?php echo $this->Html->link(__('Delete', true), array('controller' => 'uploads', 'action' => 'delete', $upload['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $upload['id'])); ?>
			</td>
		</tr>
	<?php endforeach; ?>
	</table>
<?php endif; ?>

	<div class="actions">
		<ul>
			<li><?php echo $this->Html->link(__('New Upload', true), array('controller' => 'uploads', 'action' => 'add'));?> </li>
		</ul>
	</div>
</div>
<div class="related">
	<h3><?php __('Related Wikis');?></h3>
	<?php if (!empty($log['Wiki'])):?>
	<table cellpadding = "0" cellspacing = "0">
	<tr>
		<th><?php __('Id'); ?></th>
		<th><?php __('Log Id'); ?></th>
		<th><?php __('Wiki'); ?></th>
		<th><?php __('Author'); ?></th>
		<th><?php __('Url'); ?></th>
		<th><?php __('History'); ?></th>
		<th><?php __('Time'); ?></th>
		<th class="actions"><?php __('Actions');?></th>
	</tr>
	<?php
		$i = 0;
		foreach ($log['Wiki'] as $wiki):
			$class = null;
			if ($i++ % 2 == 0) {
				$class = ' class="altrow"';
			}
		?>
		<tr<?php echo $class;?>>
			<td><?php echo $wiki['id'];?></td>
			<td><?php echo $wiki['log_id'];?></td>
			<td><?php echo $wiki['wiki'];?></td>
			<td><?php echo $wiki['author'];?></td>
			<td><?php echo $wiki['url'];?></td>
			<td><?php echo $wiki['history'];?></td>
			<td><?php echo $wiki['time'];?></td>
			<td class="actions">
				<?php echo $this->Html->link(__('View', true), array('controller' => 'wikis', 'action' => 'view', $wiki['id'])); ?>
				<?php echo $this->Html->link(__('Edit', true), array('controller' => 'wikis', 'action' => 'edit', $wiki['id'])); ?>
				<?php echo $this->Html->link(__('Delete', true), array('controller' => 'wikis', 'action' => 'delete', $wiki['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $wiki['id'])); ?>
			</td>
		</tr>
	<?php endforeach; ?>
	</table>
<?php endif; ?>

	<div class="actions">
		<ul>
			<li><?php echo $this->Html->link(__('New Wiki', true), array('controller' => 'wikis', 'action' => 'add'));?> </li>
		</ul>
	</div>
</div>
<div class="related">
	<h3><?php __('Related Tags');?></h3>
	<?php if (!empty($log['Tag'])):?>
	<table cellpadding = "0" cellspacing = "0">
	<tr>
		<th><?php __('Id'); ?></th>
		<th><?php __('Name'); ?></th>
		<th><?php __('Status'); ?></th>
		<th><?php __('Created'); ?></th>
		<th class="actions"><?php __('Actions');?></th>
	</tr>
	<?php
		$i = 0;
		foreach ($log['Tag'] as $tag):
			$class = null;
			if ($i++ % 2 == 0) {
				$class = ' class="altrow"';
			}
		?>
		<tr<?php echo $class;?>>
			<td><?php echo $tag['id'];?></td>
			<td><?php echo $tag['name'];?></td>
			<td><?php echo $tag['LogsTag']['status'];?></td>
			<td><?php echo $tag['LogsTag']['created'];?></td>
			<td class="actions">
				<?php echo $this->Html->link(__('View', true), array('controller' => 'logs_tags', 'action' => 'view', $tag['LogsTag']['id'])); ?>
				<?php echo $this->Html->link(__('Edit', true), array('controller' => 'logs_tags', 'action' => 'edit', $tag['LogsTag']['id'])); ?>
				<?php echo $this->Html->link(__('Delete', true), array('controller' => 'logs_tags', 'action' => 'delete', $tag['LogsTag']['id']), null, sprintf(__('Are you sure you want to delete # %s?', true), $tag['name'])); ?>
			</td>
		</tr>
	<?php endforeach; ?>
	</table>
<?php endif; ?>

	<div class="actions">
		<ul>
			<li><?php echo $this->Html->link(__('New Tag', true), array('controller' => 'logs_tags', 'action' => 'add', $log['log']['id']));?> </li>
		</ul>
	</div>
</div>

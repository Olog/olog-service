ALTER TABLE `logs_logbooks` 
 DROP FOREIGN KEY `logs_logbooks_logbook_id_fk`,
 DROP INDEX `logbook_id_fk` ;

ALTER TABLE `logs_logbooks` 
 MODIFY COLUMN `log_id` INT(11) UNSIGNED NOT NULL,
 MODIFY COLUMN `logbook_id` INT(11) UNSIGNED NOT NULL;

ALTER TABLE `logs_logbooks` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `logs_attributes` 
 DROP FOREIGN KEY `logs_attributes_attribute_id_fk`,
 DROP INDEX `logs_attributes_attribute_id_fk` ;

ALTER TABLE `logs_attributes` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `log_id` INT(11) UNSIGNED NOT NULL,
 MODIFY COLUMN `attribute_id` INT(11) UNSIGNED NOT NULL;

ALTER TABLE `logs`
 MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `properties`
 MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `attributes` 
 MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `property_id` INT(11) UNSIGNED NOT NULL;

ALTER TABLE `subscriptions` 
 MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `tag_id` INT(11) UNSIGNED NOT NULL,
 DROP FOREIGN KEY `subscriptions_tag_id_fk`;

ALTER TABLE `logbooks` 
 MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `is_tag` INT(1) UNSIGNED NOT NULL DEFAULT 0;

ALTER TABLE `attributes` 
 ADD CONSTRAINT `attributes_property_id_fk` FOREIGN KEY `attributes_property_id_fk` (`property_id`)
    REFERENCES `properties` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `logs_attributes` 
 ADD CONSTRAINT `logs_attributes_attribute_id_fk` FOREIGN KEY `logs_attributes_attribute_id_fk` (`attribute_id`)
    REFERENCES `attributes` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
 ADD CONSTRAINT `logs_attributes_log_id_fk` FOREIGN KEY `logs_attributes_log_id_fk` (`log_id`)
    REFERENCES `logs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `logs_logbooks` ADD CONSTRAINT `log_id_fk` FOREIGN KEY `log_id_fk` (`log_id`)
    REFERENCES `logs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
 ADD CONSTRAINT `logbook_id_fk` FOREIGN KEY `logbook_id_fk` (`logbook_id`)
    REFERENCES `logbooks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `subscriptions` ADD CONSTRAINT `subscriptions_tag_id_fk` FOREIGN KEY `subscriptions_tag_id_fk` (`tag_id`)
    REFERENCES `logbooks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;


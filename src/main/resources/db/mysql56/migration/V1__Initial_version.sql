-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.41


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema ologdb4
--
DROP DATABASE IF EXISTS olog;
CREATE DATABASE olog;
USE olog;

--
-- Definition of table `levels`
--

DROP TABLE IF EXISTS `levels`;
CREATE TABLE `levels` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `levels`
--

/*!40000 ALTER TABLE `levels` DISABLE KEYS */;
INSERT INTO `levels` (`id`,`name`) VALUES
 (1,'Info'),
 (4,'Problem'),
 (3,'Request'),
 (2,'Suggestion'),
 (5,'Urgent');
/*!40000 ALTER TABLE `levels` ENABLE KEYS */;


--
-- Definition of table `logbooks`
--

DROP TABLE IF EXISTS `logbooks`;
CREATE TABLE `logbooks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `is_tag` int(1) NOT NULL DEFAULT '0',
  `owner` varchar(45) DEFAULT NULL,
  `status_id` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `logbooks_status_id_fk` (`status_id`),
  CONSTRAINT `logbooks_status_id_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `logbooks`
--

/*!40000 ALTER TABLE `logbooks` DISABLE KEYS */;
INSERT INTO `logbooks` (`id`,`name`,`is_tag`,`owner`,`status_id`) VALUES 
 (1,'Operations',0,NULL,1),
 (2,'Electronics Maintenance',0,NULL,1),
 (3,'Mechanical Technicians',0,NULL,1),
 (4,'LOTO',0,NULL,1),
 (5,'Inverpower Power Supplies',1,NULL,1),
 (6,'RF Area',1,NULL,1),
 (7,'Kicker',1,NULL,1),
 (8,'Bumps',1,NULL,1),
 (9,'Septums',1,NULL,1),
 (10,'Large Power Supplies',1,NULL,1),
 (11,'Timing Systems',1,NULL,1);
/*!40000 ALTER TABLE `logbooks` ENABLE KEYS */;


--
-- Definition of table `logs`
--

DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `source` varchar(80) NOT NULL DEFAULT '',
  `owner` varchar(32) NOT NULL,
  `level_id` int(11) NOT NULL DEFAULT '1',
  `status_id` int(11) NOT NULL DEFAULT '1',
  `description` mediumtext NOT NULL,
  `md5entry` varchar(32) NOT NULL DEFAULT '',
  `md5recent` mediumtext NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `level_id_fk` (`level_id`),
  KEY `log_parent_id_fk` (`parent_id`),
  KEY `status_id_fk` (`status_id`),
  CONSTRAINT `level_id_fk` FOREIGN KEY (`level_id`) REFERENCES `levels` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `log_parent_id_fk` FOREIGN KEY (`parent_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `status_id_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


--
-- Definition of table `logs_logbooks`
--

DROP TABLE IF EXISTS `logs_logbooks`;
CREATE TABLE `logs_logbooks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `logbook_id` int(11) NOT NULL,
  `state` enum('open','closed') DEFAULT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status_id` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `log_id_fk` (`log_id`),
  KEY `logbook_id_fk` (`logbook_id`) USING BTREE,
  KEY `logs_logbooks_status_id_fk` (`status_id`),
  CONSTRAINT `logs_logbooks_status_id_fk` FOREIGN KEY (`status_id`) REFERENCES `statuses` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `logs_logbooks_logbook_id_fk` FOREIGN KEY (`logbook_id`) REFERENCES `logbooks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `logs_logbooks_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


--
-- Definition of table `properties`
--

DROP TABLE IF EXISTS `properties`;
CREATE TABLE  `properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `status_id` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `properties`
--

/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;

--
-- Definition of table `attributes`
--

DROP TABLE IF EXISTS `attributes`;
CREATE TABLE  `attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `property_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `status_id` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `attributes_property_id_fk` (`property_id`),
  CONSTRAINT `attributes_property_id_fk` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

--
-- Definition of table `logs_attributes`
--

DROP TABLE IF EXISTS `logs_attributes`;
CREATE TABLE  `logs_attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `attribute_id` int(11) NOT NULL,
  `value` varchar(200) NOT NULL,
  `grouping_num` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `logs_attributes_attribute_id_fk` (`attribute_id`),
  KEY `logs_attributes_log_id_fk` (`log_id`),
  CONSTRAINT `logs_attributes_attribute_id_fk` FOREIGN KEY (`attribute_id`) REFERENCES `attributes` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `logs_attributes_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8;

--
-- Definition of table `statuses`
--

DROP TABLE IF EXISTS `statuses`;
CREATE TABLE `statuses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `statuses`
--

/*!40000 ALTER TABLE `statuses` DISABLE KEYS */;
INSERT INTO `statuses` (`id`,`name`) VALUES 
 (1,'Active'),
 (2,'Inactive');
/*!40000 ALTER TABLE `statuses` ENABLE KEYS */;


--
-- Definition of table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
CREATE TABLE `subscriptions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) NOT NULL,
  `email` varchar(250) NOT NULL DEFAULT '',
  `webhook` varchar(250) DEFAULT NULL,
  `level_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `subscriptions_tag_id_fk` (`tag_id`),
  CONSTRAINT `subscriptions_tag_id_fk` FOREIGN KEY (`tag_id`) REFERENCES `logbooks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `subscriptions`
--

/*!40000 ALTER TABLE `subscriptions` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscriptions` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
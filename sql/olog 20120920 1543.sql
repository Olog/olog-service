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
-- Create schema olog
--

CREATE DATABASE IF NOT EXISTS olog;
USE olog;

--
-- Definition of table `attributes`
--

DROP TABLE IF EXISTS `attributes`;
CREATE TABLE `attributes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `property_id` int(11) NOT NULL,
  `name` varchar(200) NOT NULL,
  `state` enum('Active','Inactive') NOT NULL DEFAULT 'Active',
  PRIMARY KEY (`id`),
  KEY `attributes_property_id_fk` (`property_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `attributes`
--

/*!40000 ALTER TABLE `attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `attributes` ENABLE KEYS */;


--
-- Definition of table `entries`
--

DROP TABLE IF EXISTS `entries`;
CREATE TABLE `entries` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `created` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `entries`
--

/*!40000 ALTER TABLE `entries` DISABLE KEYS */;
/*!40000 ALTER TABLE `entries` ENABLE KEYS */;


--
-- Definition of table `logbooks`
--

DROP TABLE IF EXISTS `logbooks`;
CREATE TABLE `logbooks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `is_tag` int(1) NOT NULL DEFAULT '0',
  `owner` varchar(45) DEFAULT NULL,
  `state` enum('Active','Inactive') NOT NULL DEFAULT 'Active',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logbooks`
--

/*!40000 ALTER TABLE `logbooks` DISABLE KEYS */;
INSERT INTO `logbooks` (`id`,`name`,`is_tag`,`owner`,`state`) VALUES 
 (1,'Operations',0,'LAB.FRIB.ASD','Active'),
 (2,'Electronics Maintenance',0,NULL,'Active'),
 (3,'Mechanical Technicians',0,NULL,'Active'),
 (4,'LOTO',0,NULL,'Active'),
 (5,'Inverpower Power Supplies',1,NULL,'Active'),
 (6,'RF Area',1,NULL,'Active'),
 (7,'Kicker',1,NULL,'Active'),
 (8,'Bumps',1,NULL,'Active'),
 (9,'Septums',1,NULL,'Active'),
 (10,'Large Power Supplies',1,NULL,'Active'),
 (11,'Timing Systems',1,NULL,'Active');
/*!40000 ALTER TABLE `logbooks` ENABLE KEYS */;


--
-- Definition of table `logs`
--

DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `modified` datetime NOT NULL,
  `source` varchar(80) NOT NULL DEFAULT '',
  `owner` varchar(32) NOT NULL,
  `description` mediumtext NOT NULL,
  `md5entry` varchar(32) NOT NULL DEFAULT '',
  `state` enum('Active','Inactive') NOT NULL DEFAULT 'Active',
  `level` enum('Info','Problem','Request','Suggestion','Urgent') NOT NULL DEFAULT 'Info',
  `entry_id` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `entry_id_fk` (`entry_id`),
  CONSTRAINT `entry_id_fk` FOREIGN KEY (`entry_id`) REFERENCES `entries` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logs`
--

/*!40000 ALTER TABLE `logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs` ENABLE KEYS */;


--
-- Definition of table `logs_attributes`
--

DROP TABLE IF EXISTS `logs_attributes`;
CREATE TABLE `logs_attributes` (
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logs_attributes`
--

/*!40000 ALTER TABLE `logs_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs_attributes` ENABLE KEYS */;


--
-- Definition of table `logs_logbooks`
--

DROP TABLE IF EXISTS `logs_logbooks`;
CREATE TABLE `logs_logbooks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `log_id` int(11) NOT NULL,
  `logbook_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `log_id_fk` (`log_id`),
  KEY `logbook_id_fk` (`logbook_id`) USING BTREE,
  CONSTRAINT `logs_logbooks_logbook_id_fk` FOREIGN KEY (`logbook_id`) REFERENCES `logbooks` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `logs_logbooks_log_id_fk` FOREIGN KEY (`log_id`) REFERENCES `logs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `logs_logbooks`
--

/*!40000 ALTER TABLE `logs_logbooks` DISABLE KEYS */;
/*!40000 ALTER TABLE `logs_logbooks` ENABLE KEYS */;


--
-- Definition of table `properties`
--

DROP TABLE IF EXISTS `properties`;
CREATE TABLE `properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `state` enum('Active','Inactive') NOT NULL DEFAULT 'Active',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `properties`
--

/*!40000 ALTER TABLE `properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `properties` ENABLE KEYS */;


--
-- Definition of table `statuses`
--

DROP TABLE IF EXISTS `statuses`;
CREATE TABLE `statuses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

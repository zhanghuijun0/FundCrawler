/*
Navicat MySQL Data Transfer

Source Server         : 218.240.49.29
Source Server Version : 50541
Source Host           : 218.240.49.29:3306
Source Database       : finance

Target Server Type    : MYSQL
Target Server Version : 50541
File Encoding         : 65001

Date: 2015-10-20 12:34:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fund
-- ----------------------------
DROP TABLE IF EXISTS `fund`;
CREATE TABLE `fund` (
  `f_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '基金id',
  `f_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '基金名称',
  `f_code` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '基金代码',
  `f_ft_id` int(11) DEFAULT NULL COMMENT '基金分类',
  `f_i_id` int(11) NOT NULL COMMENT '所属机构',
  `f_date` date NOT NULL COMMENT '成立日期',
  PRIMARY KEY (`f_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for fund_dailyprice
-- ----------------------------
DROP TABLE IF EXISTS `fund_dailyprice`;
CREATE TABLE `fund_dailyprice` (
  `fdp_id` int(11) NOT NULL AUTO_INCREMENT,
  `fdp_f_id` int(11) NOT NULL COMMENT '基金id',
  `fdp_date` date NOT NULL COMMENT '日期',
  `fdp_price` decimal(11,4) NOT NULL COMMENT '基金单位净值',
  PRIMARY KEY (`fdp_id`),
  UNIQUE KEY `pri` (`fdp_f_id`,`fdp_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for fund_type
-- ----------------------------
DROP TABLE IF EXISTS `fund_type`;
CREATE TABLE `fund_type` (
  `ft_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '基金分类id',
  `ft_name` varchar(255) NOT NULL COMMENT '分类名称',
  `ft_parentid` int(11) NOT NULL COMMENT '父分类id',
  PRIMARY KEY (`ft_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for institution
-- ----------------------------
DROP TABLE IF EXISTS `institution`;
CREATE TABLE `institution` (
  `i_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '机构id',
  `i_name` varchar(255) NOT NULL COMMENT '机构名称',
  `i_code` varchar(255) DEFAULT NULL COMMENT '机构代码',
  `i_it_id` int(11) DEFAULT NULL COMMENT '机构类型',
  PRIMARY KEY (`i_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for institution_type
-- ----------------------------
DROP TABLE IF EXISTS `institution_type`;
CREATE TABLE `institution_type` (
  `it_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '机构分类id',
  `it_name` varchar(255) NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`it_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Procedure structure for check_fund
-- ----------------------------
DROP PROCEDURE IF EXISTS `check_fund`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_fund`(IN `nam` varchar(255),IN `cod` varchar(255),IN `type_id` int,IN `institution_id` int,IN `s_date` date,OUT `id` int)
BEGIN
	declare num int default 0;

	SELECT COUNT(*) INTO num from fund WHERE f_code = cod;
	IF(num=0)THEN
			INSERT INTO fund (f_name,f_code,f_ft_id,f_i_id,f_date) VALUES (nam,cod,type_id,institution_id,s_date);
	END IF;
	SELECT f_id INTO id FROM fund WHERE f_code = cod;
	#调用示例：CALL check_fund('兴全全球视野股票',340006,2,3,'2006-09-20',@id);
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for check_institution
-- ----------------------------
DROP PROCEDURE IF EXISTS `check_institution`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_institution`(IN `institution_name` varchar(255),IN `type_name` varchar(255),OUT `id` int)
BEGIN
	declare num int default 0;
	declare institution_id int default 0;
	
	#机构分类是否存在
	SELECT COUNT(*) INTO num FROM institution_type WHERE it_name = type_name;
	IF(num=0)	THEN
		INSERT INTO institution_type (it_name) VALUES (type_name);
	END IF;
	SELECT it_id INTO institution_id FROM institution_type WHERE it_name = type_name;

	#机构是否存在
	SELECT COUNT(*) INTO num FROM institution WHERE i_name = institution_name;
	IF(num=0)THEN
		INSERT INTO institution (i_name,i_it_id) VALUES (institution_name,institution_id);
	END IF;
	
	SELECT i_id INTO id FROM institution WHERE i_name = institution_name;

	#调用示例：CALL check_institution('张慧俊','学校',@id);
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for check_price
-- ----------------------------
DROP PROCEDURE IF EXISTS `check_price`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_price`(IN `fund_id` int,IN `n_date` date,IN `price` double,OUT `id` int)
BEGIN
	declare num int default 0;
	SELECT COUNT(*) FROM fund_dailyprice WHERE fdp_f_id = fund_id AND fdp_date = n_date;
	IF(num=0)THEN
		INSERT INTO fund_dailyprice (fdp_f_id,fdp_date,fdp_price) VALUES (fund_id,n_date,price);
	END IF;
	#调用示例：CALL check_price(2,'2015-10-10',2.2583,@id);
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for check_type
-- ----------------------------
DROP PROCEDURE IF EXISTS `check_type`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `check_type`(IN `option1` varchar(255),IN `option2` varchar(255),IN `option3` varchar(255),OUT `id` int)
BEGIN
	
	declare num int default 0;
	declare parentid int default 0;

	#检查第一级分类是否存在
	SELECT COUNT(*) INTO num FROM fund_type WHERE ft_name = option1;
	IF(num=0)	THEN
	INSERT INTO fund_type (ft_name,ft_parentid) VALUES (option1,0);
	END IF;

	#得到第二级分类的id
	SELECT ft_id INTO parentid FROM fund_type WHERE ft_name = option1;
	
	SELECT COUNT(*) INTO num FROM fund_type WHERE ft_name = option2;
	IF(num=0)	THEN
	INSERT INTO fund_type (ft_name,ft_parentid) VALUES (option2,parentid);
	END IF;

	#得到第三级分类的id
	SELECT ft_id INTO parentid FROM fund_type WHERE ft_name = option2;
	
	SELECT COUNT(*) INTO num FROM fund_type WHERE ft_name = option3;
	IF(num=0)	THEN
	INSERT INTO fund_type (ft_name,ft_parentid) VALUES (option3,parentid);
	END IF;

	SELECT ft_id INTO id FROM fund_type WHERE ft_name = option3;
	#调用示例：CALL check_type('混合基金','绝对收益目标基金','灵活策略基金',@id);
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for show_counts
-- ----------------------------
DROP PROCEDURE IF EXISTS `show_counts`;
DELIMITER ;;
CREATE DEFINER=`admin`@`%` PROCEDURE `show_counts`(OUT `id1` int,OUT `id2` int,OUT `id3` int,OUT `id4` int)
BEGIN
	SELECT count(1) INTO id1 from fund;	
	SELECT count(1) INTO id2 from fund_dailyprice;
	SELECT count(1) INTO id3 from fund_type;
	SELECT count(1) INTO id4 from institution;
	
	#调用示例：CALL show_counts(@id1,@id2,@id3,@id4);
END
;;
DELIMITER ;

insert into institution_type (it_name) values ('银行');
insert into institution_type (it_name) values ('基金公司');
insert into institution_type (it_name) values ('信托公司');
insert into institution_type (it_name) values ('券商');
insert into institution_type (it_name) values ('基金子公司');
insert into institution_type (it_name) values ('保险公司');

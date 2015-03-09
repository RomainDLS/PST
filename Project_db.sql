SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';


CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`music_database`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`music_database` ;

CREATE TABLE IF NOT EXISTS `mydb`.`music_database` (
  `idmusic_database` INT NOT NULL AUTO_INCREMENT,
  `title` MEDIUMTEXT NOT NULL,
  `artiste` VARCHAR(45) NOT NULL,
  `year` YEAR NULL,
  `album` MEDIUMTEXT NULL,
  `type` MEDIUMTEXT NULL,
  `comment` LONGTEXT NULL, 
  PRIMARY KEY (`idmusic_database`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`signature`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`signature` ;

CREATE TABLE IF NOT EXISTS `mydb`.`signature` (
  `music` INT NOT NULL,
  `hash` INT NOT NULL,
  `time` INT NOT NULL,
  INDEX `idmusic_idx` (`music` ASC),
  CONSTRAINT `idmusic`
    FOREIGN KEY (`music`)
    REFERENCES `mydb`.`music_database` (`idmusic_database`))
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

CREATE DATABASE IF NOT EXISTS `my-library`;
CREATE USER IF NOT EXISTS `library-app` IDENTIFIED BY 'library-app';
GRANT ALL PRIVILEGES ON `my-library`.* TO 'library-app'@'%';

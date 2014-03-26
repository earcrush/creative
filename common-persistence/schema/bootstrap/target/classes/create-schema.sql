DROP DATABASE IF EXISTS earcrush_vle;
/
CREATE DATABASE earcrush_vle;
/
GRANT ALL PRIVILEGES ON earcrush_vle.* TO 'earcrush_vle'@'%' IDENTIFIED BY 'earcrush' WITH GRANT OPTION;
/
GRANT ALL PRIVILEGES ON earcrush_vle.* TO 'earcrush_vle'@'localhost' IDENTIFIED BY 'earcrush' WITH GRANT OPTION;
/
FLUSH PRIVILEGES;
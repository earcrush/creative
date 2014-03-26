DROP DATABASE IF EXISTS ${earcrush-common-persistence.database.name};
/
CREATE DATABASE ${earcrush-common-persistence.database.name};
/
GRANT ALL PRIVILEGES ON ${earcrush-common-persistence.database.name}.* TO '${earcrush-common-persistence.database.user}'@'%' IDENTIFIED BY 'earcrush' WITH GRANT OPTION;
/
GRANT ALL PRIVILEGES ON ${earcrush-common-persistence.database.name}.* TO '${earcrush-common-persistence.database.user}'@'localhost' IDENTIFIED BY 'earcrush' WITH GRANT OPTION;
/
FLUSH PRIVILEGES;
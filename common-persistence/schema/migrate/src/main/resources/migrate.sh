#!/bin/bash

# To be prompted for a password rather than forced to give on command-line,
# symlink to this file where symlink name includes 'secure'
#   example: ln -s migrate.sh migrate-secure.sh
#  `./migrate-secure.sh -h host -r port -u username -d database`

# -c allows the use of specific liquibase commands http://www.liquibase.org/manual/command_line
# -c "rollbackCount 1" will rollback one schema changelog

secure="no"
me=`basename $0`
[ ! $me == ${me/secure/} ] && secure="yes"

USAGE="usage: $0 [-h host] [-r port] [-u username] [-p password] [-d database] [-c liquibase_command]"

host=
port=
username=
password=
database=
classpath=
liquibase_command="update"

# Get options
while getopts h:r:u:p:d:c: opt
do
    echo "opt=$opt arg=$OPTARG"
    case "$opt" in
      h)  host="$OPTARG";;
      r)  port="$OPTARG";;
      u)  username="$OPTARG";;
      p)  password="$OPTARG";;
      d)  database="$OPTARG";;
      c)  liquibase_command="$OPTARG";;
      \?)       # unknown flag
          echo >&2 $USAGE
      exit 1;;
    esac
done

if [ $secure == "yes" ]; then
  echo -n "Enter password: "
  stty -echo
  read password
  stty echo
fi

# check args set
if [[ -z $host || -z $port || -z $username || -z $password || -z $database || -z $liquibase_command ]]; then
    echo >&2 $USAGE
    exit 1
fi

echo "Ok: host=$host port=$port username=$username password=$password database=$database liquibase_command=$liquibase_command"

if [[ -d lib ]]; then
  for file in `ls ./lib/*.jar`
    do
      classpath="$file:$classpath"
    done
fi

echo "classpath: $classpath"

# spit out version.properties
if [ -e version.properties ]; then
  echo "attempting liquibase command \"$liquibase_command\".  Version properties: "
  cat version.properties;
fi

java -jar ./lib/liquibase-core-2.0.5.jar \
          --driver=oracle.jdbc.OracleDriver \
          --classpath="$classpath" \
          --changeLogFile=schema-changelog.xml \
          --url="jdbc:oracle:thin:@$host:$port:$database" \
          --username="$username" \
          --password="$password" \
          $liquibase_command

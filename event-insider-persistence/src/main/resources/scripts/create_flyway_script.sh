#!/bin/bash

DESC=$1

if [ -z "$DESC" ]; then
  echo "Usage: ./create_migration.sh add_table"
  exit 1
fi

TIMESTAMP=$(date +"%Y%m%d%H%M%S")

FILENAME="V${TIMESTAMP}__${DESC}.sql"

touch db/migration/$FILENAME

echo "Created: $FILENAME"
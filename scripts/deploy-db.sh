#!/bin/bash
set -e

# Load environment variables from .env (skip invalid bash identifiers)
if [ -f .env ]; then
  export $(grep -v '^#' .env | grep -E '^[a-zA-Z_][a-zA-Z0-9_]*=' | xargs)
else
  echo ".env file not found!"
  exit 1
fi

# Default dump file name if not set
DUMP_FILE=${DUMP_FILE:-db_dump.sql}

# Step 1: Dump from origin remote DB
echo "Dumping DB from origin remote host ($REMOTE_HOST_ORIGIN)..."
PGPASSWORD=$REMOTE_DB_PASSWORD_ORIGIN pg_dump \
  -h $REMOTE_HOST_ORIGIN \
  -U $REMOTE_DB_USER_ORIGIN \
  -d $REMOTE_DB_NAME_ORIGIN \
  -F p > "$DUMP_FILE"

echo "Origin dump completed: $DUMP_FILE"

# Replace origin role with destination role in the dump
echo "Replacing origin role '$REMOTE_DB_USER_ORIGIN' with destination role '$REMOTE_DB_USER'..."
sed -i "s/${REMOTE_DB_USER_ORIGIN}/\"${REMOTE_DB_USER}\"/g" "$DUMP_FILE"

# Replace origin DB name with destination DB name in the dump
echo "Replacing origin DB name '$REMOTE_DB_NAME_ORIGIN' with destination DB name '$REMOTE_DB_NAME'..."
sed -i "s/$REMOTE_DB_NAME_ORIGIN/$REMOTE_DB_NAME/g" "$DUMP_FILE"

# Step 2: Drop all tables on the remote DB
echo "Cleaning remote DB on EC2..."
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -c \
  "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

echo "Remote DB cleaned."

# Step 3: Disconnect active sessions to avoid locks
echo "Disconnecting active sessions..."
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -c \
"REVOKE CONNECT ON DATABASE \"$REMOTE_DB_NAME\" FROM PUBLIC;
 SELECT pg_terminate_backend(pid)
 FROM pg_stat_activity
 WHERE datname = '$REMOTE_DB_NAME' AND pid <> pg_backend_pid();"

# Step 4: Restore the dump to the remote DB
echo "Restoring dump to remote DB..."
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -a -v ON_ERROR_STOP=1 < "$DUMP_FILE"

# Step 5: Re-enable DB connections
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -c \
"GRANT CONNECT ON DATABASE \"$REMOTE_DB_NAME\" TO PUBLIC;"

echo "✅ Remote DB successfully updated!"

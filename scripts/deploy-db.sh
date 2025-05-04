#!/bin/bash
set -e

# Load environment variables from .env
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo ".env file not found!"
  exit 1
fi

# Default dump file name if not set, and expand ~ if present
DUMP_FILE=${DUMP_FILE:-db_dump.sql}

# Step 1: Dump local DB from Docker container
echo "Dumping local DB from Docker container..."
docker exec -e PGPASSWORD=$LOCAL_DB_PASSWORD $LOCAL_CONTAINER_NAME \
  pg_dump -U $LOCAL_DB_USER -d $LOCAL_DB_NAME -F p > "$DUMP_FILE"

echo "Local dump completed: $DUMP_FILE"

# Step 2: Drop all tables on the remote DB
echo "Cleaning remote DB on EC2..."
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -c \
  "DROP SCHEMA public CASCADE; CREATE SCHEMA public;"

echo "Remote DB cleaned."

# Step 3: Disconnect active sessions to avoid locks
echo "Disconnecting active sessions..."
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -c \
"REVOKE CONNECT ON DATABASE $REMOTE_DB_NAME FROM PUBLIC;
 SELECT pg_terminate_backend(pid)
 FROM pg_stat_activity
 WHERE datname = '$REMOTE_DB_NAME' AND pid <> pg_backend_pid();"

# Step 4: Restore the dump to the remote DB
echo "Restoring dump to remote DB..."
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -a -v ON_ERROR_STOP=1 < "$DUMP_FILE"

# Step 5: Re-enable DB connections
PGPASSWORD=$REMOTE_DB_PASSWORD psql -h $REMOTE_HOST -U $REMOTE_DB_USER -d $REMOTE_DB_NAME -c \
"GRANT CONNECT ON DATABASE $REMOTE_DB_NAME TO PUBLIC;"

echo "✅ Remote DB successfully updated!"

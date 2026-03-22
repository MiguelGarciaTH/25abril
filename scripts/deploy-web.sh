#!/bin/bash

set -e

#scp -r arquivo-web arquivo-25-abril-ec2:
rsync -avzL --delete --progress --rsync-path="sudo rsync" arquivo-web/dist/ aws-prod-server:/var/www/html/arquivo-web/

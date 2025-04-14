#!/bin/bash

set -e

#scp -r arquivo-web arquivo-25-abril-ec2:
rsync -avzL --delete --progress arquivo-web/dist/ arquivo-25-abril-ec2:/var/www/html/arquivo-web/

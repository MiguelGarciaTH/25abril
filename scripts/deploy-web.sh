#!/bin/bash

set -e

#scp -r arquivo-web arquivo-25-abril-ec2:
rsync -avz --delete arquivo-web/dist/ arquivo-25-abril-ec2:/var/www/html

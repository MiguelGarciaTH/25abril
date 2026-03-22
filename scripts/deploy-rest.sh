#!/bin/bash

set -e 

cd "$(dirname "$0")/.."

ssh aws-prod-server 'rm *.deb'

scp $(find -name "*.deb") aws-prod-server:

ssh aws-prod-server 'sudo dpkg -i *.deb; sudo systemctl restart arquivo-rest; journalctl -fu arquivo-rest'

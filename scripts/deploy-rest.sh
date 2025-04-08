#!/bin/bash

set -e 

cd "$(dirname "$0")/.."

ssh arquivo-25-abril-ec2 'rm *.deb'

scp $(find -name "*.deb") arquivo-25-abril-ec2:

ssh arquivo-25-abril-ec2 'sudo dpkg -i *.deb; sudo systemctl restart arquivo-rest; journalctl -fu arquivo-rest'

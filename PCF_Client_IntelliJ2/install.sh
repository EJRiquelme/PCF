#!/bin/bash

# Instalar Java
wget -q -O - https://download.bell-sw.com/pki/GPG-KEY-bellsoft | sudo apt-key add -
echo "deb [arch=amd64] https://apt.bell-sw.com/ stable main" | sudo tee /etc/apt/sources.list.d/bellsoft.list

sudo apt-get update
sudo apt-get install bellsoft-java11

# Instalar tu aplicación
cp ./bin/PCF_Client_IntelliJ2.jar /usr/local

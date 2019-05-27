#!/bin/bash

if [[ -d "/usr/share/tp" ]]; then
 cd /usr/share/tp
 sudo rm -rf TransferTool.jar
 sudo rm -rf /opt/TransferTool
 echo 'Removed old installation';
fi

echo 'Downloading latest transferTool sourcecode from git repositories';
cd /opt
sudo git clone https://github.com/San-Ny/TransferTool

echo 'Moving jar';
sudo cp /opt/TransferTool/out/artifacts/TransferTool_jar/TransferTool.jar /usr/share/tp/TransferTool.jar
sudo cp /opt/TransferTool/install.sh /usr/share/tp/install.sh

echo 'Creating command "tp"'

if [[ -e "/usr/bin/tp" ]]; then
  echo "File /usr/bin/tp already exists, updating it"
  sudo rm -rf "/usr/bin/tp";
fi

echo "#!/bin/bash" >> /usr/bin/tp
echo 'if [[ $1 == "update" ]]; then' >> /usr/bin/tp
echo "sudo /usr/share/tp/install.sh"
echo "else"
echo "sudo java -jar /usr/share/tp/TransferTool.jar $@" >> /usr/bin/tp
echo "fi"

sudo chmod u+x "/usr/bin/tp"
sudo chmod u+x "/usr/share/tp/install.sh"

echo ""
echo "use 'sudo tp' to use the new tool"
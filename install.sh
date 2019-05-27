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

echo 'Installing on /usr/share/tp';
sudo cp /opt/TransferTool/out/artifacts/TransferTool_jar/TransferTool.jar /usr/share/tp/TransferTool.jar
sudo cp /opt/TransferTool/install.sh /usr/share/tp/install.sh

echo 'Creating command tp'

if [[ -e "/usr/bin/tp" ]]; then
    read -r -p "Command tp already exists, overwrite it? [y/N] " response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])+$ ]]
    then
        sudo rm -rf "/usr/bin/tp";
        echo "#!/bin/bash" >> /usr/bin/tp
        echo 'if [[ $1 == "update" ]]; then' >> /usr/bin/tp
        echo "sudo /usr/share/tp/install.sh" >> /usr/bin/tp
        echo "else" >> /usr/bin/tp
        echo "sudo java -jar /usr/share/tp/TransferTool.jar $@" >> /usr/bin/tp
        echo "fi" >> /usr/bin/tp
    else
        echo -e "\nCommand not updated\n"
    fi
fi

sudo chmod u+x "/usr/bin/tp"
sudo chmod u+x "/usr/share/tp/install.sh"

echo ""
echo -e "use 'sudo tp' to use the new tool\n"
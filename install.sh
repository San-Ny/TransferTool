#!/bin/bash

VERSION=0.0.1

#path vars
CONFIG_PATH=/etc/transfertool
APPLICATION_PATH=/usr/share/tp
APPLICATION_SOURCE=/opt/TransferTool
#file vars
COMMAND_FILE=/usr/bin/tp
MAN_FILE=/usr/share/man/man1/tp.1.gz

#file writer with echos
function create_file_command {
    echo "#!/bin/bash" >> ${COMMAND_FILE}
    echo "" >> ${COMMAND_FILE}
    echo "VERSION=0.0.1" >> ${COMMAND_FILE}
    echo "" >> ${COMMAND_FILE}
    echo "CONFIG_PATH=/etc/transfertool" >> ${COMMAND_FILE}
    echo "APPLICATION_PATH=/usr/share/tp" >> ${COMMAND_FILE}
    echo "APPLICATION_SOURCE=/opt/TransferTool" >> ${COMMAND_FILE}
    echo "COMMAND_FILE=/usr/bin/tp" >> ${COMMAND_FILE}
    echo '' >> ${COMMAND_FILE}
    echo "function remove_old() {" >> ${COMMAND_FILE}
    echo '    sudo rm -rf ${APPLICATION_PATH}' >> ${COMMAND_FILE}
    echo '    sudo rm -rf ${APPLICATION_SOURCE}' >> ${COMMAND_FILE}
    echo '    sudo rm -rf ${CONFIG_PATH}' >> ${COMMAND_FILE}
    echo '    sudo rm -rf ${COMMAND_FILE}' >> ${COMMAND_FILE}
    echo '}' >> ${COMMAND_FILE}
    echo '' >> ${COMMAND_FILE}
    echo "function help_message() {" >> ${COMMAND_FILE}
    echo '    echo -e "tp usage:\n\t help -> this message\n\t version -> current program version\n\t update -> run installation script to get latest release from git\n\t remove -> remove application"' >> ${COMMAND_FILE}
    echo '}' >> ${COMMAND_FILE}
    echo '' >> ${COMMAND_FILE}
    echo 'if [[ $1 == "update" ]]; then' >> ${COMMAND_FILE}
    echo " sudo /usr/share/tp/install.sh" >> ${COMMAND_FILE}
    echo 'elif [[ $1 == "remove" ]]; then' >> ${COMMAND_FILE}
    echo " remove_old" >> ${COMMAND_FILE}
    echo 'elif [[ $1 == "help" ]]; then' >> ${COMMAND_FILE}
    echo " help_message" >> ${COMMAND_FILE}
    echo 'elif [[ $1 == "version" ]]; then' >> ${COMMAND_FILE}
    echo ' echo "Current installed version of TransferTool -> ${VERSION}"' >> ${COMMAND_FILE}
    echo 'else' >> ${COMMAND_FILE}
    echo ' sudo java -jar /usr/share/tp/TransferTool.jar $@' >> ${COMMAND_FILE}
    echo "fi" >> ${COMMAND_FILE}
}

#create needed dirs
function create_dirs() {
    sudo mkdir ${CONFIG_PATH}
    sudo mkdir ${CONFIG_PATH}/keys
    sudo mkdir ${CONFIG_PATH}/properties
    sudo mkdir ${APPLICATION_PATH}
}

function remove_old() {
    sudo rm -rf ${APPLICATION_PATH}
    sudo rm -rf ${APPLICATION_SOURCE}
    sudo rm -rf ${CONFIG_PATH}
    sudo rm -rf ${COMMAND_FILE}
    sudo rm -rf ${MAN_FILE}
}

function update_permissions() {
    sudo chmod u+x "/usr/bin/tp"
    sudo chmod u+x "/usr/share/tp/install.sh"
    sudo chmod +x "/usr/bin/tp"
}

function download() {
    cd /opt
    sudo git clone https://github.com/San-Ny/TransferTool
}

function move_files() {
    sudo cp ${APPLICATION_SOURCE}/out/artifacts/TransferTool_jar/TransferTool.jar ${APPLICATION_PATH}/TransferTool.jar
    sudo cp ${APPLICATION_SOURCE}/install.sh ${APPLICATION_PATH}/install.sh
    sudo cp ${APPLICATION_SOURCE}/transfertool.conf ${CONFIG_PATH}/properties/transfertool.conf
    sudo cp ${APPLICATION_SOURCE}/bkg.jpeg ${CONFIG_PATH}/properties/bkg.jpeg
    yes | sudo cp ${APPLICATION_SOURCE}/tp.1.gz ${MAN_FILE}
    sudo updatedb
}

if [[ $1 == "" ]]; then
    if [[ -e ${COMMAND_FILE} ]]; then
        read -r -p "Overwrite old installation? [y/N] " response
        if [[ "$response" =~ ^([yY][eE][sS]|[yY])+$ ]]
        then
            echo "Removing old files"
            remove_old
            create_dirs
            echo 'Downloading latest transferTool sourcecode from git repositories';
            download
            move_files
            echo "Creating command tp"
            create_file_command
            update_permissions
        else
            echo -e "tp not reinstalled\n"
        fi
    else
        echo 'Installing on /usr/share/tp';
        create_dirs
        download
        move_files
        create_file_command
        update_permissions
    fi
elif [[ $1 == "version" ]]; then
 echo ${VERSION}
fi

echo -e "\nUse 'tp' to use TransferTool\n"
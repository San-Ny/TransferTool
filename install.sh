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
    echo "#!/bin/bash" >> /usr/bin/tp
    echo "" >> /usr/bin/tp
    echo "VERSION=0.0.1" >> /usr/bin/tp
    echo "" >> /usr/bin/tp
    echo "CONFIG_PATH=/etc/transfertool" >> /usr/bin/tp
    echo "APPLICATION_PATH=/usr/share/tp" >> /usr/bin/tp
    echo "APPLICATION_SOURCE=/opt/TransferTool" >> /usr/bin/tp
    echo "COMMAND_FILE=/usr/bin/tp" >> /usr/bin/tp
    echo '' >> /usr/bin/tp
    echo "function remove_old() {" >> /usr/bin/tp
    echo '    sudo rm -rf ${APPLICATION_PATH}' >> /usr/bin/tp
    echo '    sudo rm -rf ${APPLICATION_SOURCE}' >> /usr/bin/tp
    echo '    sudo rm -rf ${CONFIG_PATH}' >> /usr/bin/tp
    echo '    sudo rm -rf ${COMMAND_FILE}' >> /usr/bin/tp
    echo '}' >> /usr/bin/tp
    echo '' >> /usr/bin/tp
    echo "function help_message() {" >> /usr/bin/tp
    echo '    echo -e "tp usage:\n\t help -> this message\n\t version -> current program version\n\t update -> run installation script to get latest release from git\n\t remove -> remove application"' >> /usr/bin/tp
    echo '}' >> /usr/bin/tp
    echo '' >> /usr/bin/tp
    echo 'if [[ $1 == "update" ]]; then' >> ${COMMAND_FILE}
    echo " sudo /usr/share/tp/install.sh" >> ${COMMAND_FILE}
    echo 'elif [[ $1 == "remove" ]]; then' >> ${COMMAND_FILE}
    echo " remove_old" >> ${COMMAND_FILE}
    echo 'elif [[ $1 == "help" ]]; then' >> ${COMMAND_FILE}
    echo " help_message" >> ${COMMAND_FILE}
    echo 'elif [[ $1 == "version" ]]; then' >> ${COMMAND_FILE}
    echo ' echo ${VERSION}' >> ${COMMAND_FILE}
    echo 'else' >> ${COMMAND_FILE}
    echo " sudo java -jar /usr/share/tp/TransferTool.jar $@" >> ${COMMAND_FILE}
    echo "fi" >> ${COMMAND_FILE}
}

function create_man_page() {
    echo 'Manpage for tp.' >> ${MAN_FILE}
    echo ' code on https://github.com/San-Ny/TransferTool' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
    echo '' >> ${MAN_FILE}
}

#create needed dirs
function create_dirs() {
    sudo mkdir ${CONFIG_PATH}
    sudo mkdir ${APPLICATION_PATH}
}

function remove_old() {
    sudo rm -rf ${APPLICATION_PATH}
    sudo rm -rf ${APPLICATION_SOURCE}
    sudo rm -rf ${CONFIG_PATH}
    sudo rm -rf ${COMMAND_FILE}
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
    sudo cp /opt/TransferTool/out/artifacts/TransferTool_jar/TransferTool.jar /usr/share/tp/TransferTool.jar
    sudo cp /opt/TransferTool/install.sh /usr/share/tp/install.sh
    sudo cp /opt/TransferTool/transfertool.conf ${CONFIG_PATH}
    yes | sudo cp /opt/TransferTool/tp.1.gz ${MAN_FILE}
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
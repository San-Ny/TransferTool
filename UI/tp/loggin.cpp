#include "loggin.h"
#include "ui_loggin.h"
#include <libssh/libssh.h>
#include <QDebug>
#include "session.h"
#include <string>
#include <sstream>
#include <vector>
#include <iterator>
#include <QColorDialog>

loggin::loggin(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::loggin)
{
    ui->setupUi(this);
    ui->txt_logging_passwd->setEchoMode(QLineEdit::Password);

    //background
    QPixmap bkgnd("/etc/transfertool/properties/bkg.jpeg");
    bkgnd = bkgnd.scaled(this->size(), Qt::IgnoreAspectRatio);
    QPalette palette;
    palette.setBrush(QPalette::Background, bkgnd);
    this->setPalette(palette);
}

loggin::~loggin()
{
    delete ui;
}

template<typename Out>
void split(const std::string &s, char delim, Out result) {
    std::stringstream ss(s);
    std::string item;
    while (std::getline(ss, item, delim)) {
        *(result++) = item;
    }
}

std::vector<std::string> split(const std::string &s, char delim) {
    std::vector<std::string> elems;
    split(s, delim, std::back_inserter(elems));
    return elems;
}

void loggin::on_btn_logging_loggin_clicked()
{
    qDebug() << "Starting connection process";

    //getting text & conversion
    std::string user = ui->txt_logging_user->text().toLatin1().constData();
    std::string port = ui->txt_loggin_port->text().toLatin1().constData();
    std::string s_passwd = ui->txt_logging_passwd->text().toStdString();

    char* passwd = const_cast<char*>(s_passwd.c_str());

    //empty strings
    if(user == "" && s_passwd == "")
    {
        qDebug() << "empty fields!";
        return;
    }

    //split
    std::vector<std::string> v = split(user, '@');

    //testing purposes printing passwd
    //qDebug() << "password->" << passwd;

    Session *session = new Session();

    int rt = session->connect(passwd, v[0].c_str(), v[1].c_str(), port.c_str());

    if(rt == 0)
    {
        qDebug() << "connection succesful";
        return;
    }
    else
    {
        qDebug() << "connection unsuccesful";
        return;
    }
}

void loggin::on_btn_loggin_color_clicked()
{
    QColor color = QColorDialog::getColor(Qt::white, this);
    QPalette palette = ui->lbl_loggin_user->palette();
    palette.setColor(QPalette::WindowText, color);
    ui->lbl_loggin_user->setPalette(palette);
    ui->lbl_loggin_passwd->setPalette(palette);
}

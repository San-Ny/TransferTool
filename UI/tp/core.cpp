#include "core.h"
#include "session.h"
#include "ui_core.h"
#include <QTabWidget>

Core::Core(QWidget *parent, Session session):QDialog(parent), ui(new Ui::Core)
{
    ui->setupUi(this);
    this->session = session;
}

Core::~Core()
{
    delete ui;
}

void Core::on_btn_core_close_clicked()
{
    this->session.desconect();
}

void Core::on_btn_scp_send_clicked()
{
    //send file via scp

}

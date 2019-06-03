#ifndef LOGGIN_H
#define LOGGIN_H

#include "core.h"

#include <QMainWindow>
#include <QCloseEvent>

namespace Ui {
class loggin;
}

class loggin : public QMainWindow
{
    Q_OBJECT

public:
    explicit loggin(QWidget *parent = nullptr);
    ~loggin();

private slots:
    void on_btn_logging_loggin_clicked();

    void on_btn_loggin_color_clicked();

private:
    Ui::loggin *ui;
    Core *core;
};

#endif // LOGGIN_H

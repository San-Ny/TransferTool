#ifndef CORE_H
#define CORE_H

#include "session.h"

#include <QDialog>

namespace Ui {
class Core;
}

class Core : public QDialog
{
    Q_OBJECT

public:
    explicit Core(QWidget *parent = nullptr);
    ~Core();

    Core(QWidget *parent, Session session);
private slots:
    void on_btn_core_close_clicked();

    void on_btn_scp_send_clicked();

private:
    Ui::Core *ui;
    Session session;
};

#endif // CORE_H

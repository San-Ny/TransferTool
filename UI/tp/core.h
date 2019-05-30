#ifndef CORE_H
#define CORE_H

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

private:
    Ui::Core *ui;
};

#endif // CORE_H

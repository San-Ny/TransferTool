#include "loggin.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication a(argc, argv);
    loggin w;
    w.show();

    return a.exec();
}

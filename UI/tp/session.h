#ifndef SESSION_H
#define SESSION_H


#define LIBSSH_STATIC 1
#include <libssh/libssh.h>

#include <QCloseEvent>


class Session
{
private:
    ssh_session session;
    const char* password;
public:
    Session(void);
    Session(char *password,const char* user, const char* host);
    Session(char *password, const char* user, const char* host, const char* port);
    int verify_knownhost(void);
    int execute(const char* command);
    int desconect(void);
    int connect(char *password, const char* user, const char* host, const char* port);
    const char* get_error(void);
public:
    void closeEvent(QCloseEvent *event);
};

#endif // SESSION_H

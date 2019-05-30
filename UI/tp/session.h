#ifndef SESSION_H
#define SESSION_H


#define LIBSSH_STATIC 1
#include <libssh/libssh.h>


class Session
{
private:
    ssh_session session;
public:
    Session(void);
    Session(char *password,const char* user, const char* host);
    Session(char *password, const char* user, const char* host, const char* port);
    int verify_knownhost(void);
    int execute(const char* command);
    int desconect(void);
    int connect(char *password, const char* user, const char* host, const char* port);
};

#endif // SESSION_H

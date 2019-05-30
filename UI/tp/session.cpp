#include "session.h"
#define LIBSSH_STATIC 1
#include <libssh/libssh.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>
#include <QDebug>


Session::Session()
{

}

Session::Session(char *password,const char* user, const char* host)
{
    if(connect(password, user, host, "22") != 0) qDebug() << "Unable to create connection";
}

Session::Session(char *password, const char* user, const char* host, const char* port)
{
    if(connect(password, user, host, port) != 0) qDebug() << "Unable to create connection";
}

int Session::connect(char *password, const char* user, const char* host, const char* port){
    int rc;

    // Open session and set options
    this->session = ssh_new();
    if (this->session == nullptr) return -1;

    qDebug() << host;
    qDebug() << port;
    qDebug() << user;

    ssh_options_set(this->session, SSH_OPTIONS_HOST, host);
    ssh_options_set(this->session, SSH_OPTIONS_USER, user);
    ssh_options_set(this->session, SSH_OPTIONS_PORT, port);

    // Connect to server
    rc = ssh_connect(this->session);
    if (rc != SSH_OK)
    {
        fprintf(stderr, "Error connecting to localhost: %s\n", ssh_get_error(this->session));
        ssh_free(this->session);
        return -1;
    }
    // Verify the server's identity
    // For the source code of verify_knownhost(), check previous example
    if (verify_knownhost() < 0)
    {
        ssh_disconnect(this->session);
        ssh_free(this->session);
        return -1;
    }

    rc = ssh_userauth_password(this->session, nullptr, password);
    if (rc != SSH_AUTH_SUCCESS)
    {
        fprintf(stderr, "Error authenticating with password: %s\n", ssh_get_error(this->session));
        ssh_disconnect(this->session);
        ssh_free(this->session);
        return -1;
    }

    qDebug() << "connected";
    return 0;
}

int Session::verify_knownhost()
{
    enum ssh_known_hosts_e state;
    unsigned char *hash = nullptr;
    ssh_key srv_pubkey = nullptr;
    size_t hlen;
    char buf[10];
    char *hexa;
    char *p;
    int cmp;
    int rc;
    rc = ssh_get_server_publickey(this->session, &srv_pubkey);
    if (rc < 0) {
        return -1;
    }
    rc = ssh_get_publickey_hash(srv_pubkey, SSH_PUBLICKEY_HASH_SHA1, &hash, &hlen);
    ssh_key_free(srv_pubkey);
    if (rc < 0) {
        return -1;
    }
    state = ssh_session_is_known_server(this->session);

    switch (state) {
            case SSH_KNOWN_HOSTS_OK:
                /* OK */
                break;
            case SSH_KNOWN_HOSTS_CHANGED:
                fprintf(stderr, "Host key for server changed: it is now:\n");
                ssh_print_hexa("Public key hash", hash, hlen);
                fprintf(stderr, "For security reasons, connection will be stopped\n");
                ssh_clean_pubkey_hash(&hash);
                return -1;
            case SSH_KNOWN_HOSTS_OTHER:
                fprintf(stderr, "The host key for this server was not found but an other"
                        "type of key exists.\n");
                fprintf(stderr, "An attacker might change the default server key to"
                        "confuse your client into thinking the key does not exist\n");
                ssh_clean_pubkey_hash(&hash);
                return -1;
            case SSH_KNOWN_HOSTS_NOT_FOUND:
                fprintf(stderr, "Could not find known host file.\n");
                fprintf(stderr, "If you accept the host key here, the file will be"
                        "automatically created.\n");
                /* FALL THROUGH to SSH_SERVER_NOT_KNOWN behavior */
                break;
            case SSH_KNOWN_HOSTS_UNKNOWN:
                hexa = ssh_get_hexa(hash, hlen);
                fprintf(stderr,"The server is unknown. Do you trust the host key?\n");
                fprintf(stderr, "Public key hash: %s\n", hexa);
                ssh_string_free_char(hexa);
                ssh_clean_pubkey_hash(&hash);
                p = fgets(buf, sizeof(buf), stdin);
                if (p == nullptr) {
                    return -1;
                }
                cmp = strncasecmp(buf, "yes", 3);
                if (cmp != 0) {
                    return -1;
                }
                rc = ssh_session_update_known_hosts(this->session);
                if (rc < 0) {
                    fprintf(stderr, "Error %s\n", strerror(errno));
                    return -1;
                }
                break;
            case SSH_KNOWN_HOSTS_ERROR:
                fprintf(stderr, "Error %s", ssh_get_error(this->session));
                ssh_clean_pubkey_hash(&hash);
                return -1;
    }
    ssh_clean_pubkey_hash(&hash);
    return 0;
}

int Session::execute(const char* command){
    ssh_channel channel;
    int rc;
    char buffer[256];
    int nbytes;
    channel = ssh_channel_new(this->session);
    if (channel == nullptr) return SSH_ERROR;

    rc = ssh_channel_open_session(channel);
    if (rc != SSH_OK)
    {
        ssh_channel_free(channel);
        return rc;
    }
    rc = ssh_channel_request_exec(channel, command);
    if (rc != SSH_OK)
    {
        ssh_channel_close(channel);
        ssh_channel_free(channel);
        return rc;
    }
    nbytes = ssh_channel_read(channel, buffer, sizeof(buffer), 0);
    while (nbytes > 0)
    {
        if (write(1, buffer, nbytes) != (unsigned int) nbytes)
        {
            ssh_channel_close(channel);
            ssh_channel_free(channel);
            return SSH_ERROR;
        }
        nbytes = ssh_channel_read(channel, buffer, sizeof(buffer), 0);
    }
    if (nbytes < 0)
    {
        ssh_channel_close(channel);
        ssh_channel_free(channel);
        return SSH_ERROR;
    }
    ssh_channel_send_eof(channel);
    ssh_channel_close(channel);
    ssh_channel_free(channel);
    return SSH_OK;
}

int Session::desconect(){
    ssh_disconnect(this->session);
    ssh_free(this->session);
    return 0;
}

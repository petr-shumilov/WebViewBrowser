#include <curl.h>
#include <string>
#include <sstream>
#include <stdlib.h>
#ifndef MYAPPLICATION2_NETWORK_H
#define MYAPPLICATION2_NETWORK_H


//for configure connection
enum OPTION {
    URL,
    TIMEOUT,
    USER_AGENT,
    POST,
    COOKIE
};

struct Response
{
    CURLcode status;
    std::string headers;
    std::string data;
    Response(CURLcode _status, std::string _headers, std::string _data);
};

class Network
{
public:

    Network(std::string url = "");

    ~Network();

    // setup parameters of request
    void Configure(OPTION param, const char* arg);

    // convert response to string
    static size_t write_callback(void *contents, size_t size, size_t nmemb, void *userp);

    // execute request
    Response Exec();

private:
    // main handler
    CURL *curlHandler;
};



#endif //MYAPPLICATION2_NETWORK_H

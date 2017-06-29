#include "Network.h"

Response::Response(CURLcode _status, std::string _headers, std::string _data) {
    status = _status;
    headers = _headers;
    data = _data;
}

Network::Network(const char *  url) {
    curl_global_init(CURL_GLOBAL_ALL);

    this->curlHandler = curl_easy_init();

    if (url){
        curl_easy_setopt(this->curlHandler, CURLOPT_URL, url);
    }
}

Network::~Network() {
    if (this->curlHandler)
        curl_easy_cleanup(this->curlHandler);

}

void Network::Configure(OPTION param, const char* arg) {
    switch (param) {
        case URL:
            curl_easy_setopt(this->curlHandler, CURLOPT_URL, arg);
            break;
        case TIMEOUT:
            curl_easy_setopt(this->curlHandler, CURLOPT_TIMEOUT, atoi(arg));
            break;
        case USER_AGENT:
            curl_easy_setopt(this->curlHandler, CURLOPT_USERAGENT, arg);
            break;
        case POST:
            curl_easy_setopt(this->curlHandler, CURLOPT_POSTFIELDS, arg);
            break;
        case COOKIE:
            curl_easy_setopt(this->curlHandler, CURLOPT_COOKIE, arg);
            break;
    }
}

size_t Network::write_callback(void *contents, size_t size, size_t nmemb, void *userp) {
    ((std::string*)userp)->append((char*)contents, size * nmemb);
    return size * nmemb;
}

Response Network::Exec() {

    std::string headers, plaintext;

    curl_slist *headerlist = curl_slist_append(NULL, "Expect:");

    curl_easy_setopt(this->curlHandler, CURLOPT_HTTPHEADER, headerlist);
    curl_easy_setopt(this->curlHandler, CURLOPT_FOLLOWLOCATION, true);
    curl_easy_setopt(this->curlHandler, CURLOPT_WRITEFUNCTION, Network::write_callback);
    curl_easy_setopt(this->curlHandler, CURLOPT_HEADERDATA, &headers);
    curl_easy_setopt(this->curlHandler, CURLOPT_WRITEDATA, &plaintext);
    curl_easy_setopt(this->curlHandler, CURLOPT_COOKIEFILE, "");
    curl_easy_setopt(this->curlHandler, CURLOPT_SSL_VERIFYPEER, 0);
    curl_easy_setopt(this->curlHandler, CURLOPT_SSL_VERIFYHOST, 0);

    CURLcode status = curl_easy_perform(this->curlHandler);

    return Response(status, headers, plaintext);
}

std::string Network::urlEncode(const char *source)
{
    CURL *curl = curl_easy_init();
    char *cres = curl_easy_escape(curl, source, strlen(source));
    std::string res(cres);
    curl_free(cres);
    curl_easy_cleanup(curl);
    return res;
}

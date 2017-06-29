#include <jni.h>
#include <string>
#include <sstream>
#include <stdlib.h>
#include "Network.h"
#include "YandexAPI.h"
#include "rapidjson/document.h"


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_petr_myapplication_SearchAutoCompleteAdapter_yandexSuggestAPI(JNIEnv *env, jobject instance, jstring requestPart_) {

    // build request url
    std::string url = API_URL + Network::urlEncode(env->GetStringUTFChars(requestPart_, 0));

    // init requester
    Network *connection = new Network(url.c_str());
    connection->Configure(TIMEOUT, CONNECTION_TIMEOUT);

    // execute request
    Response jsonResponse = connection->Exec();

    // results array
    jobjectArray suggestions;

    // checking connection status
    if (jsonResponse.status == CURLE_OK) {

        // init JSON parser
        rapidjson::Document jsonData;
        jsonData.Parse(jsonResponse.data.c_str());

        // API DEPENDENCE SCOPE
        if (jsonData.IsArray() && jsonData[1].IsArray()) {

            const rapidjson::Value&parsedSuggestions = jsonData[1];

            suggestions = env->NewObjectArray(parsedSuggestions.Size(), env->FindClass("java/lang/String"), env->NewStringUTF(""));

            for(size_t i = 0; i < parsedSuggestions.Size(); ++i) {
                std::string curSuggestion = parsedSuggestions[i].GetString();
                env->SetObjectArrayElement(suggestions, i, env->NewStringUTF(curSuggestion.c_str()));
            }

        }
        else {
            // return empty array if some failed
            suggestions = env->NewObjectArray(1, env->FindClass("java/lang/String"), env->NewStringUTF(""));
            env->SetObjectArrayElement(suggestions, 0, env->NewStringUTF(""));
        }
        // API DEPENDENCE SCOPE

    }
    else {
        suggestions = env->NewObjectArray(1, env->FindClass("java/lang/String"), env->NewStringUTF(""));
        env->SetObjectArrayElement(suggestions, 0, env->NewStringUTF(""));
    }

    return suggestions;
}
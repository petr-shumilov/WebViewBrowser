#ifndef MYAPPLICATION2_YANDEXAPI_H
#define MYAPPLICATION2_YANDEXAPI_H



/*
 * Request settings
 */

#define API_URL "http://suggest.yandex.ru/suggest-ff.cgi?part="
#define CONNECTION_TIMEOUT "10"


extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_example_petr_myapplication_SearchAutoCompleteAdapter_yandexSuggestAPI(JNIEnv *env, jobject instance, jstring requestPart_);

#endif //MYAPPLICATION2_YANDEXAPI_H

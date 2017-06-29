cmake_minimum_required(VERSION 3.4.1)

#TODO: FIX ABSOLUTE PATHS


add_library( # Specifies the name of the library.
             yandexAPI
             SHARED
             src/main/cpp/YandexAPI.cpp )

add_library( network
             SHARED
             src/main/cpp/Network.cpp )

add_library( curl
             STATIC
             IMPORTED )

set_target_properties(
                    curl
                    PROPERTIES IMPORTED_LOCATION
                    /home/petr/AndroidStudioProjects/MyApplication2/app/src/main/jni/${ANDROID_ABI}/libcurl.a)

add_library( libcrypto
             STATIC
             IMPORTED )

set_target_properties(
                    libcrypto
                    PROPERTIES IMPORTED_LOCATION
                    /home/petr/AndroidStudioProjects/MyApplication2/app/src/main/jni/${ANDROID_ABI}/libcrypto.a)

add_library( libssl
             STATIC
             IMPORTED )

set_target_properties(
                    libssl
                    PROPERTIES IMPORTED_LOCATION
                    /home/petr/AndroidStudioProjects/MyApplication2/app/src/main/jni/${ANDROID_ABI}/libssl.a)


find_library( log-lib log zlib )

include_directories( /home/petr/AndroidStudioProjects/MyApplication2/app/src/main/cpp/curl )


target_link_libraries(network curl libcrypto libssl z)
target_link_libraries(yandexAPI network)






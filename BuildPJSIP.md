# PJSIP Build for Android

## Reference Docs,
1. https://docs.pjsip.org/en/2.16/get-started/android/requirements.html
2. https://docs.pjsip.org/en/2.16/get-started/android/build_instructions.html

All tools installation should be done from reference docs only, e.g, NDK, OpenSSL, pjproject etc


## 1. Install SWIG
  As per official docs [Reference Doc 1](https://docs.pjsip.org/en/2.16/get-started/android/requirements.html)

## 2. Download and extract Oboe
  As per official docs [Reference Doc 1](https://docs.pjsip.org/en/2.16/get-started/android/requirements.html)

## 3. Install openh264 (optional)
  ### for openh264,

  ```$ export ANDROID_SDK_ROOT=<ANDROID SDK PATH>```
  ```$ export ANDROID_NDK_HOME=$ANDROID_SDK_ROOT/<ANDROID NDK PATH>```
  ```$ export PATH=$ANDROID_NDK_HOME/toolchains/llvm/prebuilt/darwin-x86_64/bin:$PATH```

  ```$ make clean```
  ```$ rm -rf codec/build obj```

  ```$ make OS=android    ARCH=arm   TARGET=android-24   NDKROOT=$ANDROID_NDK_HOME   CFLAGS="-fPIC -falign-functions=16"  LDFLAGS="-Wl,-z,max-page-size=16384"  libraries -j$(sysctl -n hw.ncpu)```


## 4. Install OpenSSL
  As per official docs [Reference Doc 1](https://docs.pjsip.org/en/2.16/get-started/android/requirements.html)

## 5. for opus,
  ```$ export API_LEVEL=28``` // require API level 28 and above
  
  for armv8a
  ```$ cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK_ROOT/build/cmake/android.toolchain.cmake  -DANDROID_ABI=arm64-v8a -DANDROID_PLATFORM=android-$API_LEVEL -DCMAKE_BUILD_TYPE=Release -DOPUS_BUILD_SHARED_LIBRARY=OFF -DCMAKE_INSTALL_PREFIX=/Users/hardikchauhan/Downloads/opus/opus-dev-lib/arm64-v8a```

  ```$ cmake --build . --target install```
  ```$ export OPUS_DIR=/Users/hardikchauhan/Downloads/opus/opus-dev-lib/arm64-v8a```


  for armeabi-v7a
  ```$ cmake -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK_HOME/build/cmake/android.toolchain.cmake -DANDROID_ABI=armeabi-v7a -DANDROID_PLATFORM=android-$API_LEVEL -DCMAKE_BUILD_TYPE=Release -DOPUS_BUILD_SHARED_LIBRARY=OFF -DCMAKE_INSTALL_PREFIX=/Users/hardikchauhan/Downloads/opus/opus-dev-lib//armeabi-v7a```

  ```$ cmake --build . --target install```
  ```$ export OPUS_DIR=/Users/hardikchauhan/Downloads/opus/opus-dev-lib/armeabi-v7a```


## 6. Download PJSIP 
  As per official docs -> get the specific version commit from github [Reference Doc 1](https://docs.pjsip.org/en/2.16/get-started/android/requirements.html)

## 7. Create config_site.h - 

```
/* Activate Android specific settings in the 'config_site_sample.h' */
#define PJ_CONFIG_ANDROID 1
#include <pj/config_site_sample.h>

#define PJMEDIA_HAS_VIDEO 1
#define PJMEDIA_HAS_OPUS_CODEC     1
#define PJMEDIA_HAS_G711_CODEC     1   // usually already enabled
```

## 8. Configuring PJSIP
  MUST be in the pjproject path

  ```$ cd /path/to/pjproject ```
  ```$ export ANDROID_NDK_ROOT=<ANDROID NDK PATH>```

  for arm64-v8a
  ```$ APP_PLATFORM=24 CFLAGS="-D__BIONIC_NO_PAGE_SIZE_MACRO" LDFLAGS="-Wl,-z,max-page-size=16384" ./configure-android --with-opus=$OPUS_DIR -with-ssl=$OPENSSL_DIR --with-oboe=$OBOE_DIR --disable-gnutls```


  for armeabi-v7a
  ```$ APP_PLATFORM=24 TARGET_ABI=armeabi-v7a CFLAGS="-D__BIONIC_NO_PAGE_SIZE_MACRO" LDFLAGS="-Wl,-z,max-page-size=16384" ./configure-android --with-opus=$OPUS_DIR -with-ssl=$OPENSSL_DIR --with-oboe=$OBOE_DIR --disable-gnutls```

  $ make dep
  $ make clean
  $ make


## 9. Verifying configuration
THIS IS MUST,

> Check that OPUS is detected and enabled


> Check that OpenSSL is detected and enabled:
> checking for OpenSSL installations..
> checking for openssl/ssl.h... yes
> checking for ERR_load_BIO_strings in -lcrypto... yes
> checking for SSL_CTX_new in -lssl... yes
> OpenSSL library found, SSL support enabled


> Check that Oboe is detected and enabled:
> checking Oboe usability... yes
> checking sound device backend... Oboe


## Building PJSUA2 Java interface with SWIG
  As per official docs
  [Reference Doc 2](https://docs.pjsip.org/en/2.16/get-started/android/build_instructions.html)



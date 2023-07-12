# Phone.com VoIP Android SDK


## Architecture

This library wraps the standard SIP service bindings in a background service and completely hides SIP from the rest of the client application, to be able to have VoIP capabilities at a high level of abstraction. You can talk to the service using static methods and you will receive broadcast intents as a response. To talk to the service, refer to [PhoneComServiceCommand](https://github.com/phonedotcom/pdc-voip-android-sdk/blob/develop/sipservice/src/main/java/com/phone/sip/PhoneComServiceCommand.java) static methods. To receive events from the service, extend [BroadcastEventReceiver](https://github.com/phonedotcom/pdc-voip-android-sdk/blob/develop/sipservice/src/main/java/com/phone/sip/BroadcastEventReceiver.java). 
	All the commands that you will send to the service will get executed in the background and without blocking your main thread. Once the service has done the requested job or operation, it will notify you in a callback method in a class extended from BroadcastEvenReceiver. So, you don't risk blocking your UI thread in any way.


#### What is implemented and working

- Single account
- In-Call operations
  - mute
  - unmute
  - hold
  - un-hold
  - send DTMF (RFC 2833)
- Accept an incoming call
- Answer with video an incoming call
- Decline an incoming call
- Get/Set codec priorities
- Hang up all active calls
- Hold all active calls
- Hold/Decline sip call when incoming/outgoing gsm call
- Video support
  - mute/unmute video
  - video preview
- Get Call Status
- Sip Credential encryption on device.

### Used Libraries versions

- PJSIP: 2.12.1
- GSON: 2.9.1
- RxJava: 2.2.18


### How to get our library in your project

1. Add Jitpack repository to your build.gradle file (project level)

    ```java
    allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    }
	```

2. Add the dependency in your build.gradle file (module level)  
[![](https://jitpack.io/v/phonedotcom/pdc-voip-android-sdk.svg)](https://jitpack.io/#phonedotcom/pdc-voip-android-sdk)
	```java
	dependencies {
	        implementation 'com.github.phonedotcom:pdc-voip-android-sdk:version'
	}
	```
3. Phone.com Service Configuration and Initialization
This class helps to make client ready for communication with SIP server. All you need to provide is basic data while configuration. 
	 
	 - Configure FCM Push Notification
		```java 
			val configFCMPushNotification = ConfigureFCMPushNotification(
	                "<Device Registration Token>",
                    BuildConfig.VERSION_NAME,
                    BuildConfig.APPLICATION_ID,
                    "<Device Information>",
                    "<FCM Application ID>",
                    "<Device Type i.e Android>",
                    "<Sip Credential VoIP ID>",
	                "<Sip Extension ID>"
	        )
		```
            
      - Configure SIP Account
	      ```java 
				val configSip = ConfigureSip(
                  "<SIP Username>",
                  "<SIP Password>",
                  "<SIP Domain>",
                  "<SIP Port>",
                  "<SIP Secure Port>",
                  "<SIP Protocal Secure>",
                  "<SIP Protocol Name>"
              )
		```   

	- Configure Foreground Service Notification
	    ```java 
			val configPhoneServiceNotification = ConfigurePhoneServiceNotification(
                "<Client App Name>",
                "<Foreground Service Notification Message>",
                <App Icon Resource>
            )
		```   

	- Now set configuration and build ***PhoneComService*** instance
	    ```java 
			val phoneComService = PhoneComService.Builder()
							.setFcmRegistrationDetails(configFCMPushNotification)
							.setSipInitializationDetails(configSip)
							.setForegroundServiceNotificationDetails(configPhoneServiceNotification
							.build(this)
		```   

      


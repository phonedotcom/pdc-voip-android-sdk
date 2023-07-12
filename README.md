
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

```kotlin
allprojects {
	repositories {
		...
    		maven { url 'https://jitpack.io' }
		...
    }
}
```
<br/>

2. Add the dependency in your build.gradle file (module level)  
[![](https://jitpack.io/v/phonedotcom/pdc-voip-android-sdk.svg)](https://jitpack.io/#phonedotcom/pdc-voip-android-sdk)
```kotlin
dependencies {
	implementation 'com.github.phonedotcom:pdc-voip-android-sdk:version'
}
```
<br/>
	
3. Phone.com Service Configuration and Initialization
This class helps to make client ready for communication with SIP server. All you need to provide is basic data while configuration. 
	 
- Configure FCM Push Notification
```kotlin 
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
```kotlin 
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
```kotlin 
val configPhoneServiceNotification = ConfigurePhoneServiceNotification(
	"<Client App Name>",
	"<Foreground Service Notification Message>",
	<App Icon Resource>
)
```   
  -  Now set configuration and build ***PhoneComService*** instance
```kotlin 
val phoneComService = PhoneComService.Builder()
	.setFcmRegistrationDetails(configFCMPushNotification)
	.setSipInitializationDetails(configSip)
	.setForegroundServiceNotificationDetails(configPhoneServiceNotification
	.build(this)
```   
<br/>
      
4. #### Event broadcast receiver - Receive callback back to the application
- BroadcastEventReceiver class helps client application to receive callback events corresponding to the commands given to library. You have to create a class and extend `BroadcastEventReceiver` class of library and override pre-defined methods created for specific purpose.
```kotlin
class CallEventBroadcastReceiver : com.phone.sip.BroadcastEventReceiver() {

	private lateinit var context: Context
	override fun onReceive(context: Context?, intent: Intent?) {  
        	super.onReceive(context, intent)
		if (intent == null) return
		context?.let {
			this.context = it 
		}
	}  
  
	override fun onInitialize(initializeStatus: InitializeStatus) {  
        	super.onInitialize(initializeStatus)    
    	}  
  
    	override fun onCallEvent(event: CallEvent) {  
        	super.onCallEvent(event)  
    	}  
  
    	override fun onIncomingCall(incomingCallData: IncomingCallData, isAnyActiveCall: Boolean) {  
        	super.onIncomingCall(incomingCallData, isAnyActiveCall)
	}  
  
    	override fun onMissedCall(missedCallData: MissedCallData) {  
        	super.onMissedCall(missedCallData)  
    	}  
  
    	override fun onCallMediaEvent(mediaEvent: Int) {  
        	super.onCallMediaEvent(mediaEvent)  
    	}  
  
    	override fun onCallMediaState(stateType: MediaState?, stateValue: Boolean) {  
        	super.onCallMediaState(stateType, stateValue)    
  	}  
}
```
 - Register `CallEventBroadcastReceiver` and start receiving callbacks
Register `CallEventBroadcastReceiver` class created by extending `BroadcastEventReceiver`, in the `onCreate()` of Application class. So the callback can be received any time while the process of client application is running in system.
```kotlin
class AndroidApplication : Application() {
	override fun onCreate() {  
		super.onCreate()
	    	...
		callEventBroadcastReceiver.register(this)
		...
	}
}
```
<br/>

5. #### Call the library methods to perform tasks
`PhoneComServiceCommand` - This class allows you to communicate with the library. There are several static methods which allows to send commands to library to perform different tasks like login with sip account, connect incoming call, mute/unmute call, notify for missed call, approve or decline the entry request and etc.
List of all the commands support are as follows,

- `phoneComService.initialize()`
This method initialize the library. It gets all the information provided with **[Phone.com Service Configuration and Initialization](#phone.com-service-configuration-and-initialization)** section

- `PhoneComServiceCommand.unregisterPushAndLogout(<Application Context>)` 
This method unregister the firebase messaging service and  logout user so application will stop receiving push notification and calls from sip library

- `PhoneComServiceCommand.acceptIncomingCall(<Application Context>, <Boolean isVideo>)`
This method allows client application to accept the incoming call, and also check weather it is a video call or not.

- `PhoneComServiceCommand.hangUpActiveCalls(<Application Context>)` 
This method allows application to hangup or disconnect an active call.

- `PhoneComServiceCommand.setCallMute(<Application Context>, <Boolean mute/unmute>)` 
This method allows application to mute or unmute the active call.

- `PhoneComServiceCommand.setupIncomingVideoFeed(<Application Context>, <android.VideoView surface>)`
This method allows application to start showing incoming video feed from call.

- `PhoneComServiceCommand.sendDTMF(<Application Context>, <String key>)`
This method allows application to send a DTMF tone to library to perform various operations. most common use case for this is to play keypad tone.

- `PhoneComServiceCommand.declineIncomingCall(context)`
This method allows application to decline an incoming call.

- `PhoneComServiceCommand.rejectCallUserBusy(context)`
This method allows application to reject an incoming call automatically when user is busy on another call from other application.
 <br/>
 
6. Configure Channels to host Notification

Channel created with id SERVICE_NOTIFICATION_CHANNEL_ID, helps library to host service notification generated for Foreground service. 

```kotlin 
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
	createChannelId(
		PhoneComServiceConstants.SERVICE_NOTIFICATION_CHANNEL_ID,
		false,
		NotificationManager.IMPORTANCE_DEFAULT,
		getString(R.string.service_notification_channel_name)
	)
}        
```
<br/>   

7. Once mobile device is registered with Firebase Cloud Messaging (FCM), client app should start getting push notification in FirebaseMessagingService class. 

`PhoneComFirebaseMessageHelper` class helps to validate and process message `data` extracted from `RemoteMessage` received in `onMessageReceived(...)` 

```kotlin
var data: String? = ""
data = if (remoteMessageData.containsKey("data")) {
		remoteMessageData["data"]
	} else if (remoteMessageData.containsKey("default")) {
		remoteMessageData["default"]
	}
```

 

   `PhoneComFirebaseMessageHelper.processMessageData` process on push notification data and send a callback based on the notification type i.e Incoming call or Missed call 

> (For more details on callback methods, please refer documents related to ***BroadcastEventReceiver***)

```kotlin
if (PhoneComFirebaseMessageHelper.validate(messageData)) {
	PhoneComFirebaseMessageHelper.processMessageData(this, messageData)
}
```
 

package net.gotev.sipservice

import android.content.Context
import android.media.MediaPlayer
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.regex.Pattern

object Utility {

    object String {
        fun validateString(value: kotlin.String?): Boolean {
            return value != null && value.trim { it <= ' ' }.isNotEmpty()
        }
    }

    object Sip {


        /**
         * This method is used to get uri of Sip user
         *
         * @param username username
         * @param context Android context needed
         * @return sip user uri
         */
        fun getSipUserUri(
           username: kotlin.String?,
            context: Context
        ): kotlin.String {
            var usernameShadow = username
            val domainName = SipApplication.getDomainName(context)
            try {
                usernameShadow = URLEncoder.encode(username, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            return kotlin.String.format(
                "sip:%s@%s:%d;%s",
                usernameShadow,
                domainName,
                getPortNumber(context),
                getProtocolName(context)
            )
        }


        /**
         * This method is used to get protocol name.
         *
         * @param context Android context needed
         * @return protocol name
         */
        private fun getProtocolName(context: Context): kotlin.String {
            val secureProtocolName = SipApplication.getSecureProtocolName(context)
            val protocolName = SipApplication.getProtocolName(context)
            return if (SharedPreferencesHelper.getInstance(context)
                    .isSecureProtocol(context)) secureProtocolName else protocolName
        }


        /**
         * This method is used to get port number.
         *
         * @param context Android context needed
         * @return port number
         */
        private fun getPortNumber(context: Context): Int {
            val port = SipApplication.getPort(context)
            val securedPort = SipApplication.getSecurePort(context)
            return if (SharedPreferencesHelper.getInstance(context)
                    .isSecureProtocol(context)) securedPort else port
        }


        /**
         * This method is used to get uri of domain.
         *
         * @param context Android context needed
         * @return uri of domain
         */
        fun getDomainUri(context: Context): kotlin.String? {
            val protocolName = getProtocolName(context)
            val domainName = SipApplication.getDomainName(context)
            return kotlin.String.format(
                "sip:%s:%d;%s",
                domainName,
                getPortNumber(context),
                protocolName
            )
        }


        /**
         * This method is used to get time in seconds.
         *
         * @return current time in seconds
         */
        fun timeInSeconds(): Long {
            val millis = System.currentTimeMillis()
            return millis / 1000
        }


        /**
         * This method is used to get uuid from the header message passed.
         *
         * @param header header message
         * @return uuid
         */
        fun getUUIDFromHeader(header: kotlin.String?): kotlin.String? {
            val patternString = "X-Linked-UUID:(.*)(?:\\s.*)*"
            val pattern = Pattern.compile(patternString)
            val matcher = pattern.matcher(header)
            var uuid = ""
            if (matcher.find()) {
                try {
                    uuid = matcher.group(1).trim { it <= ' ' }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return uuid
        }


        /**
         * This method is used to get name from the header message passed.
         *
         * @param header message
         * @return name
         */
        fun getNameFromHeader(header: kotlin.String?): kotlin.String? {
            val patternString = "From:(.*)(?:\\s.*)*"
            val pattern = Pattern.compile(patternString)
            val matcher = pattern.matcher(header)
            var uuid = ""
            if (matcher.find()) {
                try {
                    uuid = matcher.group(1).trim { it <= ' ' }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return uuid
        }


        /**
         * This method is used to play custom ringtone in device.
         *
         * @param fileName filename to play ringtone
         * @param ctx      context Android context needed
         */
        fun playSound(fileName: kotlin.String?, ctx: Context) {
            val p = MediaPlayer()
            try {
                val afd = ctx.assets.openFd(fileName!!)
                p.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                p.prepare()
                p.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /**
         * This method is used to get username from the message passed.
         *
         * @param message message
         * @return username
         */
        fun getUserName(message: kotlin.String?): kotlin.String? {
            if (message != null) {
                var name: kotlin.String? = ""
                //  String message = ":\"New extension\" <sip:525@72.1.47.164>;tag=sansay502590rdb9linkedUUID:5080e5a8-18d8-4275-a2f9-bf9537e49839";
                val strArray = message.split(":|\"|<").toTypedArray()
                for (splits in strArray) {
                    name = splits
                    if (name != null && !name.trim { it <= ' ' }.isEmpty()) break
                }
                return name!!.trim { it <= ' ' }
            }
            return message
        }

    }
}
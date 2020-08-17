package org.nexttracks.android.messages

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.nexttracks.android.support.Preferences
import org.nexttracks.android.support.interfaces.IncomingMessageProcessor

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class MessageAccount(var id: Long, var username: String, var password: String, var hostname: String, var port: Int) : MessageBase() {
    public override fun getBaseTopicSuffix(): String {
        return BASETOPIC_SUFFIX
    }

    override fun processIncomingMessage(handler: IncomingMessageProcessor) {
        handler.processIncomingMessage(this)
    }

    override fun addMqttPreferences(preferences: Preferences) {
        topic = preferences.pubTopicAccounts
        qos = preferences.pubQosAccounts
        retained = preferences.pubRetainAccounts
    }

    companion object {
        const val TYPE = "account"
        private const val BASETOPIC_SUFFIX = "/event"
    }
}

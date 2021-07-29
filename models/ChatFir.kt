package com.example.templates.models

import android.util.Log
import com.example.templates.utils.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatFir(private val username: String): RequiresLogger {
    val db = Firebase.firestore
    override val TAG = "ChatFir"
    /* FIREBASE KEYS */
    val collectionK = "chats"
    val username1K = "username1"
    val username2K = "username2"
    val messageK = "message"
    val dateTimeK = "datetime"
    val chatK = "chat"
    val readK = "read"

    /* LISTENERS */
    var chatListener: ListenerRegistration? = null
    var chatListListener: ListenerRegistration? = null

    fun mutableMapOfChat(username: String, chat: Chat): MutableMap<String, Any>{
        var mR = mutableListOf<AnyMap>()
        var mL = mutableListOf<AnyMap>()
        for (message in chat.messages){
            if (message.isSelf){
                mL.add(
                    mutableMapOf(
                        messageK to message.message,
                        dateTimeK to message.datetime,
                        readK to message.read
                    )
                )
            }else{
                mR.add(
                    mutableMapOf(
                        messageK to message.message,
                        dateTimeK to message.datetime,
                        readK to message.read
                    )
                )
            }
        }
        return mutableMapOf(
            username to mL,
            chat.username to mR
        )
    }

    fun sendMessage(chat: Chat, chatR: DocumentReference, l: GenericCB? = null){
        chatR.update(
            mutableMapOf(
                chatK to mutableMapOfChat(username,chat)
            ) as AnyMap
        ).addOnSuccessListener {
            log("Successfully sent message")
            l?.invoke(true, "Successfully updated")
        }.addOnFailureListener {
            log("Failed to send message: $it")
            l?.invoke(false, "Failed to update")
        }
    }

    /* NOTE: Marks all messages in chat as read */
    fun readChat(chat: Chat, chatR: DocumentReference, l: GenericCB? = null){
        var hasUnread = false
        chat.messages.map {
            if(!it.read && !it.isSelf){
                hasUnread = true
                it.read = true
            }
        }
        if(hasUnread){
            sendMessage(chat, chatR, l)
        }
    }

    /* NOTE: Use it when a new chat is opened */
    fun createChat(chat: Chat,  l: ((Boolean, String, DocumentReference?) -> Unit)? = null){
        db.collection(collectionK)
            .add(
                mutableMapOf(
                    chatK to mutableMapOfChat(username, chat),
                    username1K to username,
                    username2K to chat.username
                )
            )
            .addOnSuccessListener {
                log("Successfully created chat")
                l?.invoke(true, "Successfully created chat", it)
            }
            .addOnFailureListener {
                log("Failed to create chat: $it")
                l?.invoke(false, "Failed to create chat: $it", null)
            }
    }

    fun listenToChat(remoteUsername: String, l: ((Boolean, Boolean, String, Chat?, DocumentReference?) -> Unit)? = null){
        chatListener = db.collection(collectionK)
            .addSnapshotListener { qs, e ->
                var found = false
                if (e == null) {
                    for (doc in qs!!.documents) {
                        if ((doc.data!![this.username1K] as String == this.username ||
                                    doc.data!![this.username2K] as String == this.username) &&
                            (doc.data!![this.username1K] as String == remoteUsername ||
                                    doc.data!![this.username2K] as String == remoteUsername)
                        ) {
                            found = true
                            val d = doc.data!!
                            val c =
                                d[this.chatK] as MapListOfAny
                            val isFirst = this.username == d[this.username1K] as String
                            val u =
                                (if (isFirst) d[this.username2K] else d[this.username1K]) as String
                            val m = mutableListOf<Message>()
                            c.forEach { (ck, cv) ->
                                for (mI in cv) {
                                    val v = (mI[this.messageK] as String).replace("\"", "")
                                    val d = (mI[this.dateTimeK] as String).replace("\"", "")
                                    val r = (mI[this.readK] as Boolean?) ?: true
                                    m.add(
                                        Message(v, d, ck == this.username,r)
                                    )
                                }
                            }
                            log("Chat update retrieved")
                            l?.invoke(
                                true, false,
                                "Successfully retrived chat update",
                                Chat(username = u, messages = m),
                                doc.reference
                            )
                            break
                        }
                    }
                    if(!found) {
                        log("This is a new chat")
                        l?.invoke(true, true, "This is a new chat", null,null)
                    }
                } else {
                    log("Error listening to chat: $e")
                }
            }
    }

    fun listenChatList( l: ((Boolean, String, MutableList<Chat>) -> Unit)? = null){
        chatListListener = db.collection(collectionK)
            .addSnapshotListener { qs, e ->
                if (e == null) {
                    val chats = mutableListOf<Chat>()
                    for (doc in qs!!.documents) {
                        if (
                            doc.data!![this.username1K] as String == this.username ||
                            doc.data!![this.username2K] as String == this.username
                        ) {
                            val d = doc.data!!
                            val c =
                                d[this.chatK] as MapListOfAny
                            val isFirst = this.username == d[this.username1K] as String
                            val u =
                                (if (isFirst) d[this.username2K] else d[this.username1K]) as String
                            val m = mutableListOf<Message>()
                            c.forEach { (ck, cv) ->
                                for (mI in cv) {
                                    val v = (mI[this.messageK] as String).replace("\"", "")
                                    val d = (mI[this.dateTimeK] as String).replace("\"", "")
                                    val r = (mI[this.readK] as Boolean?) ?: true
                                    m.add(
                                        Message(v, d, ck == this.username,r)
                                    )
                                }
                            }
                            chats.add(
                                Chat(username = u, messages = m)
                            )
                        }
                    }
                    log("Chat list update retrieved")
                    l?.invoke(
                        true,
                        "Successfully retrived chat update",
                        chats
                    )
                } else {
                    log("Error listening to chat list: $e")
                }
            }
    }
}
package com.fiuba.ubademy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.fiuba.ubademy.main.profile.chat.ChatFragment
import com.fiuba.ubademy.main.profile.chat.Message
import com.fiuba.ubademy.main.profile.chat.MessageAdapter
import com.fiuba.ubademy.main.profile.chat.ScrollToBottomObserver
import com.fiuba.ubademy.network.model.GetUserResponse
import com.fiuba.ubademy.network.model.NotifyRequest
import com.fiuba.ubademy.utils.api
import com.fiuba.ubademy.utils.clearSharedPreferencesChat
import com.fiuba.ubademy.utils.getSharedPreferencesData
import com.fiuba.ubademy.utils.setSharedPreferencesChat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import timber.log.Timber

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter
    private lateinit var currentUserId: String
    private lateinit var otherUserId: String

    private lateinit var chatDisplayNameLabel: TextView
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatSendButton: Button
    private lateinit var chatMessageEditText: TextInputEditText
    private lateinit var chatMessageInputLayout: TextInputLayout
    private lateinit var chatSwitchAccountNotificationCard: CardView
    private lateinit var chatSwitchAccountNotificationToValue: TextView
    private lateinit var chatSwitchAccountNotificationFrom: TextView
    private lateinit var chatSwitchAccountNotificationMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatDisplayNameLabel = findViewById(R.id.chatDisplayNameLabel)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatSendButton = findViewById(R.id.chatSendButton)
        chatMessageEditText = findViewById(R.id.chatMessageEditText)
        chatMessageInputLayout = findViewById(R.id.chatMessageInputLayout)
        chatSwitchAccountNotificationCard = findViewById(R.id.chatSwitchAccountNotificationCard)
        chatSwitchAccountNotificationToValue = findViewById(R.id.chatSwitchAccountNotificationToValue)
        chatSwitchAccountNotificationFrom = findViewById(R.id.chatSwitchAccountNotificationFrom)
        chatSwitchAccountNotificationMessage = findViewById(R.id.chatSwitchAccountNotificationMessage)

        currentUserId = baseContext.getSharedPreferencesData().id

        // handle not logged-in user
        if (currentUserId.isEmpty()) {
            val authIntent = Intent(baseContext, AuthActivity::class.java)
            startActivity(authIntent)
            finish()
            return
        }

        val userFrom = intent.extras?.get("userFrom") as GetUserResponse
        otherUserId = userFrom.id

        val userTo = intent.extras?.get("userTo") as GetUserResponse

        // handle the "not-for-current-user" notification
        if (currentUserId != userTo.id) {
            chatDisplayNameLabel.visibility = View.GONE
            chatDisplayNameLabel.visibility = View.GONE
            chatMessageInputLayout.visibility = View.GONE
            chatSendButton.visibility = View.GONE
            chatSwitchAccountNotificationCard.visibility = View.VISIBLE
            chatSwitchAccountNotificationToValue.text = userTo.email
            chatSwitchAccountNotificationFrom.text = userFrom.displayName
            chatSwitchAccountNotificationMessage.text = intent.extras?.getString("message")
            return
        }

        // user is logged-in and the message is destined for the same user, show chat
        val chatId = listOf(userFrom.id, currentUserId)
            .sorted()
            .joinToString(":") {
                it
            }

        val db = Firebase.database
        val chatReference = db.reference.child(ChatFragment.CHATS_CHILD).child(chatId)

        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(chatReference, Message::class.java)
            .build()

        val manager = LinearLayoutManager(baseContext)
        manager.stackFromEnd = true

        chatDisplayNameLabel.text = userFrom.displayName

        chatRecyclerView.layoutManager = manager

        adapter = MessageAdapter(options, currentUserId)
        chatRecyclerView.adapter = adapter
        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(chatRecyclerView, adapter, manager)
        )

        chatSendButton.setOnClickListener {
            if (chatMessageEditText.text.toString().isNotEmpty()) {
                val message = Message(
                    currentUserId,
                    chatMessageEditText.text.toString()
                )
                db.reference.child(ChatFragment.CHATS_CHILD).child(chatId).push().setValue(message)
                lifecycleScope.launch { notify(chatMessageEditText.text.toString()) }
                chatMessageEditText.setText("")
                chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private suspend fun notify(message: String) {
        try {
            api().notify(
                NotifyRequest(
                    from = currentUserId,
                    to = otherUserId,
                    message
                )
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onStart() {
        super.onStart()
        if (this::adapter.isInitialized)
            adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (this::adapter.isInitialized)
            adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        setSharedPreferencesChat(currentUserId, otherUserId)
    }

    override fun onPause() {
        super.onPause()
        clearSharedPreferencesChat()
    }
}
package com.fiuba.ubademy.main.profile.chat

class Message {
    var userId: String? = null
    var text: String? = null

    constructor()

    constructor(userId: String?, text: String?) {
        this.userId = userId
        this.text = text
    }
}
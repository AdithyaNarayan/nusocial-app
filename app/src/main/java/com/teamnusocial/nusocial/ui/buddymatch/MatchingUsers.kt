package com.teamnusocial.nusocial.ui.buddymatch

import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirestoreUtils

class MatchingUsers(val allUsers: MutableList<User>, val currUser: User) {
    fun filterUsers(): MutableList<User> {
        val matchedUsers = allUsers.toMutableList()
        matchedUsers.sorted()

        //10 users each partitions
        var partitions = matchedUsers.size / 10
        partitions += matchedUsers.size % 10
        val lastParition = matchedUsers.size % 10

        //shuffle
        for(i in 0..partitions-2) {
            for(j in i..i+9) {
                var end = 10*(i + 1)
                var start = i + 1
                while(end > start) {
                    val k = java.util.Random().nextInt(end--)
                    val t = matchedUsers[end]
                    matchedUsers[end] = matchedUsers[k]
                    matchedUsers[k] = t
                }
            }
        }
        for(j in 0..lastParition-1) {
            var end = matchedUsers.size
            var start = matchedUsers.size - matchedUsers.size % 10 + 1
            while(end > start) {
                val k = java.util.Random().nextInt(end--)
                val t = matchedUsers[end]
                matchedUsers[end] = matchedUsers[k]
                matchedUsers[k] = t
            }
        }
        //

        return matchedUsers
    }
}
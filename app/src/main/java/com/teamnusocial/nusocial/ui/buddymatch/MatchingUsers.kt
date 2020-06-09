package com.teamnusocial.nusocial.ui.buddymatch

import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirestoreUtils

class MatchingUsers(val allUsers: MutableList<User>, val currUser: User) {
    fun filterUsers(): MutableList<User> {
        var matchedUsers: MutableList<User> = mutableListOf<User>()

        //matchedUsers = allUsers.sortedWith(UserComparator(FirestoreUtils().getCurrentUser().second.newInstance())).toMutableList()
        return  matchedUsers
    }
}
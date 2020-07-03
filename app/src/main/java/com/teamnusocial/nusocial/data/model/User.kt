package com.teamnusocial.nusocial.data.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.squareup.okhttp.Dispatcher
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
enum class Gender {
    MALE, FEMALE, OTHERS
}
@Parcelize
data class User(
    public var uid: String,
    public val name: String,
    public val gender: Gender,
    public val profilePicturePath: String,
    public val modules: MutableList<Module>,
    public val yearOfStudy: Int,
    public val courseOfStudy: String,
    public val location: LocationLatLng,
    public val buddies: List<String>, // store uid of buddies
    public val about: String,
    public val lastBroadcasted: Timestamp,
    public val seenAndMatch: MutableList<String>,
    public var communities: MutableList<Pair<String, Timestamp>>,
    public var registrationToken: MutableList<String>,
    public var notesForAdmin: MutableList<Pair<String, Boolean>>
) : Comparable<User>, Parcelable {
    constructor() : this(
        "",
        "--Blank--",
        Gender.MALE,
        "https://i7.pngflow.com/pngimage/455/105/png-anonymity-computer-icons-anonymous-user-anonymous-purple-violet-logo-smiley-clipart.png",
        mutableListOf(),
        1,
        "--blank--",
        LocationLatLng(0.0, 0.0, ""),
        listOf(),
        "",
        Timestamp(Date(0)),
        mutableListOf(),
        mutableListOf(),
        mutableListOf(),
        mutableListOf()
    )

    fun compareWithYou(another: User?): Double {
        var matchedModules: Int = 0
        var matchedCourse: Int
        var matchedAcadYears: Int
        var matchedGender: Int
        var you: User = User()

        CoroutineScope(Dispatchers.IO).launch {
            you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
        }

        for (module1 in another!!.modules) {
            for(module2 in you.modules) {
                if(module1 == module2) {
                    matchedModules++
                }
            }
        }

        matchedCourse = if(another!!.courseOfStudy == you.courseOfStudy) 1 else 0
        matchedAcadYears = Math.abs(another!!.yearOfStudy - you.yearOfStudy)
        matchedGender = if(another!!.gender == you.gender) 1 else 0

        return formula(matchedModules,matchedCourse, matchedAcadYears, matchedGender)
    }

    fun formula(matchedModules: Int,matchedCourse: Int, matchedAcadYears: Int, matchedGender: Int): Double {
        val moduleMatch = 5.0
        val courseMatch = 0.2
        val acadYearMatch = 0.1
        val genderMatch = 0.1
        return matchedModules * moduleMatch + matchedCourse * courseMatch + matchedGender * genderMatch - matchedAcadYears * acadYearMatch
    }

    override fun compareTo(other: User): Int {
        val this_and_curr = compareWithYou(this)
        val other_and_curr = compareWithYou(other)

        if(this_and_curr < other_and_curr) return -1
        else if(this_and_curr > other_and_curr) return 1
        else return 0

    }
}
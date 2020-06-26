package com.teamnusocial.nusocial.ui.buddymatch

import com.teamnusocial.nusocial.data.model.Gender
import com.teamnusocial.nusocial.data.model.User
import kotlin.Comparator
import kotlin.collections.HashMap

class UserComparator(val you: User,val allUsers: MutableList<User>) : Comparator<User> {
    private lateinit var seenUsers: MutableList<User>
    private lateinit var genderCount: Array<Int>
    private lateinit var yearCount: Array<Int>
    private lateinit var coursePreference: HashMap<String, Int>
    override fun compare(o1: User?, o2: User?): Int {
        val compareWithO1 = compareWithYou(o1)
        val compareWithO2 = compareWithYou(o2)
        if(compareWithO1 > compareWithO2) return 1
        else if(compareWithO1 < compareWithO2) return -1
        else return 0
    }
    fun compareWithYou(another: User?): Double {
        var matchedModules: Int = 0
        var matchedCourse: Int
        var matchedAcadYears: Int
        var matchedGender: Int
        var you: User = User()
        for(module1 in another!!.modules) {
            for(module2 in you.modules) {
                if(module1 == module2) {
                    matchedModules++
                }
            }
        }

        matchedCourse = if(another!!.courseOfStudy == you.courseOfStudy) 1 else 0
        matchedAcadYears = Math.abs(another!!.yearOfStudy - you.yearOfStudy)
        matchedGender = if(another!!.gender == you.gender) 1 else 0

        var res = formula(matchedModules,matchedCourse, matchedAcadYears, matchedGender)
        //extra priorities
        analyzePastMatches()
        var maxPos = 0
        //gender
        for(i in 0..2) {
            if(genderCount[i] > genderCount[maxPos]) maxPos = i
        }
        if((maxPos == 0 && another.gender == Gender.MALE) || (maxPos == 1 && another.gender == Gender.FEMALE) || (maxPos == 2 && another.gender == Gender.OTHERS))
            res += 0.1
        //year of study
        maxPos = 0
        for(i in 0..4) {
            if(yearCount[i] > yearCount[maxPos]) maxPos = i
        }
        if(maxPos + 1 == another.yearOfStudy)
            res += 0.1
        //course of study
        var courseMax = ""
        var timeMax = 0
        for(key in coursePreference.keys) {
            val curr = coursePreference.get(key)!!
            if(curr > timeMax) {
                timeMax = curr
                courseMax = key
            }
        }
        if(courseMax.equals(another.courseOfStudy, true)) res += 0.1
        return res
    }
    fun analyzePastMatches() {
        seenUsers = allUsers.filter { user -> you.seenAndMatch.contains(user.uid) || you.buddies.contains(user.uid)}.toMutableList()
        genderCount = arrayOf(0,0,0)// 0 for MALE, 1 for FEMALE, 2 for OTHERS
        yearCount = arrayOf(0,0,0,0,0)
        coursePreference = HashMap<String, Int>()
        for(user in seenUsers) {
            //record gender
            if(user.gender == Gender.MALE) genderCount[0]++
            else if(user.gender == Gender.FEMALE) genderCount[1]++
            else genderCount[2]++

            yearCount[user.yearOfStudy - 1]++

            if(coursePreference.containsKey(user.courseOfStudy)) {
                coursePreference.replace(user.courseOfStudy, coursePreference.get(user.courseOfStudy)!! + 1)
            }
            else coursePreference.put(user.courseOfStudy, 1)
        }

    }

    fun formula(matchedModules: Int,matchedCourse: Int, matchedAcadYears: Int, matchedGender: Int): Double {
        val moduleMatch = 5.0
        val courseMatch = 0.2
        val acadYearMatch = 0.1
        val genderMatch = 0.1
        return matchedModules * moduleMatch + matchedCourse * courseMatch + matchedGender * genderMatch - matchedAcadYears * acadYearMatch
    }
}
class MatchingUsers(val allUsers: MutableList<User>, var you: User) {
    fun filterUsers(): MutableList<User> {
        val matchedUsers = allUsers.toMutableList()
        matchedUsers.sortWith(UserComparator(you, allUsers))

        //10 users each partitions
        //shuffle
        var index = 0
        while(index < matchedUsers.size) {
            if(matchedUsers.size - index < 9) {
                for(i in index+1..matchedUsers.size) {
                    var end = matchedUsers.size - 1
                    var start = index
                    while(end > start) {
                        val k = java.util.Random().nextInt(end--)
                        val t = matchedUsers[end]
                        matchedUsers[end] = matchedUsers[k]
                        matchedUsers[k] = t
                    }
                }
                break
            }
            for(i in index..index+9) {
                var end = index + 9
                var start = index
                while(end > start) {
                    val k = java.util.Random().nextInt(end--)
                    val t = matchedUsers[end]
                    matchedUsers[end] = matchedUsers[k]
                    matchedUsers[k] = t
                }
            }
            index += 10
        }
        //
        return matchedUsers
    }
}

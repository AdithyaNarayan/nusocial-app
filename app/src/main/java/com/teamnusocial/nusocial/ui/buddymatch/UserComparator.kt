package com.teamnusocial.nusocial.ui.buddymatch

import com.teamnusocial.nusocial.data.model.User
import java.lang.Math.abs

class UserComparator(val you: User): Comparator<User> {
    val moduleMatch = 5.0
    val courseMatch = 0.2
    val acadYearMatch = 0.1
    val genderMatch = 0.1

    override fun compare(o1: User?, o2: User?): Int {
        val diff = compareWithYou(o1) - compareWithYou(o2)
        if(diff > 0) return 1
        else if(diff < 0) return -1
        else return 0
    }
    fun compareWithYou(another: User?): Double {
        var matchedModules: Int = 0
        var matchedCourse: Int
        var matchedAcadYears: Int
        var matchedGender: Int


        for(module1 in another!!.modules) {
            for(module2 in you.modules) {
                if(module1 == module2) {
                    matchedModules++
                }
            }
        }

        matchedCourse = if(another!!.courseOfStudy == you.courseOfStudy) 1 else 0
        matchedAcadYears = abs(another!!.yearOfStudy - you.yearOfStudy)
        matchedGender = if(another!!.gender == you.gender) 1 else 0

        return formula(matchedModules,matchedCourse, matchedAcadYears, matchedGender)
    }

    fun formula(matchedModules: Int,matchedCourse: Int, matchedAcadYears: Int, matchedGender: Int)
    = matchedModules*moduleMatch + matchedCourse*courseMatch + matchedGender*genderMatch - matchedAcadYears*acadYearMatch

}
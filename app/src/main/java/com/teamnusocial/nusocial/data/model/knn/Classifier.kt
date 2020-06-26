package com.teamnusocial.nusocial.data.model.knn

import android.util.Log
import com.teamnusocial.nusocial.data.model.User

class Classifier {
    private var k = 1

    private var listDataPoint: MutableList<User> = ArrayList()
    private var mapCluster: HashMap<String, MutableList<User>> = HashMap()

    fun populateDataPoints(list: List<User>, user: User) {
        listDataPoint.clear()
        listDataPoint.addAll(list)
        listDataPoint.remove(user)
    }

    private fun calculateDistances(user: User): MutableList<Double> {
        val listDistance: MutableList<Double> = ArrayList()
        listDataPoint.forEach {
            listDistance.add(user.location.distanceTo(it.location))
            if (mapCluster.containsKey(it.location.cluster)) {
                mapCluster[it.location.cluster]!!.add(it)
            } else {
                mapCluster[it.location.cluster] = mutableListOf(it)
            }
        }
        return listDistance
    }

    private fun getMatchedCluster(hashMap: HashMap<String, Int>): MutableList<User> {
        var matchedCluster = ""
        var maxMatches = 0
        hashMap.forEach { (cluster, matches) ->
            if (matches > maxMatches) {
                maxMatches = matches
                matchedCluster = cluster
            }
        }
        return mapCluster[matchedCluster]!!
    }

    fun classifyDataPoint(user: User): MutableList<User> {
        val hashMap: HashMap<String, Int> = HashMap()
        val listDistance = calculateDistances(user)
        for (i in 0..k) {
            var min = Double.MAX_VALUE
            var minIndex = -1
            Log.d("BROADCAST", listDistance.toString())

            for (j in listDistance.indices) {
                if (listDistance[j] < min) {
                    min = listDistance[j]
                    minIndex = j
                }
            }
            val cluster = listDataPoint[minIndex].location.cluster
            if (hashMap.containsKey(cluster)) {
                hashMap[cluster] = hashMap[cluster]!! + 1
            } else {
                hashMap[cluster] = 1
            }
            //listDistance[minIndex] = Double.MAX_VALUE
        }
        return getMatchedCluster(hashMap)
    }

}
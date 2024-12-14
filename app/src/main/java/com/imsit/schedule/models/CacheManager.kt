package com.imsit.schedule.models

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imsit.schedule.models.Schedule.Group

class CacheManager(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()
    private val lastUpdatedKey = "last_updated_time"
    private val groupsCacheKey = "groups_cache"
    private val cacheExpiryTime = 24 * 60 * 60 * 1000 // milliseconds

    fun getLastUpdatedTime(): Long {
        return preferences.getLong(lastUpdatedKey, 0L)
    }

    fun saveLastUpdatedTime(time: Long) {
        preferences.edit().putLong(lastUpdatedKey, time).apply()
    }

    fun shouldUpdateCache(): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - getLastUpdatedTime()) > cacheExpiryTime
    }

    fun saveGroupsToCache(groups: HashMap<String, ArrayList<Group>>) {
        val json = gson.toJson(groups)
        preferences.edit().putString(groupsCacheKey, json).apply()
    }

    fun loadGroupsFromCache(): HashMap<String, ArrayList<Group>>? {
        val json = preferences.getString(groupsCacheKey, null) ?: return HashMap()

        val type = object : TypeToken<HashMap<String, ArrayList<Group>>>() {}.type
        val groups: HashMap<String, ArrayList<Group>> = try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            return HashMap()
        }

        return if (groups.isNotEmpty()) {
            val sorted = groups.toSortedMap(Comparator.comparingInt {
                it.split(" ")[0].toInt()
            }).toMutableMap() as HashMap<String, ArrayList<Group>>

            sorted
        } else {
            HashMap()
        }
    }

}
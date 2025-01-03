package com.imsit.schedule.data.cache

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imsit.schedule.data.models.DataClasses

class CacheManager(context: Context) {

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val gson = Gson()
    private val lastUpdatedKey = "last_updated_time"
    private val groupsCacheKey = "groups_cache"
    private val chosenConfigurationKey = "chosen_configuration"
    private val cacheExpiryTime = 24 * 60 * 60 * 1000 // milliseconds

    data class Configuration(val course: String, val speciality: String, val group: String)

    fun loadLastConfiguration(): Configuration {
        val json = preferences.getString(chosenConfigurationKey, null)

        val type = object : TypeToken<Configuration>() {}.type

        val value = try {
            gson.fromJson(json, type)
        }
        catch (e: Exception) {
            Configuration("", "", "")
        }

        return value
    }

    fun saveActualConfiguration(configuration: Configuration) {
        val json = gson.toJson(configuration)
        preferences.edit().putString(chosenConfigurationKey, json).apply()
    }

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

    fun saveGroupsToCache(groups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>) {
        val json = gson.toJson(groups)
        preferences.edit().putString(groupsCacheKey, json).apply()
    }

    fun loadGroupsFromCache(): HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>> {
        val json = preferences.getString(groupsCacheKey, null) ?: return HashMap()

        val type = object : TypeToken<HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>>() {}.type
        val groups: HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>> = try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            return HashMap()
        }

        return if (groups.isNotEmpty()) {
            val sorted = groups.toSortedMap(Comparator.comparingInt {
                it.split(" ")[0].toInt()
            }).toMutableMap() as HashMap<String, HashMap<String, ArrayList<DataClasses.Group>>>

            sorted
        } else {
            HashMap()
        }
    }

}
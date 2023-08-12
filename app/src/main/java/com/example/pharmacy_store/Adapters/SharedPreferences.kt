import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPrefManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val SHARED_PREF_NAME = "worklk_madproject_pref"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_UID = "key_user_uid"
    }

    fun setLoggedIn(loggedIn: Boolean) {
        editor.putBoolean(KEY_IS_LOGGED_IN, loggedIn)
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Save user's ID
    fun saveUserId(userId: String) {
        Log.d("UserID", "Saving UserID to SharedPrefs: $userId")
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    // Retrieve user's ID
    fun getUserId(): String {
        val userId = sharedPreferences.getString(KEY_USER_ID, "") ?: ""
        Log.d("UserID", "Retrieving UserID from SharedPrefs: $userId")
        return userId
    }

    // Save user's UID from Firebase Authentication
    fun saveUserUID(userUID: String?) {
        if (userUID != null) {
            Log.d("UserUID", "Saving UserUID to SharedPrefs: $userUID")
            editor.putString(KEY_USER_UID, userUID)
            editor.apply()
        }
    }

    // Retrieve user's UID from Firebase Authentication
    fun getUserUID(): String {
        val userUID = sharedPreferences.getString(KEY_USER_UID, "") ?: ""
        Log.d("UserUID", "Retrieving UserUID from SharedPrefs: $userUID")
        return userUID
    }
}

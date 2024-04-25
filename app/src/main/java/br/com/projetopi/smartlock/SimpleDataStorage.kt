package br.com.projetopi.smartlock

import android.content.Context
import br.com.projetopi.smartlock.Classes.User

class SimpleStorage (context: Context) {

    val sharedPreferences = context.getSharedPreferences("SIMPLE_STORAGE",
        Context.MODE_PRIVATE)

    fun getUserAccountData(): User {

        val name: String? = sharedPreferences.getString("account_name", null)
        val email: String? = sharedPreferences.getString("account_email", null)
        val age: Int = sharedPreferences.getInt("account_age", 0)
        val CPF: String? = sharedPreferences.getString("account_CPF", null)
        val phone: String? = sharedPreferences.getString("account_phone", null)
        val uid: String? = sharedPreferences.getString("account_uid", null)

        return User(uid,name,email,null,age,CPF,phone)
    }

    fun storageUserAccount(user: User){
        with (sharedPreferences.edit()) {
            putString("account_name", user.name)
            putString("account_email", user.email)
            putInt("account_age", user.age)
            putString("account_CPF", user.CPF)
            putString("account_phone", user.phone)
            putString("account_uid", user.uid)
            apply()
        }
    }

    fun clearUserAccount(){
        with (sharedPreferences.edit()) {
            putString("account_name", null)
            putString("account_email", null)
            putInt("account_age", 0)
            putString("account_CPF", null)
            putString("account_phone", null)
            putString("account_uid", null)
            apply()
        }
    }
}


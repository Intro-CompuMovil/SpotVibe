package com.example.spotvibe

data class User(var name: String = "",
                var lastName: String = "",
                var username: String = "",
                var number: String = "",
                var cedula: String = "",
                var email: String = "",
                val rol:String="",
                var profileImageUrl: String = "")

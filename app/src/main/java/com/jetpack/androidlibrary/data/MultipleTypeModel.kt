package com.jetpack.androidlibrary.data

data class MultipleTypeModel(var type: Int, var user: UserModel? = null, var tag: Tag? = null)

data class Tag(var name: String = "", var url: String = "")

data class UserModel(var name: String = "", var avatar: String = "", var describe: String = "")
package com.example.permission

import android.os.Parcel
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity

@Suppress("UNREACHABLE_CODE")
data class PermissionConfig(
    var activity: FragmentActivity? = null,
    var permissions: MutableList<String> = arrayListOf(),
    var isForceAllPermissionsGranted: Boolean = false,
    var requestForceAllFailure: ((denied: List<String>) -> Unit)? = null,
    var requestSuccess: (() -> Unit)? = null,
    var requestFailure: ((granted: List<String>, denied: List<String>, forceDenied: List<String>) -> Unit)? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("activity"),
        TODO("permissions"),
        parcel.readByte() != 0.toByte(),
        TODO("requestForceAllFailure"),
        TODO("requestSuccess"),
        TODO("requestFailure")
    ) {
        // Do nothing there.
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<PermissionConfig> {
        override fun createFromParcel(parcel: Parcel): PermissionConfig {
            return PermissionConfig(parcel)
        }

        override fun newArray(size: Int): Array<PermissionConfig?> {
            return arrayOfNulls(size)
        }
    }
}
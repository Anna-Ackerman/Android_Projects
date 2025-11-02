package stu.cn.ua.helloworld.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    val firstName: String,
    val lastName: String,
    val group: String
) : Parcelable {

    fun isValid(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                group.isNotBlank()
    }

    companion object {
        val GROUPS = listOf(
            "KI-221", "KI-222", "KI-223", "KIт-181", "МРАп-191"
        )
        const val MAX_VARIANT = 20
    }
}
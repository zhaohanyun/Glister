package cn.edu.sjtu.glister.glisterfrontend

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ImagePropDelegate private constructor ():
    ReadWriteProperty<Any?, String?> {
    private var _value: String? = null
        // Kotlin custom setter
        set(newValue) {
            newValue ?: run {
                field = null
                return
            }
            field = if (newValue == "null" || newValue.isEmpty()) null else newValue
        }

    constructor(initialValue: String?): this() { _value = initialValue }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        _value = value
    }
}

//class Image(var score: String? = null,
//            imageUrl: String? = null) {
//    var imageUrl: String? by ChattPropDelegate(imageUrl)
//}

//Photo:
//photoId int
//photoUri str
//isRecommended bool
//isStarred bool
//score int

class Image(val photoId: Int?=null,
            photoUri: String? = null,
            var isRecommended: Boolean = false,
            var isStarred : Boolean = false,
            val score: Int? = null){
    var photoUri: String? by ChattPropDelegate(photoUri)
}

package cn.edu.sjtu.olivia_fsm.glister_fsm

import android.os.Parcel
import android.os.Parcelable

data class AlbumFolder(var folderNames: String?, var imagePath: String?, var imgCount: Int, var isVideo: Boolean) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(folderNames)
        parcel.writeString(imagePath)
        parcel.writeInt(imgCount)
        parcel.writeByte(if (isVideo) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AlbumFolder> {
        override fun createFromParcel(parcel: Parcel): AlbumFolder {
            return AlbumFolder(parcel)
        }

        override fun newArray(size: Int): Array<AlbumFolder?> {
            return arrayOfNulls(size)
        }
    }
}
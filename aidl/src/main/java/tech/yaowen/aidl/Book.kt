package tech.yaowen.aidl

import android.os.Parcel
import android.os.Parcelable

class Book : Parcelable {
    private var bookId:Int
    private var bookName: String
    private constructor(parcel: Parcel) {
        bookId = parcel.readInt()
        bookName = parcel.readString() ?: ""
    }

    constructor(bookId: Int, bookName:String) {
        this.bookId = bookId
        this.bookName = bookName
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(bookId)
        dest?.writeString(bookName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel)
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }


}
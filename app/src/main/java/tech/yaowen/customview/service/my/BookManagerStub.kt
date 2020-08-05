package tech.yaowen.customview.service.my

import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import kotlinx.coroutines.channels.consumesAll
import tech.yaowen.customview.service.Book
import tech.yaowen.customview.service.INewBookAddListener

final class BookManagerStub() : Binder(), BookManager {

    init {
        attachInterface(this, DESCROPTOR)
    }

    override fun getBookList(): List<Book> {
        return ArrayList()
    }

    override fun addBook(book: Book) {
        TODO("Not yet implemented")
    }

    override fun addNewBookAddListener(listener: INewBookAddListener) {
        TODO("Not yet implemented")
    }

    override fun removeBookAddListener(listener: INewBookAddListener) {
        TODO("Not yet implemented")
    }

    override fun asBinder(): IBinder {
        return this
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
        when (code) {
            IBinder.INTERFACE_TRANSACTION -> {
                reply?.writeString(DESCROPTOR)
                return true
            }
            TRANSACTION_getBookList -> {

            }

            TRANSACTION_addBook -> {

            }

            TRANSACTION_addNewBookAddListener -> {

            }

            TRANSACTION_removeBookAddListener -> {

            }
        }
        return super.onTransact(code, data, reply, flags)
    }
}
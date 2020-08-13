package tech.yaowen.customview.service.my

import android.os.IBinder
import android.os.IInterface
import tech.yaowen.customview.service.Book
import tech.yaowen.customview.service.INewBookAddListener

interface BookManager : IInterface {
    val DESCROPTOR: String
        get() = BookManager::class.qualifiedName ?: ""

    val TRANSACTION_getBookList: Int
        get() = IBinder.FIRST_CALL_TRANSACTION + 0

    val TRANSACTION_addBook: Int
        get() = IBinder.FIRST_CALL_TRANSACTION + 1

    val TRANSACTION_addNewBookAddListener: Int
        get() = IBinder.FIRST_CALL_TRANSACTION + 2

    val TRANSACTION_removeBookAddListener: Int
        get() = IBinder.FIRST_CALL_TRANSACTION + 3

    fun getBookList(): List<Book>
    fun addBook(book: Book)

    fun addNewBookAddListener(listener: INewBookAddListener)
    fun removeBookAddListener(listener: INewBookAddListener)
}
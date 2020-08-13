package tech.yaowen.customview.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import java.util.concurrent.CopyOnWriteArrayList

class AIDLService : Service() {

    val books = CopyOnWriteArrayList<Book>()
    val addBookListener = RemoteCallbackList<INewBookAddListener>()

    private val binder = object : IBookManager.Stub() {
        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String?
        ) {
            // Do nothing
        }

        override fun addBook(book: Book?) {
            book?.let {
                books.add(it)
                for (index in 0 until addBookListener.beginBroadcast()) {
                    addBookListener.getBroadcastItem(index).onNewBookAdd(it)
                }
                addBookListener.finishBroadcast()
            }
        }

        override fun getBookList(): MutableList<Book> {
            return books
        }

        override fun addNewBookAddListener(listener: INewBookAddListener?) {
            listener?.let {
                addBookListener.register(it)
            }
        }

        override fun removeBookAddListener(listener: INewBookAddListener?) {
            listener?.let {
                addBookListener.unregister(it)
            }
        }

    }

    override fun onCreate() {
        books.add(Book("A"))
        books.add(Book("B"))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}
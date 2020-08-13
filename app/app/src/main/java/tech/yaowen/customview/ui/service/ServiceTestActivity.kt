package tech.yaowen.customview.ui.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import tech.yaowen.customview.R
import tech.yaowen.customview.service.*
import java.util.*

class ServiceTestActivity : AppCompatActivity() {

    private lateinit var mService: TestService
    private var mBound: Boolean = false

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as TestService.LocalBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    /** Messenger for communicating with the service.  */
    private var messenger: Messenger? = null

    /** Flag indicating whether we have called bind on the service.  */
    private var bound: Boolean = false

    /**
     * Class for interacting with the main interface of the service.
     */
    private val mConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            messenger = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            messenger = null
            bound = false
        }

        override fun onBindingDied(name: ComponentName?) {
            super.onBindingDied(name)
        }
    }

    var bookManager: IBookManager? = null
    var aidlBound: Boolean = false

    val aidlConnection = object: ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bookManager = IBookManager.Stub.asInterface(service)
            try {
                bookManager?.let {
                    val bookList = it.bookList
                    it.addNewBookAddListener(newBookListener)
                    Toast.makeText(this@ServiceTestActivity, bookList[0].name, Toast.LENGTH_LONG).show()
                    it.addBook(Book("c"))
                }
                aidlBound  = true
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

    }

    val newBookListener = object: INewBookAddListener.Stub() {
        override fun onNewBookAdd(book: Book?) {
            Toast.makeText(this@ServiceTestActivity, "new book add: " + book?.name, Toast.LENGTH_LONG).show()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.service_test_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ServiceTestFragment.newInstance())
                .commitNow()
        }
    }


    override fun onStart() {
        super.onStart()
        // Bind to LocalService
//        Intent(this, TestService::class.java).also { intent ->
//            bindService(intent, connection, Context.BIND_AUTO_CREATE)
//        }

        bindAIDLService()
    }

    override fun onStop() {
        super.onStop()
//        unbindService(connection)
//        mBound = false
        unbindDIDLService()
    }

    /** Called when a button is clicked (the button in the layout file attaches to
     * this method with the android:onClick attribute)  */
    fun callServiceFunc() {
        if (mBound) {
            // Call a method from the LocalService.
            // However, if this call were something that might hang, then this request should
            // occur in a separate thread to avoid slowing down the activity performance.
            val num: Int = mService.randomNumber
            Toast.makeText(this, "number: $num", Toast.LENGTH_SHORT).show()
        }
    }


    fun sayHello(v: View) {
        if (!bound) return
        // Create and send a message to the service, using a supported 'what' value
        val msg: Message = Message.obtain(null, MSG_SAY_HELLO, 0, 0)
        try {
            messenger?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }

    }


    fun startMessengerService() {
        // Bind to the service
        Intent(this, MessengerService::class.java).also { intent ->
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindMessengerService() {
        // Unbind from the service
        if (bound) {
            unbindService(mConnection)
            bound = false
        }
    }

    fun bindAIDLService() {
        // Bind to the service
        Intent(this, AIDLService::class.java).also { intent ->
            bindService(intent, aidlConnection, Context.BIND_AUTO_CREATE)
        }
    }


    fun unbindDIDLService() {
        if (aidlBound) {
            bookManager?.removeBookAddListener(newBookListener)
            unbindService(aidlConnection)
        }
    }

    fun addNewBook() {
        bookManager?.addBook(Book("${Math.random()}"))
    }


}
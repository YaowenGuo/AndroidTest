// INewBookAddListener.aidl
package tech.yaowen.customview.service;

// Declare any non-default types here with import statements
import tech.yaowen.customview.service.Book;

interface INewBookAddListener {
     void onNewBookAdd(in Book book);
}

// IBookManager.aidl
package tech.yaowen.customview.service;

// Declare any non-default types here with import statements
import tech.yaowen.customview.service.Book;
import tech.yaowen.customview.service.INewBookAddListener;

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    List<Book> getBookList();
    void addBook(in Book book);

   void addNewBookAddListener(INewBookAddListener listener);
   void removeBookAddListener(INewBookAddListener listener);

}

package rmi.rmiClient_MXbean;

import java.util.List;

public class Libraries
        implements LibrariesMXBean {
    private static List<Book> bookList;

    public Libraries(List<Book> bookList) {
        this.bookList = bookList;
    }

    @Override
    public List<Book> getBookList() {
        return bookList;
    }

    @Override
    public int getTotal() {
        return bookList.size();
    }

    @Override
    public void clearBookList() {
        bookList.clear();
    }

    @Override
    public Book getBook(int index) {
        if (bookList.size() > 0) {
            return bookList.get(index);
        }
        return null;
    }

}  

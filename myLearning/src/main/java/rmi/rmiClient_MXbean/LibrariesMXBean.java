package rmi.rmiClient_MXbean;

import java.util.List;

public interface LibrariesMXBean {


    public int getTotal();

    public List<Book> getBookList();

    public Book getBook(int index);

    public void clearBookList();

}  
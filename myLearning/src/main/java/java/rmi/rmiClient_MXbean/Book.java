package java.rmi.rmiClient_MXbean;

import java.beans.ConstructorProperties;

public class Book {
    private int index;
    private String title;
    private int price;
    private String author;

    @ConstructorProperties({"index", "title", "price", "author"})
    public Book(int index, String title, int price, String author) {
        this.index = index;
        this.title = title;
        this.price = price;
        this.author = author;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}  
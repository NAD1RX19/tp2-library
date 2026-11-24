package library;

public class Book {

private final String isbn;
private final String title;
private final int year;

public Book(String isbn, String title, int year) {
this.isbn = isbn;
this.title = title;
this.year = year;
}

public String getIsbn() {
return isbn;
}

public String getTitle() {
return title;
}

public int getYear() {
return year;
}

@Override
public boolean equals(Object o) {
if (this == o)
return true;

if (!(o instanceof Book))
return false;

Book other = (Book) o;
return isbn.equals(other.isbn);
}

@Override
public int hashCode() {
return isbn.hashCode();
}}

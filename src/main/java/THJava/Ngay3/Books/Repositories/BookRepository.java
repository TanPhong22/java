package THJava.Ngay3.Books.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import THJava.Ngay3.Books.Models.Book;
import THJava.Ngay3.Books.Models.CartItem;

public interface BookRepository extends JpaRepository<Book, Long> {
	@Query("SELECT b FROM Book b WHERE b.isdeleted = false")
	Page<Book> findWithOutDelete(Pageable page);
	@Modifying
	@Query("UPDATE Book b set b.isdeleted = true where b.id = :id")
	void deleteBookById(long id);
	@Query("SELECT b FROM Book b WHERE CONCAT(b.title, ' ', b.author, ' ', b.category, ' ', b.price) LIKE %:keyword% AND b.isdeleted = false")
    public Page<Book> Search(Pageable page, @Param("keyword")String keyword);
	
	Optional<Book> findById(Long Id);
}

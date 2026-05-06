package service.user.repository;
import service.user.model.user;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface userRepository extends JpaRepository<user, Integer> {
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    Optional<user> findByEmail(String email);
}
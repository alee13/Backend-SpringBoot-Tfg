package db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import models.Producto;

@Repository
public interface ProductRepository extends JpaRepository<Producto, Long> {

}
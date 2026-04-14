package db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import models.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long>{

}

package db;

import org.springframework.data.jpa.repository.JpaRepository;

import models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long>{

}

package db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import models.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
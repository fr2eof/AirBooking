package pet.airbooking.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pet.airbooking.core.entity.BookingEntity;


public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
}

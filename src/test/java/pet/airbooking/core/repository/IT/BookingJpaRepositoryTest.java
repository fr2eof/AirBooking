package pet.airbooking.core.repository.IT;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pet.airbooking.core.entity.BookingEntity;
import pet.airbooking.core.repository.BookingJpaRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class BookingJpaRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");
    @Autowired
    private BookingJpaRepository repository;

    @Nested
    class Save {

        @Test
        void shouldSaveBooking_withGeneratedId() {
            // Given
            BookingEntity entity = new BookingEntity(1L, 2L);

            // When
            BookingEntity actualSaved = repository.save(entity);

            // Then
            assertThat(actualSaved.getId()).isNotNull();
            assertThat(actualSaved.getUserId()).isEqualTo(1L);
            assertThat(actualSaved.getListingId()).isEqualTo(2L);
        }
    }

    @Nested
    class FindById {

        @Test
        void shouldReturnBooking_whenExists() {
            // Given
            BookingEntity entity = new BookingEntity(1L, 2L);
            BookingEntity saved = repository.save(entity);

            // When
            Optional<BookingEntity> actualOptional =
                    repository.findById(saved.getId());

            // Then
            assertThat(actualOptional).isPresent();

            BookingEntity actual = actualOptional.get();

            assertThat(actual.getId()).isEqualTo(saved.getId());
            assertThat(actual.getUserId()).isEqualTo(1L);
            assertThat(actual.getListingId()).isEqualTo(2L);
        }

        @Test
        void shouldReturnEmpty_whenNotExists() {
            // Given
            Long id = 999L;

            // When
            Optional<BookingEntity> actual =
                    repository.findById(id);

            // Then
            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class ExistsById {

        @Test
        void shouldReturnTrue_whenExists() {
            // Given
            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            // When
            boolean actualExists =
                    repository.existsById(saved.getId());

            // Then
            assertThat(actualExists).isTrue();
        }

        @Test
        void shouldReturnFalse_whenNotExists() {
            // Given
            Long id = 999L;

            // When
            boolean actualExists =
                    repository.existsById(id);

            // Then
            assertThat(actualExists).isFalse();
        }
    }

    @Nested
    class Delete {

        @Test
        void shouldDeleteBooking() {
            // Given
            BookingEntity saved =
                    repository.save(new BookingEntity(1L, 2L));

            Long id = saved.getId();

            // When
            repository.deleteById(id);

            // Then
            Optional<BookingEntity> actual =
                    repository.findById(id);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    class FindAll {

        @Test
        void shouldReturnPage() {
            // Given
            repository.save(new BookingEntity(1L, 2L));
            repository.save(new BookingEntity(3L, 4L));

            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<BookingEntity> actualPage =
                    repository.findAll(pageable);

            // Then
            assertThat(actualPage.getContent()).hasSize(2);
        }
    }
}

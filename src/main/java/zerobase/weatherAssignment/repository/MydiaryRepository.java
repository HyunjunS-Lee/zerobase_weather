package zerobase.weatherAssignment.repository;

import jakarta.persistence.Table;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weatherAssignment.domain.Mydiary;

import java.time.LocalDate;

@Repository
@Table(name="mydiary")
public interface MydiaryRepository extends JpaRepository<Mydiary, Integer> {
    //read
    List<Mydiary> findAllByDate(LocalDate date);

    //read diaries
    List<Mydiary> findAllByDateBetween(LocalDate firstDate, LocalDate lastDate);

    //update
    Mydiary getFirstByDate(LocalDate date);

    //delete
    void deleteAllByDate(LocalDate date);
}

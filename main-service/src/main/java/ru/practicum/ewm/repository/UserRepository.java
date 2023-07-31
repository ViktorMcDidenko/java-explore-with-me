package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * " +
            "FROM users u " +
            "WHERE (:ids IS NULL OR u.id IN :ids)", nativeQuery = true)
    List<User> findByIdIn(@Param("ids") Set<Long> ids, Pageable pageable);

    boolean existsByName(String name);
}
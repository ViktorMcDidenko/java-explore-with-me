package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select u from User u where (:ids is null or u.id in :ids)")
    List<User> findByIdIn(Set<Long> ids, Pageable pageable);

    boolean existsByName(String name);
}
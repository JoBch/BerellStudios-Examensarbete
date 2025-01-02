package se.berellstudios.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import se.berellstudios.server.entities.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    UserEntity findById(int userId);
}
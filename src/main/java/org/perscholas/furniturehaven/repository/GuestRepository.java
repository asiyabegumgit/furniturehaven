package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest,Long> {
}

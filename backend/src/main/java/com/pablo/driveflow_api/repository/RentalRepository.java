package com.pablo.driveflow_api.repository;

import com.pablo.driveflow_api.model.Rental;
import com.pablo.driveflow_api.model.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT r FROM Rental r WHERE r.id = :id AND r.deletedAt IS NULL")
    Optional<Rental> findById(@Param("id") Long id);

    @Query("SELECT r FROM Rental r WHERE r.deletedAt IS NULL")
    Page<Rental> findAll(Pageable pageable);

    @Query("SELECT r FROM Rental r WHERE r.customer.id = :customerId AND r.deletedAt IS NULL")
    Page<Rental> findAllByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
            FROM Rental r
            WHERE r.vehicle.id = :vehicleId
              AND r.status = :activeStatus
              AND r.deletedAt IS NULL
              AND r.startDate <= :endDate
              AND r.endDate >= :startDate
            """)
    boolean existsConflictForVehicle(@Param("vehicleId") Long vehicleId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("activeStatus") RentalStatus activeStatus);

    @Query("SELECT COUNT(r) FROM Rental r WHERE r.deletedAt IS NULL")
    long count();
}


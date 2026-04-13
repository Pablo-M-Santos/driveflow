package com.pablo.driveflow_api.repository;

import com.pablo.driveflow_api.model.RentalStatus;
import com.pablo.driveflow_api.model.VehicleStatus;
import com.pablo.driveflow_api.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    @Query("SELECT v FROM Vehicle v WHERE v.plate = :plate AND v.deletedAt IS NULL")
    Optional<Vehicle> findByPlate(@Param("plate") String plate);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Vehicle v WHERE v.plate = :plate AND v.deletedAt IS NULL")
    boolean existsByPlate(@Param("plate") String plate);

    @Query("SELECT v FROM Vehicle v WHERE v.id = :id AND v.deletedAt IS NULL")
    Optional<Vehicle> findById(@Param("id") Long id);

    @Query("SELECT v FROM Vehicle v WHERE v.deletedAt IS NULL")
    Page<Vehicle> findAll(Pageable pageable);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.deletedAt IS NULL")
    long count();

    @Query("""
            SELECT v FROM Vehicle v
            WHERE v.deletedAt IS NULL
              AND v.status = :availableStatus
              AND NOT EXISTS (
                  SELECT r.id FROM Rental r
                  WHERE r.vehicle = v
                    AND r.status = :activeRentalStatus
                    AND r.deletedAt IS NULL
                    AND r.startDate <= :endDate
                    AND r.endDate >= :startDate
              )
            """)
    Page<Vehicle> findAvailableByPeriod(@Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("availableStatus") VehicleStatus availableStatus,
                                        @Param("activeRentalStatus") RentalStatus activeRentalStatus,
                                        Pageable pageable);
}
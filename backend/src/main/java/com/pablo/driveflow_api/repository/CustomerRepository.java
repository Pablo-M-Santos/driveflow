package com.pablo.driveflow_api.repository;

import com.pablo.driveflow_api.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.cpf = :cpf AND c.deletedAt IS NULL")
    Optional<Customer> findByCpf(@Param("cpf") String cpf);

    @Query("SELECT c FROM Customer c WHERE c.email = :email AND c.deletedAt IS NULL")
    Optional<Customer> findByEmail(@Param("email") String email);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.cpf = :cpf AND c.deletedAt IS NULL")
    boolean existsByCpf(@Param("cpf") String cpf);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.email = :email AND c.deletedAt IS NULL")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT c FROM Customer c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Customer> findById(@Param("id") Long id);

    @Query("SELECT c FROM Customer c WHERE c.deletedAt IS NULL")
    Page<Customer> findAll(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.deletedAt IS NULL")
    long count();
}


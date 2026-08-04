package com.hyperoptic.employees.repository;

import com.hyperoptic.employees.repository.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            SELECT e
            FROM Employee e
            WHERE (:name IS NULL OR e.name = :name)
            AND (:team IS NULL OR e.team = :team)
            AND (:teamLead IS NULL OR e.teamLead.name = :teamLead)
            """)
    List<Employee> findAll(String name, Employee.Team team, String teamLead);
}

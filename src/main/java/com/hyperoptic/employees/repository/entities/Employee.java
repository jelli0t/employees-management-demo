package com.hyperoptic.employees.repository.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "team_lead_id")
    private Employee teamLead;

    public Employee(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    public enum Team {
        DEVELOPMENT,
        HR
    }
}

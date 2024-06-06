package org.weare4saken.spring_jwt_auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(of = {"type"})
@EqualsAndHashCode(exclude = "users")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", unique = true, nullable = false)
    private RoleType type;

    @ManyToMany(mappedBy = "roles")
    private final List<User> users = new ArrayList<>();

    public Role(RoleType type) {
        this.id = null;
        this.type = type;
    }
}

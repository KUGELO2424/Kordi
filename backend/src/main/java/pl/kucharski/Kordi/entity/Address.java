package pl.kucharski.Kordi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String street;

}

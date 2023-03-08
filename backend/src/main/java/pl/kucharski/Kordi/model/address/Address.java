package pl.kucharski.Kordi.model.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.kucharski.Kordi.model.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "address")
public class Address extends BaseEntity {

    private String city;
    private String street;

}

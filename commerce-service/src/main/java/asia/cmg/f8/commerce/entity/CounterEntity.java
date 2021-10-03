package asia.cmg.f8.commerce.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created on 12/30/16.
 */
@Entity
@Table(name = "commerce_counter")
public class CounterEntity{

    @Id
    @Column(name = "counter_name", length = 36)
    private String name; // counter name

    private int value;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}

package org.fermented.dairy.data.rest.boundary.rto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link org.fermented.dairy.data.rest.entity.jpa.Section}
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
public class SectionRto implements Serializable {
    private UUID id;
    private String name;
    private Boolean nonFiction;
    private BookstoreRto bookstore;
}
package org.fermented.dairy.data.rest.boundary;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.fermented.dairy.data.rest.boundary.mapper.SectionMapper;
import org.fermented.dairy.data.rest.boundary.rto.CreateResponse;
import org.fermented.dairy.data.rest.boundary.rto.SectionRequestRto;
import org.fermented.dairy.data.rest.boundary.rto.SectionRto;
import org.fermented.dairy.data.rest.entity.BookstoreRepository;
import org.fermented.dairy.data.rest.entity.SectionRepository;
import org.fermented.dairy.data.rest.entity.jpa.Bookstore;
import org.fermented.dairy.data.rest.entity.jpa.Section;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Path("/bookstores/{bookstoreId}/sections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SectionResource {

    public static final Supplier<NotFoundException> SECTION_NOT_FOUND = () -> new NotFoundException("Section not found");
    public static final Supplier<NotFoundException> CANNOT_FIND_PARENT_BOOKSTORE = () -> new NotFoundException("Cannot find parent bookstore");

    @PathParam("bookstoreId")
    private UUID bookstoreId;

    @Inject
    private SectionRepository sectionRepository;

    @Inject
    private BookstoreRepository bookstoreRepository;

    @Inject
    private SectionMapper sectionMapper;

    @GET
    public List<SectionRto> getSections(){
        try (Stream<Section> sections = sectionRepository.findByBookstore_id(bookstoreId)) {
            return sections.map(sectionMapper::toRto).toList();
        }
    }

    @GET
    @Path("{sectionId}")
    public SectionRto getSection(@PathParam("sectionId") UUID sectionId){
        return sectionRepository.findById(sectionId)
                .filter(section -> bookstoreId.equals(section.getBookstore().getId()))
                .map(sectionMapper::toRto)
                .orElseThrow(SECTION_NOT_FOUND);
    }

    @POST
    public CreateResponse<UUID> createSection(SectionRequestRto sectionRequestRto) {
        final Bookstore bookstore = bookstoreRepository.findById(bookstoreId)
                .orElseThrow(CANNOT_FIND_PARENT_BOOKSTORE);
        final UUID sectionUUID = UUID.randomUUID();
        bookstore.addSection(
                sectionMapper.toEntity(sectionRequestRto)
                        .toBuilder()
                        .id(sectionUUID)
                        .build()
        );
        bookstoreRepository.save(bookstore);
        return new CreateResponse<>(sectionUUID);
    }

    @PUT
    @Path("{sectionId}")
    public CreateResponse<UUID> updateSection( @PathParam("sectionId") UUID sectionId,
                                               SectionRequestRto sectionRequestRto  ) {
            return new CreateResponse<>(
                    sectionRepository.save(
                            sectionRepository.findById(sectionId)
                                    .map(
                                            section -> {
                                                section.setName(sectionRequestRto.getName());
                                                section.setNonFiction(sectionRequestRto.getNonFiction());
                                                return section;
                                            }
                                    ).orElseThrow(SECTION_NOT_FOUND)
                    ).getId()
            );
    }
}

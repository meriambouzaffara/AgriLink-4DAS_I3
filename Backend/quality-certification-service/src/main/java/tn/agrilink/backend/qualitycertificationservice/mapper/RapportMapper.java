package tn.agrilink.backend.qualitycertificationservice.mapper;

public interface RapportMapper {


package tn.agrilink.qualitycertificationservice.mapper;

import org.mapstruct.Mapper;
import tn.agrilink.qualitycertificationservice.dto.RapportDTO;
import tn.agrilink.qualitycertificationservice.entity.Rapport;

    @Mapper(componentModel = "spring")
    public interface RapportMapper {

        RapportDTO toDTO(Rapport rapport);

        Rapport toEntity(RapportDTO dto);
    }

}

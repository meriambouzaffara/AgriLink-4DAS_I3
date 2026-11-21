package tn.agrilink.backend.qualitycertificationservice.mapper;


import org.mapstruct.Mapper;
import tn.agrilink.backend.qualitycertificationservice.dto.RapportDTO;
import tn.agrilink.backend.qualitycertificationservice.entity.Rapport;

@Mapper(componentModel = "spring")
public interface RapportMapper {

    RapportDTO toDTO(Rapport rapport);

    Rapport toEntity(RapportDTO dto);
}

package tn.agrilink.backend.qualitycertificationservice.mapper;

import org.mapstruct.Mapper;
import tn.agrilink.backend.qualitycertificationservice.dto.ControleQualiteDTO;
import tn.agrilink.backend.qualitycertificationservice.entity.ControleQualite;

@Mapper(componentModel = "spring")
public interface ControleQualiteMapper {

    ControleQualiteDTO toDTO(ControleQualite controle);

    ControleQualite toEntity(ControleQualiteDTO dto);
}

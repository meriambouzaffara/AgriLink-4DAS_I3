package tn.agrilink.backend.qualitycertificationservice.mapper;
import org.mapstruct.Mapper;
import tn.agrilink.backend.qualitycertificationservice.dto.CertificationDTO;
import tn.agrilink.backend.qualitycertificationservice.entity.Certification;

@Mapper(componentModel = "spring")
public interface CertificationMapper {

    CertificationDTO toDTO(Certification certification);

    Certification toEntity(CertificationDTO dto);
}

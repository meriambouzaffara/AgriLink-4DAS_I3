package tn.agrilink.backend.qualitycertificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenererRapportDTO {

    private Long idResponsable;
    private List<Long> ids;
}

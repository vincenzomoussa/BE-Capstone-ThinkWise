package it.epicode.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OreInsegnateMensiliDTO {
    private List<String> nomiInsegnanti;
    private List<Integer> oreTotali;
} 
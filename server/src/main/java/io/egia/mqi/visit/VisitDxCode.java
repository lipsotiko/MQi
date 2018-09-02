package io.egia.mqi.visit;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class VisitDxCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitDxCodeId;
    private Long visitId;
    private String dxCode;

}

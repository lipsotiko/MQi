package io.egia.mqi.visit;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class VisitCode {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long visitCodeId;
    private Long visitId;
    private CodeSystem codeSystem;
    private String codeValue;
}

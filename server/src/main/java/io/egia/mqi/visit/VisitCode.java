package io.egia.mqi.visit;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class VisitCode {

    @Id
    private Long visitCodeId;
    private Long visitId;
    private String codeSystem;
    private String codeValue;
}

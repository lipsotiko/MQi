package io.egia.mqi.measure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleTrace {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private UUID measureId;
    private Long patientId;
    private String ruleName;
}

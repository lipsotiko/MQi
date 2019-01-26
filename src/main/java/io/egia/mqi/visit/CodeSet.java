package io.egia.mqi.visit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private CodeSystem codeSystem;
    private String codeValue;

    @ManyToOne
    private CodeSetGroup codeSetGroup;
}

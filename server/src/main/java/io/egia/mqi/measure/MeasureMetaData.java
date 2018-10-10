package io.egia.mqi.measure;

import io.egia.mqi.visit.CodeSet;
import lombok.Data;

import java.util.List;

@Data
public class MeasureMetaData {
    private List<CodeSet> codeSets;

    public MeasureMetaData(List<CodeSet> codeSets) {
        this.codeSets = codeSets;
    }

    public MeasureMetaData() {

    }
}

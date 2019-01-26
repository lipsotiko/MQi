package io.egia.mqi.measure;

import java.io.IOException;
import java.util.List;

public interface PublicMeasureRepo<T> {
    List<PublicMeasureListItem> getMeasureList();

    T getMeasure(String publicMeasureObjectName) throws IOException;
}

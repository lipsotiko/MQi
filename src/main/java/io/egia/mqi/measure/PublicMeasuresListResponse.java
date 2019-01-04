package io.egia.mqi.measure;

import java.util.List;

public class PublicMeasuresListResponse {
    List<BucketObject> items;

    public List<BucketObject> getItems() {
        return items;
    }

}

class BucketObject {
    String name;

    public String getName() {
        return name;
    }

}

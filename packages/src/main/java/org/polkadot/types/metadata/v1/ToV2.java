package org.polkadot.types.metadata.v1;

import org.polkadot.types.metadata.v2.MetadataV2;

public class ToV2 {

    public static MetadataV2 toV2(MetadataV1 v1) {
        return new MetadataV2(v1);
    }
}

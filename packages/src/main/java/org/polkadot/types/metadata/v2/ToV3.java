package org.polkadot.types.metadata.v2;

import org.polkadot.types.metadata.v3.MetadataV3;

public class ToV3 {

    public static MetadataV3 toV3(MetadataV2 v2) {
        return new MetadataV3(v2);
    }
}

package org.polkadot.types.metadata.v11;

import org.polkadot.types.metadata.v3.MetadataV3;

public class ToV3 {
    public static MetadataV3 toV3(MetadataV11 v11) {
        return new MetadataV3(v11);
    }
}

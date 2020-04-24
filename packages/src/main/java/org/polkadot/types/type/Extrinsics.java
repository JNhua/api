package org.polkadot.types.type;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Vector;

/**
 * A {@link org.polkadot.types.codec.Vector} of {@link org.polkadot.type.extrinsics}
 */
public class Extrinsics extends Vector<ExtrinsicV4> {
    public Extrinsics(Object value) {
        super(TypesUtils.getConstructorCodec(ExtrinsicV4.class), value);
    }
}

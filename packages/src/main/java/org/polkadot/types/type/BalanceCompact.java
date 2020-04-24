package org.polkadot.types.type;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Compact;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/22 4:47 下午
 */
public class BalanceCompact extends Compact {
    public BalanceCompact(Object value) {
        super(TypesUtils.getConstructorCodec(Balance.class), value);
    }
}

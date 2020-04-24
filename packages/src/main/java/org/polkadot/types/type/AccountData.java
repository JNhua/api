package org.polkadot.types.type;

import org.polkadot.types.Types;
import org.polkadot.types.codec.Struct;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/21 3:59 下午
 */
public class AccountData extends Struct {

    public AccountData(Object value) {
        super(new Types.ConstructorDef()
                        .add("free", Balance.class)
                        .add("reserved", Balance.class)
                        .add("miscFrozen", Balance.class)
                        .add("feeFrozen", Balance.class)
                , value
        );
    }
}

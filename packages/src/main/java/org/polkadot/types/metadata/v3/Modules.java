package org.polkadot.types.metadata.v3;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 11:14 上午
 */
public interface Modules {
    /**
     * The definition of a module in the system
     */
    class ModuleMetadataV3 extends Struct {
        public ModuleMetadataV3(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vector.with(TypesUtils.getConstructorCodec(Storage.StorageFunctionMetadataV3.class))))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataV3.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.EventMetadataV3.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vector<Calls.FunctionMetadataV3>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.EventMetadataV3>> getEvents() {
            return this.getField("events");
        }

        /**
         * the module name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * the module prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * the associated module storage
         */
        public Option<Vector<Storage.StorageFunctionMetadataV3>> getStorage() {
            return this.getField("storage");
        }
    }
}

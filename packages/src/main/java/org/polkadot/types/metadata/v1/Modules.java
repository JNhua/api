package org.polkadot.types.metadata.v1;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 10:40 上午
 */
public interface Modules {
    /**
     * The definition of a module in the system
     */
    class ModuleMetadataV1 extends Struct {
        public ModuleMetadataV1(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vector.with(TypesUtils.getConstructorCodec(Storage.StorageFunctionMetadataV1.class))))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataV1.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.EventMetadataV1.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vector<Calls.FunctionMetadataV1>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.EventMetadataV1>> getEvents() {
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
        public Option<Vector<Storage.StorageFunctionMetadataV1>> getStorage() {
            return this.getField("storage");
        }
    }
}

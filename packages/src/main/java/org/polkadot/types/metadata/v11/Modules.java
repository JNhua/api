package org.polkadot.types.metadata.v11;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.metadata.v11.StorageV11.StorageMetadataV11;

public interface Modules {
    /**
     * The definition of a module in the system
     */
    class ModuleMetadataV11 extends Struct {
        public ModuleMetadataV11(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("storage", Option.with(TypesUtils.getConstructorCodec(StorageMetadataV11.class)))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataV11.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.EventMetadataV11.class))))
                            .add("constants", Vector.with(TypesUtils.getConstructorCodec(Constants.ModuleConstantMetadataV11.class)))
                            .add("errors", Vector.with(TypesUtils.getConstructorCodec(Errors.ErrorMetadataV11.class)))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vector<Calls.FunctionMetadataV11>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.EventMetadataV11>> getEvents() {
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
        public Option<StorageV11.StorageMetadataV11> getStorage() {
            return this.getField("storage");
        }

        /**
         * the module constants
         */
        public Vector<Constants.ModuleConstantMetadataV11> getConstants() {
            return this.getField("constants");
        }

        /**
         * the module errors
         */
        public Vector<Errors.ErrorMetadataV11> getErrors() {
            return this.getField("errors");
        }
    }
}

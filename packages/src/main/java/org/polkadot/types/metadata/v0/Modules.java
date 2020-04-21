package org.polkadot.types.metadata.v0;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.*;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.Type;
import org.polkadot.types.primitive.U16;

public interface Modules {

    class FunctionArgumentMetadataV0 extends Struct {
        public FunctionArgumentMetadataV0(Object value) {
            super(new Types.ConstructorDef()
                    .add("name", Text.class)
                    .add("type", Type.class), value);
        }

        /**
         * The argument name
         */
        public Text getName() {
            return (Text) this.get("name");
        }

        /**
         * The {@link org.polkadot.types.type}
         */
        public Type getType() {
            return (Type) this.get("type");
        }
    }

    class FunctionMetadataV0 extends Struct {
        public FunctionMetadataV0(Object value) {
            super(new Types.ConstructorDef()
                            .add("id", U16.class)
                            .add("name", Text.class)
                            .add("arguments", Vector.with(TypesUtils.getConstructorCodec(FunctionArgumentMetadataV0.class)))
                            .add("documentation", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * The {@link org.polkadot.types.primitive.Text} documentation
         */
        public Vector<Text> getDocumentation() {
            return this.getField("documentation");
        }

        /**
         * The `[sectionIndex, methodIndex]` call id
         */
        public U16 getId() {
            return this.getField("id");
        }

        /**
         * The call name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class CallMetadataV0 extends Struct {
        public CallMetadataV0(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("functions", Vector.with(TypesUtils.getConstructorCodec(FunctionMetadataV0.class)))
                    , value);
        }


        /**
         * The functions available as FunctionMetadata
         */
        public Vector<FunctionMetadataV0> getFunctions() {
            return this.getField("functions");
        }

        /**
         * The section name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class ModuleMetadataV0 extends Struct {
        public ModuleMetadataV0(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("call", CallMetadataV0.class)
                    , value);
        }


        /**
         * The calls as CallMetadata
         */
        public CallMetadataV0 getCall() {
            return this.getField("call");
        }

        /**
         * The name
         */
        public Text getName() {
            return this.getField("name");
        }
    }

    class RuntimeModuleMetadata extends Struct {
        public RuntimeModuleMetadata(Object value) {
            super(new Types.ConstructorDef()
                            .add("prefix", Text.class)
                            .add("module", ModuleMetadataV0.class)
                            .add("storage", Option.with(TypesUtils.getConstructorCodec(Storage.StorageMetadataV0.class)))
                    , value);
        }

        public ModuleMetadataV0 getModule() {
            return this.getField("module");
        }

        public Text getPrefix() {
            return this.getField("prefix");
        }

        public Option<Storage.StorageMetadataV0> getStorage() {
            return this.getField("storage");
        }
    }
}

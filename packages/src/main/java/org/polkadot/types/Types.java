package org.polkadot.types;

import org.polkadot.api.Types.Signer;
import org.polkadot.types.codec.U8a;
import org.polkadot.types.metadata.latest.Calls;
import org.polkadot.types.primitive.Method;
import org.polkadot.types.type.ExtrinsicEra;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Types {
    /**
     * export type CodecArg = Codec | BN | Boolean | String | Uint8Array | boolean | number | string | undefined | CodecArgArray | CodecArgObject;
     */
    class CodecArg {
    }

    interface CodecCallback<T extends Codec> {
        Object apply(T t);
    }


    interface IHash extends Codec {
    }

    interface ConstructorCodec<T extends Codec> {

        T newInstance(Object... values);

        Class<T> getTClass();
    }

    class ConstructorDef {

        List<String> names = new ArrayList<>();

        List<ConstructorCodec> types = new ArrayList<>();

        List<Class> classes = new ArrayList<>();


        public ConstructorDef add(String name, ConstructorCodec<? extends Codec> type) {
            this.names.add(name);
            this.types.add(type);
            return this;
        }

        public ConstructorDef add(String name, Class<? extends Codec> clazz) {
            this.names.add(name);
            Types.ConstructorCodec builder = TypesUtils.getConstructorCodec(clazz);
            this.types.add(builder);
            return this;
        }

        public List<String> getNames() {
            return names;
        }

        public List<ConstructorCodec> getTypes() {
            return types;
        }

        public ConstructorDef() {
        }

        public ConstructorDef(List<ConstructorCodec> list) {
            for (ConstructorCodec type : list) {
                String simpleName = type.getTClass().getSimpleName();
                this.add(simpleName, type);
            }
        }
    }

    class RegistryTypes {
        Map<String, Class<?>> registryTypes;
    }

    interface IMethod extends Codec {

        List<Codec> getArgs();

        ConstructorDef getArgsDef();

        byte[] getCallIndex();

        byte[] getData();

        boolean hasOrigin();

        Calls.FunctionMetadataLatest getMeta();
    }


    interface RuntimeVersionInterface {
        List<? extends Object> getApis();

        BigInteger getAuthoringVersion();

        String getImplName();

        BigInteger getImplVersion();

        String getSpecName();

        BigInteger getSpecVersion();

    }

    interface IExtrinsic extends IMethod {
        U8a getHash();

        boolean isSigned();

        Method getMethod();

        IExtrinsicSignature getSignature();

        //addSignature(signer:Address|Uint8Array, signature:Uint8Array, nonce:AnyNumber, era?:Uint8Array):IExtrinsic;

        IExtrinsic addSignature(Object signer, byte[] signature, Object payload) throws Exception;

        //sign(account:KeyringPair, options:SignatureOptions):IExtrinsic;
        IExtrinsic sign(org.polkadot.common.keyring.Types.KeyringPair account, Types.SignatureOptions options);
    }

    class SignatureOptions {
        Object blockHash = null;
        ExtrinsicEra era = null;
        Object genesisHash = null;
        Object nonce = null;
        RuntimeVersionInterface version = null;
        Signer signer = null;
        Object tip = null;

        public Object getBlockHash() {
            return blockHash;
        }

        public void setBlockHash(Object blockHash) {
            this.blockHash = blockHash;
        }

        public void setGenesisHash(Object genesisHash) {
            this.genesisHash = genesisHash;
        }

        public Object getGenesisHash(){
            return genesisHash;
        }

        public ExtrinsicEra getEra() {
            return era;
        }

        public void setEra(ExtrinsicEra era) {
            this.era = era;
        }

        public Object getNonce() {
            return nonce;
        }

        public SignatureOptions setNonce(Object nonce) {
            this.nonce = nonce;
            return this;
        }

        public RuntimeVersionInterface getVersion() {
            return version;
        }

        public SignatureOptions setVersion(RuntimeVersionInterface version) {
            this.version = version;
            return this;
        }

        public Signer getSigner(){
            return signer;
        }

        public void setSigner(Signer signer){
            this.signer = signer;
        }

        public Object getTip(){
            return this.tip;
        }

        public void setTip(Object tip){
            this.tip = tip;
        }

    }

    interface IExtrinsicSignature extends Codec {
        boolean isSigned();
    }

    class ContractABIArg {
        public String name;
        public String type;
    }

    class ContractABIMethodBase {
        public List<ContractABIArg> args;
    }

    class ContractABIMethod extends ContractABIMethodBase {
        public boolean mutates;
        public String name;
        public long selector;
        public String returnType;
    }

    class ContractABI {
        public ContractABIMethodBase deploy;
        public List<ContractABIMethod> messages;
        public String name;
    }

    interface ContractABIFn {
        byte[] call(Object... args);

        List<ContractABIArg> getArgs();

        boolean isConstant();

        String getType();
    }

    abstract class Contract {
        protected ContractABI abi;
        protected ContractABIFn deploy;
        protected Map<String, ContractABIFn> messages;
    }

}

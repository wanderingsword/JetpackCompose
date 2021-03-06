/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dex;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Data extracted from a DEX file.
 */
public class DexData {
    private RandomAccessFile mDexFile;
    private HeaderItem mHeaderItem;
    private String[] mStrings;              // strings from string_data_*
    private TypeIdItem[] mTypeIds;
    private ProtoIdItem[] mProtoIds;
    private FieldIdItem[] mFieldIds;
    private MethodIdItem[] mMethodIds;
    private ClassDefItem[] mClassDefs;

    private byte tmpBuf[] = new byte[4];
    private boolean isBigEndian = false;

    /**
     * Constructs a new DexData for this file.
     */
    public DexData(RandomAccessFile raf) {
        mDexFile = raf;
    }

    /**
     * Loads the contents of the DEX file into our data structures.
     *
     * @throws IOException      if we encounter a problem while reading
     * @throws DexDataException if the DEX contents look bad
     */
    public void load() throws IOException {
        parseHeaderItem();

        loadStrings();
        loadTypeIds();
        loadProtoIds();
        loadFieldIds();
        loadMethodIds();
        loadClassDefs();

//        markInternalClasses();
    }

    /**
     * Verifies the given magic number.
     */
    private static boolean verifyMagic(byte[] magic) {
        return Arrays.equals(magic, HeaderItem.DEX_FILE_MAGIC) ||
                Arrays.equals(magic, HeaderItem.DEX_FILE_MAGIC_API_13);
    }

    /**
     * Parses the interesting bits out of the header.
     */
    void parseHeaderItem() throws IOException {
        mHeaderItem = new HeaderItem();

        seek(0);

        byte[] magic = new byte[8];
        readBytes(magic);
        if (!verifyMagic(magic)) {
            System.err.println("Magic number is wrong -- are you sure " +
                    "this is a DEX file?");
            throw new DexDataException();
        }

        /*
         * Read the endian tag, so we properly swap things as we read
         * them from here on.
         */
        seek(8 + 4 + 20 + 4 + 4);
        mHeaderItem.endianTag = readInt();
        if (mHeaderItem.endianTag == HeaderItem.ENDIAN_CONSTANT) {
            /* do nothing */
        } else if (mHeaderItem.endianTag == HeaderItem.REVERSE_ENDIAN_CONSTANT) {
            /* file is big-endian (!), reverse future reads */
            isBigEndian = true;
        } else {
            System.err.println("Endian constant has unexpected value " +
                    Integer.toHexString(mHeaderItem.endianTag));
            throw new DexDataException();
        }

        seek(8 + 4 + 20);  // magic, checksum, signature
        mHeaderItem.fileSize = readInt();
        mHeaderItem.headerSize = readInt();
        /*mHeaderItem.endianTag =*/
        readInt();
        /*mHeaderItem.linkSize =*/
        readInt();
        /*mHeaderItem.linkOff =*/
        readInt();
        /*mHeaderItem.mapOff =*/
        readInt();
        mHeaderItem.stringIdsSize = readInt();
        mHeaderItem.stringIdsOff = readInt();
        mHeaderItem.typeIdsSize = readInt();
        mHeaderItem.typeIdsOff = readInt();
        mHeaderItem.protoIdsSize = readInt();
        mHeaderItem.protoIdsOff = readInt();
        mHeaderItem.fieldIdsSize = readInt();
        mHeaderItem.fieldIdsOff = readInt();
        mHeaderItem.methodIdsSize = readInt();
        mHeaderItem.methodIdsOff = readInt();
        mHeaderItem.classDefsSize = readInt();
        mHeaderItem.classDefsOff = readInt();
        /*mHeaderItem.dataSize =*/
        readInt();
        /*mHeaderItem.dataOff =*/
        readInt();
    }

    /**
     * Loads the string table out of the DEX.
     * <p>
     * First we read all of the string_id_items, then we read all of the
     * string_data_item.  Doing it this way should allow us to avoid
     * seeking around in the file.
     */
    void loadStrings() throws IOException {
        int count = mHeaderItem.stringIdsSize;
        int stringOffsets[] = new int[count];

        System.out.println("reading " + count + " strings");

        seek(mHeaderItem.stringIdsOff);
        for (int i = 0; i < count; i++) {
            stringOffsets[i] = readInt();
        }

        mStrings = new String[count];

        seek(stringOffsets[0]);
        for (int i = 0; i < count; i++) {
            seek(stringOffsets[i]);         // should be a no-op
            mStrings[i] = readString();
//            System.out.println("STR: " + i + ": " + mStrings[i]);
        }
    }

    /**
     * Loads the type ID list.
     */
    void loadTypeIds() throws IOException {
        int count = mHeaderItem.typeIdsSize;
        mTypeIds = new TypeIdItem[count];

        //System.out.println("reading " + count + " typeIds");
        seek(mHeaderItem.typeIdsOff);
        for (int i = 0; i < count; i++) {
            mTypeIds[i] = new TypeIdItem();
            mTypeIds[i].descriptorIdx = readInt();

            //System.out.println(i + ": " + mTypeIds[i].descriptorIdx +
            //    " " + mStrings[mTypeIds[i].descriptorIdx]);
        }
    }

    /**
     * Loads the proto ID list.
     */
    void loadProtoIds() throws IOException {
        int count = mHeaderItem.protoIdsSize;
        mProtoIds = new ProtoIdItem[count];

        //System.out.println("reading " + count + " protoIds");
        seek(mHeaderItem.protoIdsOff);

        /*
         * Read the proto ID items.
         */
        for (int i = 0; i < count; i++) {
            mProtoIds[i] = new ProtoIdItem();
            mProtoIds[i].shortyIdx = readInt();
            mProtoIds[i].returnTypeIdx = readInt();
            mProtoIds[i].parametersOff = readInt();

            //System.out.println(i + ": " + mProtoIds[i].shortyIdx +
            //    " " + mStrings[mProtoIds[i].shortyIdx]);
        }

        /*
         * Go back through and read the type lists.
         */
        for (int i = 0; i < count; i++) {
            ProtoIdItem protoId = mProtoIds[i];

            int offset = protoId.parametersOff;

            if (offset == 0) {
                protoId.types = new int[0];
                continue;
            } else {
                seek(offset);
                int size = readInt();       // #of entries in list
                protoId.types = new int[size];

                for (int j = 0; j < size; j++) {
                    protoId.types[j] = readShort() & 0xffff;
                }
            }
        }
    }

    /**
     * Loads the field ID list.
     */
    void loadFieldIds() throws IOException {
        int count = mHeaderItem.fieldIdsSize;
        mFieldIds = new FieldIdItem[count];

        System.out.println("reading " + count + " fieldIds");
        seek(mHeaderItem.fieldIdsOff);
        for (int i = 0; i < count; i++) {
            mFieldIds[i] = new FieldIdItem();
            mFieldIds[i].classIdx = readShort() & 0xffff;
            mFieldIds[i].typeIdx = readShort() & 0xffff;
            mFieldIds[i].nameIdx = readInt();

//            System.out.println(i + ": " + mFieldIds[i].nameIdx +
//                    " " + mStrings[mFieldIds[i].nameIdx]);
        }
    }

    /**
     * Loads the method ID list.
     */
    void loadMethodIds() throws IOException {
        int count = mHeaderItem.methodIdsSize;
        mMethodIds = new MethodIdItem[count];

        //System.out.println("reading " + count + " methodIds");
        seek(mHeaderItem.methodIdsOff);
        for (int i = 0; i < count; i++) {
            mMethodIds[i] = new MethodIdItem();
            mMethodIds[i].classIdx = readShort() & 0xffff;
            mMethodIds[i].protoIdx = readShort() & 0xffff;
            mMethodIds[i].nameIdx = readInt();

            //System.out.println(i + ": " + mMethodIds[i].nameIdx +
            //    " " + mStrings[mMethodIds[i].nameIdx]);
        }
    }

    /**
     * Loads the class defs list.
     */
    void loadClassDefs() throws IOException {
        int count = mHeaderItem.classDefsSize;
        mClassDefs = new ClassDefItem[count];

        ClassDefItem dockCompatBuildConfig = null;

        //System.out.println("reading " + count + " classDefs");
        seek(mHeaderItem.classDefsOff);
        for (int i = 0; i < count; i++) {
            mClassDefs[i] = new ClassDefItem();
            mClassDefs[i].classIdx = readInt();

            mClassDefs[i].access_flags = readInt();
            mClassDefs[i].superclass_idx = readInt();
            mClassDefs[i].interfaces_off = readInt();
            mClassDefs[i].source_file_idx = readInt();
            mClassDefs[i].annotations_off = readInt();
            mClassDefs[i].class_data_off = readInt();
            mClassDefs[i].static_values_off = readInt();

            System.out.println(i + ": " + mClassDefs[i].classIdx + " " +
                    mStrings[mTypeIds[mClassDefs[i].classIdx].descriptorIdx] + " ; " +
                    "static_value_off: " + mClassDefs[i].static_values_off);

            if (mStrings[mTypeIds[mClassDefs[i].classIdx].descriptorIdx].contains("dock/compat/BuildConfig")) {
                /*System.out.println(i + ": " + mClassDefs[i].classIdx + " " +
                        mStrings[mTypeIds[mClassDefs[i].classIdx].descriptorIdx] + " ; " +
                        "static_value_off: " + mClassDefs[i].static_values_off);*/
                dockCompatBuildConfig = mClassDefs[i];
                break;
            }
        }

        if (dockCompatBuildConfig == null) {
            return;
        }

        seek(dockCompatBuildConfig.static_values_off);
        int staticFieldSize = readUnsignedLeb128();
        System.out.println("staticFieldSize: " + staticFieldSize);

        for (int i = 0; i < staticFieldSize; i++) {
            byte b = readByte();
            int type = b & 0x1f;
            int valueArgs = ((b >> 5) & 0x07);

            System.out.println("static type: " + type + "???valueArgs???" + valueArgs);
            if (type == 0x17) {//String
                byte[] tempBuff = new byte[valueArgs + 1];
                readBytes(tempBuff);
                int stringId = convertByteArrayToInt(tempBuff);
                System.out.println("stringId: " + stringId + "???" + "static string value: " + mStrings[stringId]);
                continue;
            }
            if (type == 0x04) { //int
                byte[] tempBuff = new byte[valueArgs + 1];
                readBytes(tempBuff);
                System.out.println("static int value: " + convertByteArrayToInt(tempBuff));
                continue;
            }
            if (type == 0x1f) { //boolean
                System.out.println("static boolean value: " + valueArgs);
            }
        }
    }

    private int convertByteArrayToInt(byte[] data) {
        int intValue = 0;
        for (int j = 0; j < data.length; j++) {
            System.out.print(" tempBuff byte???" + data[j]);
            intValue = intValue | ((data[j] & 0xff) << (j * 8));
        }
        System.out.println();
        System.out.println("convert byte array to int: " + intValue );
        return intValue;
    }

    /**
     * Sets the "internal" flag on type IDs which are defined in the
     * DEX file or within the VM (e.g. primitive classes and arrays).
     */
    void markInternalClasses() {
        for (int i = mClassDefs.length - 1; i >= 0; i--) {
            mTypeIds[mClassDefs[i].classIdx].internal = true;
        }

        for (int i = 0; i < mTypeIds.length; i++) {
            String className = mStrings[mTypeIds[i].descriptorIdx];

            if (className.length() == 1) {
                // primitive class
                mTypeIds[i].internal = true;
            } else if (className.charAt(0) == '[') {
                mTypeIds[i].internal = true;
            }

            //System.out.println(i + " " +
            //    (mTypeIds[i].internal ? "INTERNAL" : "external") + " - " +
            //    mStrings[mTypeIds[i].descriptorIdx]);
        }
    }


    /*
     * =======================================================================
     *      Queries
     * =======================================================================
     */

    /**
     * Returns the class name, given an index into the type_ids table.
     */
    private String classNameFromTypeIndex(int idx) {
        return mStrings[mTypeIds[idx].descriptorIdx];
    }

    /**
     * Returns an array of method argument type strings, given an index
     * into the proto_ids table.
     */
    private String[] argArrayFromProtoIndex(int idx) {
        ProtoIdItem protoId = mProtoIds[idx];
        String[] result = new String[protoId.types.length];

        for (int i = 0; i < protoId.types.length; i++) {
            result[i] = mStrings[mTypeIds[protoId.types[i]].descriptorIdx];
        }

        return result;
    }

    /**
     * Returns a string representing the method's return type, given an
     * index into the proto_ids table.
     */
    private String returnTypeFromProtoIndex(int idx) {
        ProtoIdItem protoId = mProtoIds[idx];
        return mStrings[mTypeIds[protoId.returnTypeIdx].descriptorIdx];
    }

    /**
     * Returns an array with all of the class references that don't
     * correspond to classes in the DEX file.  Each class reference has
     * a list of the referenced fields and methods associated with
     * that class.
     */
    public ClassRef[] getExternalReferences() {
        // create a sparse array of ClassRef that parallels mTypeIds
        ClassRef[] sparseRefs = new ClassRef[mTypeIds.length];

        // create entries for all externally-referenced classes
        int count = 0;
        for (int i = 0; i < mTypeIds.length; i++) {
            if (!mTypeIds[i].internal) {
                sparseRefs[i] =
                        new ClassRef(mStrings[mTypeIds[i].descriptorIdx]);
                count++;
            }
        }

        // add fields and methods to the appropriate class entry
        addExternalFieldReferences(sparseRefs);
        addExternalMethodReferences(sparseRefs);

        // crunch out the sparseness
        ClassRef[] classRefs = new ClassRef[count];
        int idx = 0;
        for (int i = 0; i < mTypeIds.length; i++) {
            if (sparseRefs[i] != null)
                classRefs[idx++] = sparseRefs[i];
        }

        assert idx == count;

        return classRefs;
    }

    /**
     * Runs through the list of field references, inserting external
     * references into the appropriate ClassRef.
     */
    private void addExternalFieldReferences(ClassRef[] sparseRefs) {
        for (int i = 0; i < mFieldIds.length; i++) {
            if (!mTypeIds[mFieldIds[i].classIdx].internal) {
                FieldIdItem fieldId = mFieldIds[i];
                FieldRef newFieldRef = new FieldRef(
                        classNameFromTypeIndex(fieldId.classIdx),
                        classNameFromTypeIndex(fieldId.typeIdx),
                        mStrings[fieldId.nameIdx]);
                sparseRefs[mFieldIds[i].classIdx].addField(newFieldRef);
            }
        }
    }

    /**
     * Runs through the list of method references, inserting external
     * references into the appropriate ClassRef.
     */
    private void addExternalMethodReferences(ClassRef[] sparseRefs) {
        for (int i = 0; i < mMethodIds.length; i++) {
            if (!mTypeIds[mMethodIds[i].classIdx].internal) {
                MethodIdItem methodId = mMethodIds[i];
                MethodRef newMethodRef = new MethodRef(
                        classNameFromTypeIndex(methodId.classIdx),
                        argArrayFromProtoIndex(methodId.protoIdx),
                        returnTypeFromProtoIndex(methodId.protoIdx),
                        mStrings[methodId.nameIdx]);
                sparseRefs[mMethodIds[i].classIdx].addMethod(newMethodRef);
            }
        }
    }

    /**
     * Returns the list of all method references.
     */
    public MethodRef[] getMethodRefs() {
        MethodRef[] methodRefs = new MethodRef[mMethodIds.length];
        for (int i = 0; i < mMethodIds.length; i++) {
            MethodIdItem methodId = mMethodIds[i];
            methodRefs[i] = new MethodRef(
                    classNameFromTypeIndex(methodId.classIdx),
                    argArrayFromProtoIndex(methodId.protoIdx),
                    returnTypeFromProtoIndex(methodId.protoIdx),
                    mStrings[methodId.nameIdx]);
        }
        return methodRefs;
    }

    /**
     * Returns the list of all field references.
     */
    public FieldRef[] getFieldRefs() {
        FieldRef[] fieldRefs = new FieldRef[mFieldIds.length];
        for (int i = 0; i < mFieldIds.length; i++) {
            FieldIdItem fieldId = mFieldIds[i];
            fieldRefs[i] = new FieldRef(
                    classNameFromTypeIndex(fieldId.classIdx),
                    classNameFromTypeIndex(fieldId.typeIdx),
                    mStrings[fieldId.nameIdx]);
        }
        return fieldRefs;
    }

    /*
     * =======================================================================
     *      Basic I/O functions
     * =======================================================================
     */

    /**
     * Seeks the DEX file to the specified absolute position.
     */
    void seek(int position) throws IOException {
        mDexFile.seek(position);
    }

    /**
     * Fills the buffer by reading bytes from the DEX file.
     */
    void readBytes(byte[] buffer) throws IOException {
        mDexFile.readFully(buffer);
    }

    /**
     * Reads a single signed byte value.
     */
    byte readByte() throws IOException {
        mDexFile.readFully(tmpBuf, 0, 1);
        return tmpBuf[0];
    }

    /**
     * Reads a signed 16-bit integer, byte-swapping if necessary.
     */
    short readShort() throws IOException {
        mDexFile.readFully(tmpBuf, 0, 2);
        if (isBigEndian) {
            return (short) ((tmpBuf[1] & 0xff) | ((tmpBuf[0] & 0xff) << 8));
        } else {
            return (short) ((tmpBuf[0] & 0xff) | ((tmpBuf[1] & 0xff) << 8));
        }
    }

    /**
     * Reads a signed 32-bit integer, byte-swapping if necessary.
     */
    int readInt() throws IOException {
        mDexFile.readFully(tmpBuf, 0, 4);

        if (isBigEndian) {
            return (tmpBuf[3] & 0xff) | ((tmpBuf[2] & 0xff) << 8) |
                    ((tmpBuf[1] & 0xff) << 16) | ((tmpBuf[0] & 0xff) << 24);
        } else {
            return (tmpBuf[0] & 0xff) | ((tmpBuf[1] & 0xff) << 8) |
                    ((tmpBuf[2] & 0xff) << 16) | ((tmpBuf[3] & 0xff) << 24);
        }
    }

    /**
     * Reads a variable-length unsigned LEB128 value.  Does not attempt to
     * verify that the value is valid.
     *
     * @throws IOException if we run off the end of the file
     */
    int readUnsignedLeb128() throws IOException {
        int result = 0;
        byte val;

        do {
            val = readByte();
            result = (result << 7) | (val & 0x7f);
        } while (val < 0);

        return result;
    }

    /**
     * Reads a UTF-8 string.
     * <p>
     * We don't know how long the UTF-8 string is, so we have to read one
     * byte at a time.  We could make an educated guess based on the
     * utf16_size and seek back if we get it wrong, but seeking backward
     * may cause the underlying implementation to reload I/O buffers.
     */
    String readString() throws IOException {
        int utf16len = readUnsignedLeb128();
        byte inBuf[] = new byte[utf16len * 3];      // worst case
        int idx;

        for (idx = 0; idx < inBuf.length; idx++) {
            byte val = readByte();
            if (val == 0)
                break;
            inBuf[idx] = val;
        }

        return new String(inBuf, 0, idx, "UTF-8");
    }


    /*
     * =======================================================================
     *      Internal "structure" declarations
     * =======================================================================
     */

    /**
     * Holds the contents of a header_item.
     */
    static class HeaderItem {
        public int fileSize;
        public int headerSize;
        public int endianTag;
        public int stringIdsSize, stringIdsOff;
        public int typeIdsSize, typeIdsOff;
        public int protoIdsSize, protoIdsOff;
        public int fieldIdsSize, fieldIdsOff;
        public int methodIdsSize, methodIdsOff;
        public int classDefsSize, classDefsOff;

        /* expected magic values */
        public static final byte[] DEX_FILE_MAGIC = {
                0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x36, 0x00};
        public static final byte[] DEX_FILE_MAGIC_API_13 = {
                0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x35, 0x00};
        public static final int ENDIAN_CONSTANT = 0x12345678;
        public static final int REVERSE_ENDIAN_CONSTANT = 0x78563412;
    }

    /**
     * Holds the contents of a type_id_item.
     * <p>
     * This is chiefly a list of indices into the string table.  We need
     * some additional bits of data, such as whether or not the type ID
     * represents a class defined in this DEX, so we use an object for
     * each instead of a simple integer.  (Could use a parallel array, but
     * since this is a desktop app it's not essential.)
     */
    static class TypeIdItem {
        public int descriptorIdx;       // index into string_ids

        public boolean internal;        // defined within this DEX file?
    }

    /**
     * Holds the contents of a proto_id_item.
     */
    static class ProtoIdItem {
        public int shortyIdx;           // index into string_ids
        public int returnTypeIdx;       // index into type_ids
        public int parametersOff;       // file offset to a type_list

        public int types[];             // contents of type list
    }

    /**
     * Holds the contents of a field_id_item.
     */
    static class FieldIdItem {
        public int classIdx;            // index into type_ids (defining class)
        public int typeIdx;             // index into type_ids (field type)
        public int nameIdx;             // index into string_ids
    }

    /**
     * Holds the contents of a method_id_item.
     */
    static class MethodIdItem {
        public int classIdx;            // index into type_ids
        public int protoIdx;            // index into proto_ids
        public int nameIdx;             // index into string_ids
    }

    /**
     * Holds the contents of a class_def_item.
     * <p>
     * We don't really need a class for this, but there's some stuff in
     * the class_def_item that we might want later.
     */
    static class ClassDefItem {
        public int classIdx;            // index into type_ids
        public int access_flags;
        public int superclass_idx;
        public int interfaces_off;
        public int source_file_idx;
        public int annotations_off;
        public int class_data_off;
        public int static_values_off;

        public ArrayList<String> staticValues;
    }
}

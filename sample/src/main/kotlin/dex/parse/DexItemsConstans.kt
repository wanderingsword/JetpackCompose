package dex.parse

const val UINT_TYPE_SIZE = 4


class ClassFieldValueType {
  companion object {
    val BYTE = 0x00
    val SHORT = 0x02
    val CHAR = 0x03
    val INT = 0x04
    val LONG = 0x06
    val FLOAT = 0x10
    val DOUBLE = 0x11
    val METHOD_TYPE = 0x15
    val METHOD_HANDLE = 0x16
    val STRING = 0x17
    val TYPE = 0x18
    val FIELD = 0x19
    val METHOD = 0x1a
    val ENUM = 0x1b
    val ARRAY = 0x1c
    val ANNOTATION = 0x1d
    val NULL = 0x1e
    val BOOLEAN = 0x1f
  }
}

class EncodedValue {
  var valueArgs = 0
  var valueType = 0
  var value: Any? = null
}

class EncodedAnnotation {
  var typeId = 0
  var size = 0
  var elements: Array<EncodedAnnotationElement>? = null
}

class EncodedAnnotationElement {
  var nameId = 0
  var value: EncodedValue? = null
}

class DexHeader {
  var magic: ByteArray = ByteArray(8)
  var fileSize: Int? = null
  var headerSize: Int? = null
  var endianTag: Int? = null
  var linkAreaSize: Int? = null
  var linkAreaOffset: Int? = null
  var mapAreaOffset: Int? = null
  var stringIdsSize: Int? = null
  var stringIdsOffset: Int = -1
  var typeIdsSize: Int? = null
  var typeIdsOffset: Int? = null
  var protoIdsSize: Int? = null
  var protoIdsOffset: Int? = null
  var fieldIdsSize: Int? = null
  var fieldIdsOffset: Int? = null
  var methodIdsSize: Int? = null
  var methodIdsOffset: Int? = null
  var classDefsSize: Int? = null
  var classDefsOffset: Int? = null
  var dataAreaSize: Int? = null
  var dataAreaOffset: Int? = null

  companion object {
    /**
     * Dex 相关介绍官方文档：https://source.android.google.cn/devices/tech/dalvik/dex-format
     */
    val magic_size = 8
    val checksum_size = UINT_TYPE_SIZE
    val signature_size = 20
    val file_size = UINT_TYPE_SIZE

    //头文件（整个区段）的大小，以字节为单位。默认值为 0x70
    val header_size = UINT_TYPE_SIZE

    //默认值为 ENDIAN_CONSTANT
    val endian_size = UINT_TYPE_SIZE
    val link_size = UINT_TYPE_SIZE
    val link_area_offset_size = UINT_TYPE_SIZE
    val map_area_offset_size = UINT_TYPE_SIZE
    val string_ids_size = UINT_TYPE_SIZE
    val string_ids_area_offset_size = UINT_TYPE_SIZE
    val type_ids_size = UINT_TYPE_SIZE
    val type_ids_area_offset_size = UINT_TYPE_SIZE
    val proto_ids_size = UINT_TYPE_SIZE
    val proto_ids_area_offset_size = UINT_TYPE_SIZE
    val field_ids_size = UINT_TYPE_SIZE
    val field_ids_area_offset_size = UINT_TYPE_SIZE
    val method_ids_size = UINT_TYPE_SIZE
    val method_ids_area_offset_size = UINT_TYPE_SIZE
    val data_area_size = UINT_TYPE_SIZE
    val data_area_offset_size = UINT_TYPE_SIZE

    val dexFileMagic = byteArrayOf(0x64, 0x65, 0x78, 0x0a, 0x30, 0x33, 0x39, 0x00)
    val reverseEndianConstant = 0x78563412
  }
}

class StringItem {
  var dataOffset = -1
  var data: String? = null

  override fun toString(): String {
    return "dataOffset: $dataOffset, data: $data"
  }
}

class ClassDefItem {
  var classDefOff = 0
  var staticValueOff = 0
  var packageName: String = ""
  var staticFieldMap: HashMap<String, FieldItem>? = null
  var instanceFieldSize = 0
  var directMethodSize = 0
  var virtualMethodSize = 0
}

class FieldItem {
  var className = ""
  var fieldName = ""
  var accessFlag = 0
  var type = ""
  var value: EncodedValue? = null
}



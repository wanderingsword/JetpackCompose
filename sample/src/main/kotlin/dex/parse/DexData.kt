package dex.parse

import apktool.xlcw.DEBUG
import apktool.xlcw.packageName
import apktool.xlcw.svnVersionCodeClass
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

class DexData {
  private var tempBuffer = ByteArray(4)
  private var isLittleEndian = true
  private var dexByteBuffer: ByteBuffer? = null
  private var dexFile: RandomAccessFile? = null

  private lateinit var stringPool: Array<StringItem?>
  private val dexHeader = DexHeader()
  private var fieldItemContainer: Array<FieldItem>? = null
  private var classDefDataMap: HashMap<String, ClassDefItem>? = null
  private var typeIdsPool: IntArray? = null

  constructor(dexPath: String) : this(RandomAccessFile(File(dexPath), "r"))

  constructor(dexFile: RandomAccessFile) {
    this.dexFile = dexFile
    init()
  }

  constructor(buffer: ByteBuffer) {
    this.dexByteBuffer = buffer.duplicate()
    dexByteBuffer?.order(ByteOrder.LITTLE_ENDIAN)
    init()
  }

  fun readSvnVersionCode(): String {
    //stringPool[convertByteArrayToInt(staticItem.value!!.value as ByteArray)]?.data
    if(classDefDataMap?.containsKey(svnVersionCodeClass) == false) {
      return ""
    }
    val stringID = convertByteArrayToInt(classDefDataMap?.get(svnVersionCodeClass)?.staticFieldMap?.get("B2_SVN_VERSION_CODE")?.value!!.value as ByteArray)
    println("svnVersionCode String ID: $stringID -- value: ${stringPool[stringID]?.data}")
    return stringPool[stringID]?.data ?: ""
  }

  fun readMethodRefsCount(): Int {
    return dexHeader.methodIdsSize ?: 0
  }

  private fun init() {
    initHeaderItem()
    initStringPool()
    initTypeId()
    initFieldIdPool()
    initClassDefData()
  }

  private fun initHeaderItem() {
    readBytes(dexHeader.magic)

    if (!verifyMagic(dexHeader.magic)) {
      return
    }

    seek(DexHeader.magic_size + DexHeader.checksum_size + DexHeader.signature_size + DexHeader.file_size + DexHeader.header_size)
    dexHeader.endianTag = readInt()
    if (dexHeader.endianTag == DexHeader.reverseEndianConstant) {
      isLittleEndian = false
    }

    seek(DexHeader.magic_size + DexHeader.checksum_size + DexHeader.signature_size)
    dexHeader.fileSize = readInt()
    dexHeader.headerSize = readInt()
    seekRelative(DexHeader.endian_size)
    dexHeader.linkAreaSize = readInt()
    dexHeader.linkAreaOffset = readInt()
    dexHeader.mapAreaOffset = readInt()
    dexHeader.stringIdsSize = readInt()
    dexHeader.stringIdsOffset = readInt()
    dexHeader.typeIdsSize = readInt()
    dexHeader.typeIdsOffset = readInt()
    dexHeader.protoIdsSize = readInt()
    dexHeader.protoIdsOffset = readInt()
    dexHeader.fieldIdsSize = readInt()
    dexHeader.fieldIdsOffset = readInt()
    dexHeader.methodIdsSize = readInt()
    dexHeader.methodIdsOffset = readInt()
    dexHeader.classDefsSize = readInt()
    dexHeader.classDefsOffset = readInt()
    dexHeader.dataAreaSize = readInt()
    dexHeader.dataAreaOffset = readInt()
  }

  private fun initStringPool(): Array<StringItem?> {
    seek(dexHeader.stringIdsOffset)

    if (dexHeader.stringIdsSize == null) {
      return emptyArray()
    }

    stringPool = Array(dexHeader.stringIdsSize!!, init = {
      val item = StringItem()
      item.dataOffset = readInt()
      item
    })

    println("---------------- start read String pool, stringIdsSize: ${dexHeader.stringIdsSize}")
    for (stringItem in stringPool) {
      stringItem?.data = readStringItem(stringItem?.dataOffset!!)
    }
    println("--------------- end read String pool")
    return stringPool
  }


  private fun initTypeId() {
    if (dexHeader.typeIdsSize == null || dexHeader.typeIdsOffset == null) {
      return
    }

    seek(dexHeader.typeIdsOffset!!)
    typeIdsPool = IntArray(dexHeader.typeIdsSize!!) {
      readInt()
    }
  }


  private fun initFieldIdPool() {
    if (dexHeader.fieldIdsSize == null || dexHeader.fieldIdsOffset == null) return

    seek(dexHeader.fieldIdsOffset!!)
    fieldItemContainer = Array(dexHeader.fieldIdsSize!!) {
      val item = FieldItem()
      item.className = stringPool[typeIdsPool?.get(readShort().toInt())!!]?.data.toString()
      item.type = stringPool[typeIdsPool?.get(readShort().toInt())!!]?.data.toString()
      item.fieldName = stringPool[readInt()]?.data.toString()
      item
    }
  }

  private fun verifyMagic(magic: ByteArray): Boolean {
    var count = 0
    for (i in magic.indices) {
      if (magic[i] == 0.toByte()) {
        count = i
        break
      }
    }
    println("magic---------- ${String(magic, 0, count, Charsets.UTF_8)}")
    return String(magic).startsWith("dex\n")
  }


  private fun initClassDefData() {
    if (dexHeader.classDefsSize == null) {
      return
    }

    seek(dexHeader.classDefsOffset!!)

    classDefDataMap = HashMap<String, ClassDefItem>(dexHeader.classDefsSize!!).apply {
      repeat(dexHeader.classDefsSize!!) {
        val classDefItem = ClassDefItem()
        val typeId = readInt()
        classDefItem.packageName = stringPool[typeIdsPool?.get(typeId)!!]?.data.toString()
        seekRelative(5 * UINT_TYPE_SIZE)
        classDefItem.classDefOff = readInt()
        classDefItem.staticValueOff = readInt()
        put(classDefItem.packageName, classDefItem)
      }
    }

    /*val classDefPools = Array(dexHeader.classDefsSize!!) {
      val classDefItem = ClassDefItem()
      val typeId = readInt()
      classDefItem.packageName = stringPool[typeIdsPool?.get(typeId)!!]?.data.toString()
      seekRelative(5 * UINT_TYPE_SIZE)
      classDefItem.classDefOff = readInt()
      classDefItem.staticValueOff = readInt()
      classDefItem
    }*/

    println("class size: " + classDefDataMap!!.size)
    classDefDataMap!!.values.forEach { classDefItem ->
      //classDefOff 为 0 ，表示没有此类数据（被标记为接口时是没有类数据的）
      if (classDefItem.classDefOff != 0) {
        seek(classDefItem.classDefOff)
        readClassDefItem(classDefItem)
      }
    }

    /*classDefPools.forEach { classDefItem ->
      //classDefOff 为 0 ，表示没有此类数据（被标记为接口时是没有类数据的）
      if (classDefItem.classDefOff != 0) {
        seek(classDefItem.classDefOff)
        readClassDefItem(classDefItem)
      }
    }*/
  }

  private fun readClassDefItem(classDefItem: ClassDefItem) {
    val staticFieldSize = readUnsignedLed128()
    if (DEBUG) println("read static field size: $staticFieldSize")
    classDefItem.instanceFieldSize = readUnsignedLed128()
    classDefItem.directMethodSize = readUnsignedLed128()
    classDefItem.virtualMethodSize = readUnsignedLed128()
    if(DEBUG) {
      println("classDefItem packageName: " + classDefItem.packageName + "--- classDefOffset: "
          + classDefItem.classDefOff + "--- staticValueOffset: " + classDefItem.staticValueOff
          + "--- staticSize: $staticFieldSize")
    }

    if (staticFieldSize != 0) {
      var fieldIdOffset = 0
      classDefItem.staticFieldMap = HashMap<String, FieldItem>(staticFieldSize).apply {
        for (i in 0 until staticFieldSize) {
          val fieldDiff = readUnsignedLed128()
          val accessFlag = readUnsignedLed128()

          fieldIdOffset += fieldDiff
          //println("fieldDiff: $fieldDiff ; fieldIdOffset: $fieldIdOffset")

          val fieldItem = fieldItemContainer?.get(fieldIdOffset)
          if (fieldItem != null) {
            fieldItem.accessFlag = accessFlag
            put(fieldItem.fieldName, fieldItem)
          }
        }
      }

      if (classDefItem.staticValueOff != 0 && classDefItem.packageName.endsWith("dock/compat/BuildConfig;")) {
        seek(classDefItem.staticValueOff)
        readStaticFields(classDefItem)
      }
    }
  }

  //读取类中所有的静态字段
  private fun readStaticFields(classDefItem: ClassDefItem) {
    readUnsignedLed128() //read static field size
    //println("static field size: ${readUnsignedLed128()}; staticContainerSize: ${classDefItem.staticFieldContainer?.size}")

    classDefItem.staticFieldMap?.values?.forEach { staticItem ->
      staticItem.value = readEncodeValue()
      if (staticItem.value!!.value is ByteArray)
        println("static item: " + staticItem.fieldName + " -- value: ${stringPool[convertByteArrayToInt(staticItem.value!!.value as ByteArray)]?.data}")
    }
  }

  private fun readEncodeArray(): Array<EncodedValue> {
    val arraySize = readUnsignedLed128()
    println("encoded array size: $arraySize")
    val valueArray = Array(arraySize) {
      readEncodeValue()
    }
    return valueArray
  }

  private fun readEncodeValue(): EncodedValue {
    val item = EncodedValue()
    val valueArgs = readByte()
    item.valueType = valueArgs.toInt() and 0x1f
    item.valueArgs = ((valueArgs.toInt() ushr 5) and 0x07)

    when (item.valueType) {
      ClassFieldValueType.BYTE -> {
        item.value = readByte()
      }
      ClassFieldValueType.ARRAY -> {  //encoded_array
        item.value = readEncodeArray()
      }
      ClassFieldValueType.ANNOTATION -> { //encoded_annotation
        item.value = readEncodeAnnotation()
      }
      ClassFieldValueType.NULL -> {}
      ClassFieldValueType.BOOLEAN -> {
        item.value = item.valueArgs
      }
      else -> {
        //println("value type: ${item.valueType} - valueArgs: ${item.valueArgs}")
        val buffer = ByteArray(item.valueArgs + 1);
        readBytes(buffer)
        item.value = buffer
      }
    }
    return item
  }

  //todo: 这个方法还有异常，暂时未用到
  private fun readEncodeAnnotation(): EncodedAnnotation {
    val encodedAnnotation = EncodedAnnotation()
    encodedAnnotation.typeId = readUnsignedLed128()
    encodedAnnotation.size = readUnsignedLed128()
    encodedAnnotation.elements = Array(encodedAnnotation.size) {
      val element = EncodedAnnotationElement()
      element.nameId = readUnsignedLed128()
      element.value = readEncodeValue()
      element
    }
    return encodedAnnotation
  }

  private fun readInt(): Int {
    if (dexByteBuffer != null) {
      return dexByteBuffer!!.int
    }

    dexFile?.readFully(tempBuffer, 0, 4)
    return convertByteArrayToInt(tempBuffer)
  }

  private fun readShort(): Short {
    if (dexByteBuffer != null) {
      return dexByteBuffer!!.short
    }

    dexFile?.readFully(tempBuffer, 0, 2)
    return convertByteArrayToInt(tempBuffer).toShort()
  }

  private fun convertByteArrayToInt(data: ByteArray): Int {
    var intValue = 0
    for ((index, byte) in data.withIndex()) {
      if (isLittleEndian) {
        intValue = intValue or ((byte.toInt() and 0xff) shl (index * 8))
      } else {
        intValue = intValue or ((byte.toInt() and 0xff) shl ((3 - index) * 8))
      }
    }
    return intValue
  }

  private fun readStringItem(dataOffset: Int): String {
    seek(dataOffset)
    val dataSize = readUnsignedLed128()  //字符串长度
    val data = readStringData2(dataSize)
    //println("dataOffset: $dataOffset, dataSize: $dataSize, stringData: $data")
    return data
  }

  private fun readStringData(strLen: Int): String {
    val charArray = CharArray(strLen)
    for (i in 0 until strLen) {
      val a = readUByte()
      if ((a and 0x80) == 0.toShort()) {
        charArray[i] = a.toInt().toChar()
        println("$a")
      } else if ((a and 0xe0) == 0xc0.toShort()) {
        val b = readUByte()
        println("$a $b")
        charArray[i] = (((a and 0x1F).toInt() shl 6) or ((b and 0x3F).toInt())).toChar()
      } else if ((a and 0xf0) == 0xe0.toShort()) {
        val b = readUByte()
        val c = readUByte()
        println("$a $b $c")
        charArray[i] = (((a and 0x0F).toInt() shl 12) or ((b and 0x3F).toInt() shl 6) or (c and 0x3F).toInt()).toChar()
      }
    }
    return String(charArray)
  }

  private fun readStringData2(strLen: Int): String {
    val b = ByteArray(strLen * 3)
    var count = 0
    for (i in b.indices) {
      val d = readByte()
      if (d == 0.toByte()) {
        count = i
        break
      }
      b[i] = d
    }
    return String(b, 0, count, Charsets.UTF_8)
  }

  private fun readBytes(size: Int): ByteArray {
    val temp = ByteArray(size)
    readBytes(temp)
    return temp
  }

  private fun readBytes(buffer: ByteArray) {
    if (dexByteBuffer != null) {
      dexByteBuffer!!.get(buffer, 0, buffer.size)
      return
    }
    dexFile?.readFully(buffer)
  }

  private fun readUnsignedLed128(): Int {
    var result = 0
    var b: Byte
    var count = 0

    do {
      //一个字节一个字节的读，从内存中读字节，先读出来的是高地址的字节，Little_Endian 模式下就是高位字节。
      b = readByte()
      //print("unsign byte: $b - ")
      result = result or (((b.toInt() and 0x7f) shl (7 * count)))
      count++
    } while (b < 0)
    //println()
    return result
  }

  private fun readByte(): Byte {
    if (dexByteBuffer != null) {
      return dexByteBuffer!!.get() and 0xff.toByte()
    }

    dexFile?.readFully(tempBuffer, 0, 1)
    return tempBuffer[0]
  }

  private fun readUByte(): Short {
    if (dexByteBuffer != null) {
//      println("readUByte, buffer position: ${dexByteBuffer!!.position()}")
      return (dexByteBuffer!!.get().toShort() and 0xff)
    }
    return 0
  }

  private fun seek(position: Int) {
    if (dexByteBuffer != null) {
      dexByteBuffer!!.position(position)
      return
    }
    dexFile?.seek(position.toLong())
  }

  private fun seekRelative(count: Int) {
    if (dexByteBuffer != null) {
      seek(dexByteBuffer!!.position() + count)
    }
  }

}
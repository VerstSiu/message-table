/*
 *
 *  Copyright(c) 2019 VerstSiu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.ijoic.messagetable

import java.lang.reflect.Field
import kotlin.math.max

/**
 * Message table
 *
 * @author verstsiu created at 2019-08-16 18:05
 */
class MessageTable<DATA: Any>(
  vararg infoItems: ColumnInfo<DATA>) {

  private val infoItems = infoItems.toList()
  private val dataItems = mutableListOf<DATA>()

  /**
   * Add data [item]
   */
  fun addItem(item: DATA) {
    dataItems.add(item)
  }

  /**
   * Add data [items]
   */
  fun addItems(items: Collection<DATA>) {
    dataItems.addAll(items)
  }

  /**
   * Print table contents
   */
  fun print(manager: PrintManager = DefaultPrintManager) {
    val items = this.dataItems.toList()
    val firstItem = items.firstOrNull() ?: return
    val infoItems = getInfoItems(firstItem)
    val columns = infoItems.map { MessageColumn(it) }

    items.forEach { data ->
      columns.forEach { it.values.add(it.info.getValue(data).orEmpty()) }
    }

    // measure max column with
    columns.forEach { column ->
      val columnWith = column.values.maxBy { it.length }?.length

      if (columnWith != null) {
        column.columnWidth = max(columnWith, column.columnWidth)
      }
    }

    // print title
    manager.onPrintTableTitle(
      columns.map {
        fillSpace(it.info.name.capitalize(), it.columnWidth, it.info.isAlignLeft)
      }
    )

    // print columns
    for (i in 0 until items.size) {
      manager.onPrintTableContent(
        columns.map {
          fillSpace(it.values[i], it.columnWidth, it.info.isAlignLeft)
        }
      )
    }
  }

  private fun fillSpace(value: String, spaceWith: Int, isAlignLeft: Boolean) = when {
    isAlignLeft -> value.padEnd(spaceWith)
    else -> value.padStart(spaceWith)
  }

  /**
   * Reset table contents
   */
  fun reset() {
    dataItems.clear()
  }

  /* -- default column info :begin -- */

  private var defaultInfoItems: List<ColumnInfo<DATA>>? = null

  private fun getInfoItems(item: DATA): List<ColumnInfo<DATA>> {
    val infoItems = this.infoItems

    if (!infoItems.isNullOrEmpty()) {
      return infoItems
    }
    return getDefaultInfoItems(item)
  }

  private fun getDefaultInfoItems(item: DATA): List<ColumnInfo<DATA>> {
    val oldInfoItems = defaultInfoItems

    if (!oldInfoItems.isNullOrEmpty()) {
      return oldInfoItems
    }
    val fields = item::class.java.declaredFields
    val infoItems =  fields.mapNotNull { field ->
      getFieldReader(field, item)?.let { ColumnInfo(getDisplayFieldName(field, item), !isPrimitiveType(field.type), it) }
    }
    defaultInfoItems = infoItems
    return infoItems
  }

  private fun getDisplayFieldName(field: Field, item: DATA): String {
    // try access field value directly
    val annFieldName = field.getAnnotation(PrintColumn::class.java)?.name

    if (!annFieldName.isNullOrEmpty()) {
      return annFieldName
    }

    // try access field value with get method
    try {
      val fieldMethod = item::class.java.getDeclaredMethod("get${field.name.capitalize()}")

      if (fieldMethod != null) {
        val annMethodFiledName = fieldMethod.getAnnotation(PrintColumn::class.java)?.name

        if (!annMethodFiledName.isNullOrEmpty()) {
          return annMethodFiledName
        }
      }

    } catch (e: Exception) {
      // ignore check error
    }
    return field.name
  }

  private fun getFieldReader(field: Field, item: DATA): ((DATA) -> String?)? {
    // try access field value directly
    try {
      field.get(item)
      return { field.get(it)?.toString() }

    } catch (e: Exception) {
      // ignore check error
    }

    // try access field value with get method
    try {
      val fieldMethod = item::class.java.getDeclaredMethod("get${field.name.capitalize()}")

      if (fieldMethod != null) {
        fieldMethod.invoke(item)
        return { fieldMethod.invoke(it)?.toString() }
      }

    } catch (e: Exception) {
      // ignore check error
    }
    return null
  }

  /* -- default column info :end -- */

  /* -- print manager :begin -- */

  /**
   * Print manager
   */
  interface PrintManager {
    /**
     * Print table title with [columns]
     */
    fun onPrintTableTitle(columns: List<String>)

    /**
     * Print table conent with [columns]
     */
    fun onPrintTableContent(columns: List<String>)
  }

  /**
   * Default print manager
   */
  private object DefaultPrintManager: PrintManager {
    override fun onPrintTableTitle(columns: List<String>) {
      println(columns.joinToString(" \t"))
    }

    override fun onPrintTableContent(columns: List<String>) {
      println(columns.joinToString(" \t"))
    }
  }

  /* -- print manager :end -- */

  private fun isPrimitiveType(type: Class<*>): Boolean {
    return type.isPrimitive || Number::class.java.isAssignableFrom(type) ||
        type == Boolean::class.java || type == Char::class.java
  }

  /**
   * Message column
   */
  private data class MessageColumn<DATA: Any>(
    val info: ColumnInfo<DATA>,
    val values: MutableList<String> = mutableListOf(),
    var columnWidth: Int = info.name.length
  )

}
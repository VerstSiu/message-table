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

import org.junit.Test

/**
 * Message table test
 *
 * @author verstsiu created at 2019-08-16 18:05
 */
class MessageTableTest {
  @Test
  fun testSimple() {
    val table = MessageTable<User>()
    table.addItem(User("Tom", 11, 11))
    table.addItem(User("Janey", 22))
    table.print()
  }

  @Test
  fun testItemsReset() {
    val table = MessageTable<User>()
    table.addItem(User("Tom", 11, 11))
    table.addItem(User("Janey", 22))
    table.print()

    table.reset()
    table.addItems(
      listOf(
        User("Jimmy", 21)
      )
    )
    table.print()
  }

  @Test
  fun testCustomColumnInfo() {
    val table = MessageTable<User>(
      ColumnInfo("name") { it.name },
      ColumnInfo("info", isAlignLeft = false) { "${it.age}|${it.salary}" }
    )

    table.addItem(User("Tom", 11, 11))
    table.addItem(User("Janey", 22))
    table.print()
  }

  data class User(
    val name: String,
    val age: Int,
    val salary: Int? = null
  )
}

# Message Table

## Get Start

* Add it in your root build.gradle at the end of repositories:

    ```gradle
    allprojects {
        repositories {
          ...
          maven { url 'https://jitpack.io' }
        }
    }
    ```

* Add the dependency:

    ```gradle
    dependencies {
    }
    ```

## Usage

1. Print table message with POJO:

    ```kotlin
    val table = MessageTable<User>()

    table.addItem(User("Tom", 11))
    table.addItem(User("Jimmy", 22))

    table.print()
    // Name  Age
    // Tom    11
    // Jimmy  22
    ``` 

2. Print table message with customized column info:

    ```kotlin
    val table = MessageTable<User>(
      ColumnInfo("name", isAlignLeft = true) { it },
      ColumnInfo("age", isAlignLeft = false) { it.toString() }
    )

    table.addItem(User("Tom", 11))
    table.addItem(User("Jimmy", 22))

    table.print()
    // Name  Age
    // Tom    11
    // Jimmy  22
    ```

## License

```

   Copyright(c) 2019 VerstSiu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```
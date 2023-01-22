# DotaInfo

This is sample app which demonstrates usage of modern Android & Kotlin libraries based on clean architecutre principles using multi module architecture.


# Tech stack & Open-source libraries
 - Minimum SDK level 23
 - Kotlin based, Coroutines + Flow for asynchronous
 - Ktor: Construct the REST APIs + Serialization
 - SQLDelight: Typesafe Kotlin APIs for SQL database
 - Coil: An image loading library backed by Kotlin Coroutines
- Jetpack
  - Lifecycle: Observe Android lifecycles and handle UI states upon the lifecycle changes
  - ViewModel: Manages UI-related stuff, data holder and lifecycle aware. Allows data to survive configuration changes such as screen rotations
  - Compose: Toolkit for building native UI
  - Hilt for dependency injection
  
  
List screen            |  Details screen
:-------------------------:|:-------------------------:
![](https://github.com/MatijaSokol/DotaInfo/blob/master/previews/screenshot_list.jpg)  |  ![](https://github.com/MatijaSokol/DotaInfo/blob/master/previews/screenshot_details.jpg)
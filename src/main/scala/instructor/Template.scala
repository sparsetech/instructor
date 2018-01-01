package instructor

import java.io.File

case class Template[T <: Singleton](tag         : pine.Tag[Singleton],
                                    absolutePath: File,
                                    assets      : List[String])

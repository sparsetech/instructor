package instructor

import java.time.LocalDate

case class Meta(created    : LocalDate,
                title      : String,
                author     : String,
                affiliation: String,
                `abstract` : String,

                /* Language code, e.g. en-GB */
                // TODO Enforce correct format when parsed
                language   : String
               )
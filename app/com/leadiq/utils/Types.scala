package com.leadiq.utils

import java.io.File

object Types {

  type PossibleFile = Either[String, File]

  type PossibleResponse = Either[String, String]

  type ReturnDate = String
}

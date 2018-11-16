package com.leadiq.utils

import java.time._

import com.leadiq.utils.Types.ReturnDate

object Dates {

  def getDateWithTime: ReturnDate = ZonedDateTime.now(ZoneId.of("GMT")).toString


}

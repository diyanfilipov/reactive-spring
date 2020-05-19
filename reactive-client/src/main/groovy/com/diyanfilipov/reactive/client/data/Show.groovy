package com.diyanfilipov.reactive.client.data

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
@JsonIgnoreProperties(ignoreUnknown = true)
class Show  {

  Show() {}

  String id
  String name

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
  Date showDate
}

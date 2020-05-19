package com.diyanfilipov.reactive.client.data


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.ToString

@ToString(includePackage = false, includeNames = true)
@JsonIgnoreProperties(ignoreUnknown = true)
class Message {

  String message
  String fromName
}

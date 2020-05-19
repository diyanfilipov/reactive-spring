package com.diyanfilipov.reactive.controller

import com.diyanfilipov.reactive.dao.Show
import com.diyanfilipov.reactive.model.ShowRequest
import com.diyanfilipov.reactive.service.ShowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/show")
class ShowController {

  @Autowired
  ShowService showService

  @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
  Show createShow(@RequestBody ShowRequest showRequest) {
    showService.createShow(showRequest)
  }

  @PutMapping
  Show updateShow(@RequestBody Show show) {
    showService.updateShow(show)
  }

  @GetMapping("/list")
  List<Show> findAllShows() {
    showService.findAllShows()
  }

  @GetMapping("/{showId}")
  Show getShow(@PathVariable("showId") String showId) {
    showService.findShow(showId)
  }
}

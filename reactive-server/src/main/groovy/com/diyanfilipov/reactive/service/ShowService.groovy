package com.diyanfilipov.reactive.service

import com.diyanfilipov.reactive.dao.Show
import com.diyanfilipov.reactive.model.ShowRequest
import com.diyanfilipov.reactive.repository.ShowRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.FluxProcessor

@Service
class ShowService {

  @Autowired(required = false)
  ShowRepository showRepository

  @Autowired
  FluxProcessor<Show, Show> createdShowPublisher

  @Transactional(readOnly = false)
  Show createShow(ShowRequest showRequest) {
    Show savedShow = showRepository.save(new Show(name: showRequest.name, showDate: showRequest.showDate))
    createdShowPublisher.onNext(savedShow)
    savedShow
  }

  @Transactional(readOnly = false)
  Show updateShow(Show show) {

    Optional<Show> existingShowOptional = showRepository.findById(show?.id)

    existingShowOptional.orElseThrow({ ->
      throw new IllegalStateException("Show with id [${show?.id}] not found.")
    })

    Show existingShow = existingShowOptional.get()

    existingShow.with {
      name = show.name ?: name
      showDate = show.showDate ?: showDate
    }

    showRepository.save(existingShow)
  }

  @Transactional
  Optional<Show> findShow(String id) {
    showRepository.findById(id)
  }

  @Transactional
  List<Show> findAllShows() {
    showRepository.findAll()
  }
}

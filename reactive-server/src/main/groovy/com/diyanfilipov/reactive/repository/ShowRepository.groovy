package com.diyanfilipov.reactive.repository

import com.diyanfilipov.reactive.dao.Show
import org.springframework.data.jpa.repository.JpaRepository

interface ShowRepository extends JpaRepository<Show, String> {

}
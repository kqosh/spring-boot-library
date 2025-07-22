//package com.jdriven.library.service
//
//import com.jdriven.library.access.model.BookEntity
//import jakarta.persistence.EntityManager
//import org.hibernate.search.mapper.orm.Search
//import org.hibernate.search.mapper.orm.massindexing.MassIndexer
//import org.hibernate.search.mapper.orm.session.SearchSession
//import org.slf4j.LoggerFactory
//import org.springframework.boot.CommandLineRunner
//import org.springframework.stereotype.Component
//
////@Componentqqqq
//class InitialIndexer(private val entityManager: EntityManager) : CommandLineRunner {
//
//    private val logger = LoggerFactory.getLogger(this::class.java)
//
//    // Deze methode wordt automatisch uitgevoerd bij het opstarten
//    override fun run(vararg args: String?) {
//        logger.info("args: ${args.joinToString()}")
//
////        if (args.contains("init-index")) {
//            logger.info("Start indexing...")
//            println("Database is leeg, standaard admin-gebruiker wordt aangemaakt.")
//            init()
//            logger.info("Finished indexing.")
////        }
//    }
//
//    private fun init() {
//        val searchSession: SearchSession = Search.session(entityManager)
//        val indexer: MassIndexer = searchSession.massIndexer(BookEntity::class.java).threadsToLoadObjects(7);
//        indexer.startAndWait();
//    }
//}
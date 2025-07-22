package com.jdriven.library.access.model

import org.apache.lucene.analysis.core.LowerCaseFilterFactory
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer


//// Deze klasse wordt door Hibernate Search gevonden bij het opstarten qqqq eng
////@SearchExtension
//class MyAnalysisConfigurer : LuceneAnalysisConfigurer {
//    override fun configure(context: LuceneAnalysisConfigurationContext) {
//        context.analyzer("ngram_analyzer").custom()
//            .tokenizer(StandardTokenizerFactory::class.java)
//            .tokenFilter(LowerCaseFilterFactory::class.java)
//            // Breek woorden op in stukjes van minimaal 3 en maximaal 10 karakters
////            .tokenFilter(NGramFilterFactory::class.java) {
////                it.param("minGramSize", "3")
////                it.param("maxGramSize", "10")
////            }qqqq
//    }
//}
//
//class MyAnalysisConfigurer : LuceneAnalysisConfigurer {
//    override fun configure(context: LuceneAnalysisConfigurationContext) {
//        context.analyzer("autocomplete_indexing").custom()
//            .tokenizer(WhitespaceTokenizerFactory::class.java) // Lowercase all characters
//            .tokenFilter(LowerCaseFilterFactory::class.java) // Replace accented characters by their simpler counterpart (è => e, etc.)
//            .tokenFilter(ASCIIFoldingFilterFactory::class.java) // Generate prefix tokens
//            .tokenFilter(EdgeNGramFilterFactory::class.java)
//            .param("minGramSize", "1")
//            .param("maxGramSize", "10")
//        // Same as "autocomplete-indexing", but without the edge-ngram filter
//        context.analyzer("autocomplete_search").custom()
//            .tokenizer(WhitespaceTokenizerFactory::class.java) // Lowercase all characters
//            .tokenFilter(LowerCaseFilterFactory::class.java) // Replace accented characters by their simpler counterpart (è => e, etc.)
//            .tokenFilter(ASCIIFoldingFilterFactory::class.java)
//    }
//}
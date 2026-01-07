package com.pegai.app.data.data.repository

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.Product
import kotlinx.coroutines.tasks.await

object ProductRepository {

    private val cache = mutableMapOf<String, Product>()

    suspend fun getProdutosPorDono(donoId: String): List<Product> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("products")
            .whereEqualTo("donoId", donoId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(Product::class.java)?.copy(
                pid = doc.id
            )
        }
    }

    suspend fun getQuantidadeProdutosPorDono(donoId: String): Int {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("products")
            .whereEqualTo("donoId", donoId)

        val snapshot = query.count().get(AggregateSource.SERVER).await()

        return snapshot.count.toInt()
    }

    fun salvarNoCache(produtos: List<Product>) {
        produtos.forEach { product ->
            cache[product.pid] = product
        }
    }

    fun getProdutoPorId(id: String): Product? {
        return cache[id]
    }
}


